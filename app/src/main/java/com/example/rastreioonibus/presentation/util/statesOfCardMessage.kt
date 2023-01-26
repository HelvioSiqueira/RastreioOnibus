package com.example.rastreioonibus.presentation.util

import android.content.Context
import android.view.View
import com.example.rastreioonibus.R
import com.example.rastreioonibus.databinding.ActivityMainBinding

fun ActivityMainBinding.showMessageLoading(context: Context) {
    cardMessage.visibility = View.VISIBLE
    progressBar.visibility = View.VISIBLE
    txtMessage.text = context.resources.getString(R.string.txt_loading_message)
}

fun ActivityMainBinding.occultMessageLoading() {
    cardMessage.visibility = View.GONE
}

fun ActivityMainBinding.showMessageProblem(message: String = "") {
    progressBar.visibility = View.VISIBLE
    cardMessage.visibility = View.VISIBLE
    txtMessage.text = message
}

fun ActivityMainBinding.occultMessageProblem() {
    cardMessage.visibility = View.GONE
}
