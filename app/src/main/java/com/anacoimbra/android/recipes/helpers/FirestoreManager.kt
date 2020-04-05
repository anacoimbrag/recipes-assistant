package com.anacoimbra.android.recipes.helpers

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

object FirestoreManager {

    private const val COLLECTION_NAME = "recipes"

    private val db by lazy {
        FirebaseFirestore.getInstance()
    }

    fun getRecipeById(
        recipe: String?,
        listener: (query: Task<DocumentSnapshot>) -> Unit
    ) {
        db.collection(COLLECTION_NAME)
            .document(recipe.orEmpty())
            .get()
            .addOnCompleteListener(listener)
    }

    fun getAllRecipes(listener: (query: QuerySnapshot?, exception: FirebaseFirestoreException?) -> Unit) {
        db.collection(COLLECTION_NAME)
            .addSnapshotListener(MetadataChanges.INCLUDE, listener)
    }
}