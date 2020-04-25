package com.anacoimbra.android.recipes.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.anacoimbra.android.recipes.R
import com.anacoimbra.android.recipes.helpers.FirestoreManager
import com.anacoimbra.android.recipes.model.toRecipe
import com.anacoimbra.android.recipes.ui.adapter.RecipeAdapter
import com.google.android.gms.actions.SearchIntents
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val adapter = RecipeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.action == SearchIntents.ACTION_SEARCH) {
            val query = intent.getStringExtra(SearchIntents.EXTRA_QUERY)
            searchRecipes(query)
        } else loadRecipes()

        recipesList.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun searchRecipes(searchQuery: String?) {
        FirestoreManager.searchRecipes(searchQuery) { query, exception ->
            if (exception == null) {
                val recipes = query?.documents?.map { doc ->
                    doc?.data?.toRecipe(doc.id)
                }?.filterNotNull().orEmpty()
                adapter.recipes = recipes
            } else {
                exception.printStackTrace()
            }
        }
    }

    private fun loadRecipes() {
        FirestoreManager.getAllRecipes { query, exception ->
            if (exception == null) {
                val recipes = query?.documents?.map { doc ->
                    doc?.data?.toRecipe(doc.id)
                }?.filterNotNull().orEmpty()
                adapter.recipes = recipes
            } else {
                exception.printStackTrace()
            }
        }
    }
}