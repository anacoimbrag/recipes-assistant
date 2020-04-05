package com.anacoimbra.android.recipes.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Suppress("UNCHECKED_CAST")
fun Map<String, Any?>.toRecipe(id: String) =
    Recipe(
        id,
        get("name")?.toString(),
        get("ingredients")?.toString(),
        get("baseIngredients") as? List<String>,
        get("directions")?.toString(),
        get("image")?.toString()
    )

@Parcelize
data class Recipe(
    val id: String,
    val name: String?,
    val ingredients: String?,
    val baseIngredients: List<String>?,
    val directions: String?,
    val image: String?
) : Parcelable {
    override fun toString(): String =
        "{ id: $id, name: $name, ingredients: $ingredients, baseIngredients: ${baseIngredients?.joinToString()}, directions: $directions, image: $image }"
}

sealed class Resource<out T> {
    class Success<T>(val data: T?) : Resource<T>()
    class Error<T>(val id: String?) : Resource<T>()
}