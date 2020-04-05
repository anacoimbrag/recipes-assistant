package com.anacoimbra.android.recipes.app

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.*
import com.anacoimbra.android.recipes.R
import com.anacoimbra.android.recipes.helpers.FirestoreManager
import com.anacoimbra.android.recipes.helpers.UriManager
import com.anacoimbra.android.recipes.model.Recipe
import com.anacoimbra.android.recipes.model.Resource
import com.anacoimbra.android.recipes.model.toRecipe
import com.anacoimbra.android.recipes.ui.activity.MainActivity
import com.anacoimbra.android.recipes.ui.activity.RecipeDetailActivity
import com.bumptech.glide.Glide

class RecipeSliceProvider : SliceProvider() {

    private val recipes: MutableList<Resource<Recipe>> = mutableListOf()

    override fun onCreateSliceProvider() = true

    override fun onMapIntentToUri(intent: Intent?): Uri {
        val uriBuilder: Uri.Builder = Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
        return intent?.data ?: uriBuilder.build()
    }

    override fun onBindSlice(sliceUri: Uri): Slice? {
        val context = context ?: return null
        val id = sliceUri.getQueryParameter(UriManager.RECIPE_PARAM)
        val resource = getResource(id)
        return if (sliceUri.path == UriManager.DETAILS_PATH) {
            when (resource) {
                is Resource.Success -> createContentSlice(context, sliceUri, resource.data)
                is Resource.Error -> createErrorSlice(context, sliceUri)
                else -> createLoadingSlice(context, sliceUri)
            }

        } else {
            createErrorSlice(context, sliceUri)
        }
    }

    override fun onSlicePinned(sliceUri: Uri?) {
        sliceUri ?: return
        val id = sliceUri.getQueryParameter(UriManager.RECIPE_PARAM)
        FirestoreManager.getRecipeById(id) { task ->
            if (task.isSuccessful && task.result?.exists() == true) {
                val doc = task.result
                recipes.add(Resource.Success(doc?.data?.toRecipe(doc.id)))
                context?.contentResolver?.notifyChange(sliceUri, null)
            } else {
                recipes.add(Resource.Error(id))
                task.exception?.printStackTrace()
                context?.contentResolver?.notifyChange(sliceUri, null)
            }
        }
    }

    override fun onSliceUnpinned(sliceUri: Uri?) {
        super.onSliceUnpinned(sliceUri)
        recipes.clear()
    }

    private fun createLoadingSlice(context: Context, sliceUri: Uri) =
        list(context, sliceUri, ListBuilder.INFINITY) {
            header {
                setTitle(context.getString(R.string.loading_recipe), true)
            }
        }

    private fun createContentSlice(context: Context, sliceUri: Uri, recipe: Recipe?) =
        if (recipe != null)
            list(context, sliceUri, ListBuilder.INFINITY) {
                header {
                    setTitle(recipe.name.orEmpty(), false)
                    subtitle = recipe.baseIngredients?.joinToString().orEmpty()
                    summary = recipe.baseIngredients?.joinToString().orEmpty()
                    primaryAction = createDetailAction(recipe)
                }
                gridRow {
                    cell {
                        val bitmap = Glide.with(context)
                            .asBitmap()
                            .load(recipe.image)
                            .submit()
                            .get()
                        val image =
                            IconCompat.createWithBitmap(bitmap)
                        addImage(image, ListBuilder.LARGE_IMAGE)
                    }
                }
                row {
                    title = context.getString(R.string.directions)
                    subtitle = recipe.directions.orEmpty()
                }
            } else createErrorSlice(context, sliceUri)

    private fun createErrorSlice(context: Context, sliceUri: Uri) =
        list(context, sliceUri, ListBuilder.INFINITY) {
            header {
                title = context.getString(R.string.error_recipe_not_found)
                primaryAction = createAppAction()
            }
        }

    private fun createDetailAction(recipe: Recipe?) =
        createAction(RecipeDetailActivity.intent(context, recipe))

    private fun createAppAction() = createAction(Intent(context, MainActivity::class.java))

    private fun createAction(intent: Intent?): SliceAction =
        SliceAction.create(
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            ),
            IconCompat.createWithResource(
                context,
                R.drawable.ic_launcher_foreground
            ),
            ListBuilder.ICON_IMAGE,
            context?.getString(R.string.open_app).orEmpty()
        )

    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun getResource(id: String?) =
        recipes.find {
            it is Resource.Success<Recipe> && it.data?.id == id || it is Resource.Error && it.id == id
        }
}
