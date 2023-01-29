package com.example.rastreioonibus.presentation.util

import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import com.example.rastreioonibus.databinding.SearchBusAndStopLayoutBinding

fun SearchBusAndStopLayoutBinding.setUrlOnPrivacyPolicy(){
    val textUrl = "Politica de Privacidade"

    val spannable = SpannableString(textUrl).apply {
        setSpan(
            URLSpan("https://www.freeprivacypolicy.com/live/a1bcd0d2-b1e0-42af-8c12-81dd84fa82cc"),
            textUrl.indexOf(textUrl),
            textUrl.indexOf(textUrl) + textUrl.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }

    this.txtPrivacyPolicy.apply {
        movementMethod = LinkMovementMethod.getInstance()
        text = spannable
    }
}