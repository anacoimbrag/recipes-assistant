package com.anacoimbra.android.recipes.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anacoimbra.android.recipes.*
import com.anacoimbra.android.recipes.helpers.FirestoreManager
import com.anacoimbra.android.recipes.helpers.UriManager
import com.anacoimbra.android.recipes.model.Recipe
import com.anacoimbra.android.recipes.model.toRecipe
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import kotlinx.android.synthetic.main.content_recipe_detail.*

class RecipeDetailActivity : AppCompatActivity() {

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

    private fun handleIntent(intent: Intent?) {
        val name = intent?.getStringExtra(UriManager.RECIPE_PARAM)

        if (!name.isNullOrEmpty()) {
            FirestoreManager.getRecipeByName(name) { query, exception ->
                if (exception == null) {
                    val doc = query?.documents?.firstOrNull()
                    val recipe = doc?.data?.toRecipe(doc.id) ?: return@getRecipeByName
                    setupRecipe(recipe)
                } else exception.printStackTrace()
            }
        } else {
            val recipe = intent?.getParcelableExtra<Recipe>(
                UriManager.RECIPE_PARAM
            ) ?: return
            setupRecipe(recipe)
        }
    }

    private fun setupRecipe(recipe: Recipe) {
        toolbar_layout.title = recipe.name

        Glide.with(this)
            .load(recipe.image)
            .centerCrop()
            .into(recipeImage)

        recipeIngredients.text = recipe.ingredients
        recipeDirections.text = recipe.directions
    }

    companion object {
        fun intent(context: Context?, recipe: Recipe?) =
            Intent(context, RecipeDetailActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra(UriManager.RECIPE_PARAM, recipe)
            }

        fun start(context: Context, recipe: Recipe?) =
            context.startActivity(
                intent(
                    context,
                    recipe
                )
            )
    }
}
