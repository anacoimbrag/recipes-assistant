package com.anacoimbra.android.recipes.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.anacoimbra.android.recipes.R
import kotlinx.android.synthetic.main.settings_activity.*

class SettingsActivity : AppCompatActivity() {

    private val privacyPolicyUrl =
        "https://drive.google.com/file/d/10oSCgQ-gz68Bg8r7NZt9b9MTH9qGUzzD/view?usp=sharing"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        val creditsText = SpannableString(getString(R.string.app_credits))
        setupClickableSpan(
            creditsText,
            "https://gist.github.com/lucasheriques/ed2214dba65b8903a5b62566f4439005",
            "Github"
        )
        setupClickableSpan(creditsText, "https://www.flickr.com/", "Flickr")
        setupClickableSpan(creditsText, "https://flaticon.com", "FlatIcon")
        txtCredits.text = creditsText
        txtCredits.movementMethod = LinkMovementMethod.getInstance()

        showPrivacyPolice.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl)))
        }
    }

    private fun setupClickableSpan(spannable: SpannableString, link: String, text: String) {
        val start = spannable.indexOf(text, ignoreCase = true)
        spannable.setSpan(
            LinkSpan(link),
            start,
            start + text.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }

    inner class LinkSpan(private val link: String) : ClickableSpan() {
        override fun onClick(widget: View) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }
}