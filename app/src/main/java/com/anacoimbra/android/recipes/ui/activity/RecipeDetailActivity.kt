package com.anacoimbra.android.recipes.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anacoimbra.android.recipes.R
import com.anacoimbra.android.recipes.helpers.FirestoreManager
import com.anacoimbra.android.recipes.helpers.UriManager
import com.anacoimbra.android.recipes.model.Recipe
import com.anacoimbra.android.recipes.model.toRecipe
import com.bumptech.glide.Glide
import com.google.firebase.appindexing.Action
import com.google.firebase.appindexing.FirebaseAppIndex
import com.google.firebase.appindexing.FirebaseUserActions
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import kotlinx.android.synthetic.main.content_recipe_detail.*


class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var recipeView: Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)
        setSupportActionBar(toolbar)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onStop() {
        if (::recipeView.isInitialized)
            FirebaseUserActions.getInstance().end(recipeView)
        super.onStop()
    }

    private fun handleIntent(intent: Intent?) {
        val name = intent?.data?.getQueryParameter(UriManager.RECIPE_PARAM)

        if (!name.isNullOrEmpty()) {
            FirestoreManager.getRecipeById(name) { task ->
                if (task.isSuccessful) {
                    val doc = task.result
                    val recipe = doc?.data?.toRecipe(doc.id) ?: return@getRecipeById
                    setupRecipe(recipe)
                    indexRecipe(recipe, intent)
                } else task.exception?.printStackTrace()
            }
        } else {
            val recipe = intent?.getParcelableExtra<Recipe>(
                UriManager.RECIPE_PARAM
            ) ?: return
            setupRecipe(recipe)
            indexRecipe(recipe, intent)
        }
    }

    private fun setupRecipe(recipe: Recipe) {
        toolbarLayout.title = recipe.name

        Glide.with(this)
            .load(recipe.image)
            .centerCrop()
            .into(recipeImage)

        recipeIngredients.text = recipe.ingredients
        recipeDirections.text = recipe.directions
    }

    private fun indexRecipe(recipe: Recipe?, intent: Intent?) {
        val indexable = recipe?.toIndexable(this) ?: return
        FirebaseAppIndex.getInstance().update(indexable)
        recipeView = Action.Builder(Action.Builder.VIEW_ACTION)
            .setName(recipe.name.orEmpty())
            .setObject(recipe.name.orEmpty(), recipe.getUrl())
            .setMetadata(Action.Metadata.Builder().setUpload(false))
            .build()
        FirebaseUserActions.getInstance().start(recipeView)
    }

    companion object {
        fun intent(context: Context?, recipe: Recipe?) =
            Intent(context, RecipeDetailActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra(UriManager.RECIPE_PARAM, recipe)
            }

        fun start(context: Context, recipe: Recipe?) =
            context.startActivity(intent(context, recipe))
    }
}
