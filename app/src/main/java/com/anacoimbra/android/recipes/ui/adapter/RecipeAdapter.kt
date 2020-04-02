package com.anacoimbra.android.recipes.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anacoimbra.android.recipes.R
import com.anacoimbra.android.recipes.model.Recipe
import com.anacoimbra.android.recipes.ui.activity.RecipeDetailActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recipe_item.view.*

class RecipeAdapter : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    var recipes: List<Recipe> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = recipes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(recipe: Recipe) = with(itemView) {
            Glide.with(context)
                .load(recipe.image)
                .centerCrop()
                .into(recipeImage)

            recipeName.text = recipe.name
            recipeBaseIngredients.text = recipe.baseIngredients?.joinToString().orEmpty()

            viewRecipe.setOnClickListener { RecipeDetailActivity.start(context, recipe) }
        }
    }
}