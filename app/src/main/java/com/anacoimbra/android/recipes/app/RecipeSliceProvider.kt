package com.anacoimbra.android.recipes.app

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.*
import com.anacoimbra.android.recipes.BuildConfig
import com.anacoimbra.android.recipes.R
import com.anacoimbra.android.recipes.helpers.FirestoreManager
import com.anacoimbra.android.recipes.helpers.UriManager
import com.anacoimbra.android.recipes.model.Recipe
import com.anacoimbra.android.recipes.model.toRecipe
import com.anacoimbra.android.recipes.ui.activity.RecipeDetailActivity
import com.bumptech.glide.Glide

class RecipeSliceProvider : SliceProvider() {
    /**
     * Instantiate any required objects. Return true if the provider was successfully created,
     * false otherwise.
     */
    override fun onCreateSliceProvider(): Boolean {
        return true
    }

    /**
     * Converts URL to content URI (i.e. content://com.anacoimbra.android.recipes.dev...)
     */
    override fun onMapIntentToUri(intent: Intent?): Uri {
        // Note: implementing this is only required if you plan on catching URL requests.
        // This is an example solution.
        var uriBuilder: Uri.Builder = Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
        if (intent == null) return uriBuilder.build()
        val data = intent.data
        val dataPath = data?.path
        if (data != null && dataPath != null) {
            val path = dataPath.replace("/", "")
            uriBuilder = uriBuilder.path(path)
        }
        val context = context
        if (context != null) {
            uriBuilder = uriBuilder.authority(context.packageName)
        }
        return uriBuilder.build()
    }

    /**
     * Construct the Slice and bind data if available.
     */
    override fun onBindSlice(sliceUri: Uri): Slice? {
        val context = context ?: return null
        val activityAction = createActivityAction()
        return if (sliceUri.path == UriManager.DETAILS_PATH) {
            list(context, sliceUri, ListBuilder.INFINITY) {
                header {
                    if (recipe != null) {
                        setTitle(recipe?.name.orEmpty(), false)
                        subtitle = recipe?.baseIngredients?.joinToString().orEmpty()
                        primaryAction = activityAction
                    } else {
                        setTitle(context.getString(R.string.loading_recipe), true)
                    }
                }
                gridRow {
                    cell {
                        if (recipe != null) {
                            val bitmap = Glide.with(context)
                                .asBitmap()
                                .load(recipe?.image)
                                .submit()
                                .get()
                            val image =
                                IconCompat.createWithBitmap(bitmap)
                            addImage(image, ListBuilder.LARGE_IMAGE)
                        }
                    }
                }
                row {
                    if (recipe != null) {
                        title = context.getString(R.string.directions)
                        subtitle = recipe?.directions.orEmpty()
                    } else {
                        setTitle(null, true)
                        setSubtitle(null, true)
                    }
                }
            }
        } else {
            ListBuilder(context, sliceUri, ListBuilder.INFINITY)
                .addRow(
                    ListBuilder.RowBuilder()
                        .setTitle(context.getString(R.string.error_recipe_not_found))
                        .setPrimaryAction(activityAction)
                )
                .build()
        }
    }

    private fun createActivityAction(): SliceAction {
        return SliceAction.create(
            PendingIntent.getActivity(
                context,
                0,
                RecipeDetailActivity.intent(
                    context,
                    recipe
                ),
                PendingIntent.FLAG_UPDATE_CURRENT
            ),
            IconCompat.createWithResource(
                context,
                R.drawable.ic_launcher_foreground
            ),
            ListBuilder.ICON_IMAGE,
            context?.getString(R.string.open_app).orEmpty()
        )
    }

    /**
     * Slice has been pinned to external process. Subscribe to data source if necessary.
     */
    override fun onSlicePinned(sliceUri: Uri?) {
        sliceUri ?: return
        FirestoreManager.getRecipeByName(
            sliceUri.getQueryParameter(
                UriManager.RECIPE_PARAM
            )
        ) { query, exception ->
            if (exception == null) {
                val doc = query?.documents?.firstOrNull()
                recipe =
                    doc?.data?.toRecipe(doc.id)
                context?.contentResolver?.notifyChange(sliceUri, null)
            } else {
                exception.printStackTrace()
                context?.contentResolver?.notifyChange(
                    Uri.parse("content://${BuildConfig.APPLICATION_ID}/error"),
                    null
                )
            }
        }
    }

    companion object {
        private var recipe: Recipe? = null
    }
}
