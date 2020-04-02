package com.anacoimbra.android.recipes.app

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.service.voice.VoiceInteractionService
import androidx.slice.SliceManager
import com.anacoimbra.android.recipes.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        grantSlicePermissions()
        FirebaseApp.initializeApp(this)
        FirebaseFirestore.setLoggingEnabled(true)
    }

    private fun grantSlicePermissions() {
        val context: Context = applicationContext
        val sliceProviderUri: Uri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(BuildConfig.APPLICATION_ID)
            .build()
        val assistantPackage = getAssistantPackage(context).orEmpty()
        SliceManager.getInstance(context)
            .grantSlicePermission(assistantPackage, sliceProviderUri)
    }

    private fun getAssistantPackage(context: Context): String? {
        val packageManager: PackageManager = context.packageManager
        val resolveInfoList =
            packageManager.queryIntentServices(
                Intent(VoiceInteractionService.SERVICE_INTERFACE), 0
            )
        return if (resolveInfoList.isEmpty()) {
            null
        } else resolveInfoList[0].serviceInfo.packageName
    }
}