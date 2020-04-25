package com.anacoimbra.android.recipes.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.appindexing.FirebaseAppIndex

class AppIndexingUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null && FirebaseAppIndex.ACTION_UPDATE_INDEX == intent.action) {
            AppIndexingIntentService.enqueueWork(context)
        }
    }
}