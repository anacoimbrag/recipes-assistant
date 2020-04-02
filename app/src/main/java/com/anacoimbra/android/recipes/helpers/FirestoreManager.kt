package com.anacoimbra.android.recipes.helpers

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

object FirestoreManager {

    private const val COLLECTION_NAME = "recipes"
    private const val NAME_PARAM = "name"

    fun getRecipeByName(
        recipe: String?,
        listener: (query: QuerySnapshot?, exception: FirebaseFirestoreException?) -> Unit
    ) {
        FirebaseFirestore.getInstance()
            .collection(COLLECTION_NAME)
            .whereEqualTo(NAME_PARAM, recipe)
            .addSnapshotListener(listener)
    }

    fun getAllRecipes(listener: (query: QuerySnapshot?, exception: FirebaseFirestoreException?) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection(COLLECTION_NAME)
            .addSnapshotListener(listener)
    }
}