package com.example.rastreioonibus.presentation.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.rastreioonibus.R
import com.example.rastreioonibus.databinding.ActivityMainBinding

class StatesOfCardMessage(
    private val context: Context,
    private val binding: ActivityMainBinding
) {

    private var cardMessage: CardView = binding.cardMessage
    private var txtMessage: TextView = binding.txtMessage
    private var progressBar: ProgressBar = binding.progressBar

    fun showMessageLoading() {
        cardMessage.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        txtMessage.text = context.resources.getString(R.string.txt_loading_message)
    }

    fun occultMessageLoading() {
        cardMessage.visibility = View.GONE
    }

    fun showMessageProblem(message: String = "") {
        progressBar.visibility = View.VISIBLE
        cardMessage.visibility = View.VISIBLE
        txtMessage.text = message
    }

    fun occultMessageProblem() {
        cardMessage.visibility = View.GONE
    }
}