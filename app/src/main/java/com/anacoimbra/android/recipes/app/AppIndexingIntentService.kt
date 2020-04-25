package com.anacoimbra.android.recipes.app

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.anacoimbra.android.recipes.helpers.FirestoreManager
import com.anacoimbra.android.recipes.model.toRecipe
import com.google.firebase.appindexing.FirebaseAppIndex

class AppIndexingIntentService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        FirestoreManager.getAllRecipes { query, exception ->
            if (exception == null) {
                val indexable = query?.documents?.mapNotNull {
                    it.data?.toRecipe(it.id)?.toIndexable(applicationContext)
                }.orEmpty().toTypedArray()
                if (indexable.isNotEmpty())
                    FirebaseAppIndex.getInstance().update(*indexable)
                        .addOnSuccessListener { Log.d("Recipes Assistant", "Update succeeded!") }
                        .addOnFailureListener { ex ->
                            Log.d("Recipes Assistant", "Update failed: ${ex.localizedMessage}")
                        }
            } else {
                Log.d("Recipes Assistant", "An error occurred indexing recipes")
            }
        }
    }

    companion object {
        private const val UNIQUE_JOB_ID = 50
        fun enqueueWork(context: Context) {
            enqueueWork(context, AppIndexingIntentService::class.java, UNIQUE_JOB_ID, Intent())
        }
    }
}
