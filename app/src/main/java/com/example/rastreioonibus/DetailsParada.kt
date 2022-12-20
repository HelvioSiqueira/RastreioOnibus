package com.example.rastreioonibus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.rastreioonibus.databinding.LayoutDetailsBinding

class DetailsParada: DialogFragment() {

    private lateinit var titleParada: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = LayoutDetailsBinding.inflate(layoutInflater)

        titleParada = binding.titleParada
        titleParada.text = arguments?.getString(EXTRA_TITLE)

        return binding.root
    }

    companion object{
        const val DIALOG_TAG = "detailsDialog"
        private const val EXTRA_TITLE = "title"

        fun newInstance(titleParada: String) = DetailsParada().apply {
            arguments = Bundle().apply {
                putString(EXTRA_TITLE, titleParada)
            }
        }
    }
}