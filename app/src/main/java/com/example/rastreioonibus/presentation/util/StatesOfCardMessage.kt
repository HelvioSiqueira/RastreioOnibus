package com.example.rastreioonibus.presentation.util

import android.app.Activity
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.rastreioonibus.R
import kotlinx.android.synthetic.main.activity_main.*

class StatesOfCardMessage(private val activity: Activity) {

    private var cardMessage: CardView = activity.cardMessage
    private var txtMessage: TextView = activity.txtMessage
    private var progressBar: ProgressBar = activity.progressBar

    fun showMessageLoading() {
        cardMessage.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        txtMessage.text = activity.resources.getString(R.string.txt_loading_message)
    }

    fun occultMessageLoading(){
        cardMessage.visibility = View.GONE
    }

    fun showMessageProblem(
        message: String = ""
    ) {
        progressBar.visibility = View.VISIBLE
        cardMessage.visibility = View.VISIBLE
        txtMessage.text = message
    }

    fun occultMessageProblem(){
        cardMessage.visibility = View.GONE
    }
}