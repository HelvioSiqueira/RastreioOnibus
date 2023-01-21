package com.example.rastreioonibus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.rastreioonibus.databinding.LayoutDetailsBinding
import org.koin.android.ext.android.inject

class DetailsParada : DialogFragment() {

    private val viewModel: MapsViewModel by inject()

    private lateinit var npParada: TextView
    private lateinit var edParada: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = LayoutDetailsBinding.inflate(layoutInflater)

        val typeSelected = arguments?.getString(EXTRA_TYPE)
        val idParadaOuVeiculo = arguments?.getString(EXTRA_TITLE)

        npParada = binding.npParada
        edParada = binding.edParada

        when(typeSelected){

            "parada" ->{
                val parada = viewModel.getSelectedParada(idParadaOuVeiculo!!)

                npParada.text = parada?.np
                edParada.text = parada?.ed
            }

            "veiculo" ->{
                val veiculo = viewModel.getSelectedVeiculo(idParadaOuVeiculo!!)

                npParada.text = veiculo?.p
                edParada.visibility = View.GONE
            }
        }

        return binding.root
    }

    companion object {
        const val DIALOG_TAG = "detailsDialog"
        private const val EXTRA_TITLE = "title"
        private const val EXTRA_TYPE = "type"

        fun newInstance(idParadaOuVeiculo: String, type: String) = DetailsParada().apply {
            arguments = Bundle().apply {
                putString(EXTRA_TITLE, idParadaOuVeiculo)
                putString(EXTRA_TYPE, type)
            }
        }
    }
}