package com.anacoimbra.android.recipes.model

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import com.anacoimbra.android.recipes.R
import com.google.firebase.appindexing.Indexable
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

    fun toIndexable(context: Context?): Indexable =
        Indexable.Builder()
            .setName(name.orEmpty())
            .setDescription(
                "${context?.getString(R.string.ingredients)}: ${ingredients}, " +
                        "${context?.getString(R.string.directions)} ${directions.orEmpty()}"
            )
            .setMetadata(Indexable.Metadata.Builder().setSliceUri(Uri.parse(getUrl())))
            .setImage(image.orEmpty())
            .setKeywords(*baseIngredients?.toTypedArray().orEmpty())
            .build()

    fun getUrl() = "content://com.anacoimbra.android.recipes/detail?recipe=${id}"
}

sealed class Resource<out T> {
    class Success<T>(val data: T?) : Resource<T>()
    class Error<T>(val id: String?) : Resource<T>()
}