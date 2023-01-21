package com.example.rastreioonibus.presentation.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.rastreioonibus.databinding.LayoutDetailsBinding
import org.koin.android.ext.android.inject

class DetailsDialog : DialogFragment() {

    private val viewModel: MapsViewModel by inject()

    private lateinit var npParade: TextView
    private lateinit var edParade: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = LayoutDetailsBinding.inflate(layoutInflater)

        val typeSelected = arguments?.getString(EXTRA_TYPE)
        val idParadeOrVehicle = arguments?.getString(EXTRA_TITLE)

        npParade = binding.npParada
        edParade = binding.edParada

        when(typeSelected){

            "parada" ->{
                val parade = viewModel.getSelectedParade(idParadeOrVehicle!!)

                npParade.text = parade?.nameOfParade
                edParade.text = parade?.addressOfParade
            }

            "veiculo" ->{
                val vehicle = viewModel.getSelectedVehicle(idParadeOrVehicle!!)

                npParade.text = vehicle?.prefixOfVehicle
                edParade.visibility = View.GONE
            }
        }

        return binding.root
    }

    companion object {
        const val DIALOG_TAG = "detailsDialog"
        private const val EXTRA_TITLE = "title"
        private const val EXTRA_TYPE = "type"

        fun newInstance(idParadeOrVehicle: String, type: String) = DetailsDialog().apply {
            arguments = Bundle().apply {
                putString(EXTRA_TITLE, idParadeOrVehicle)
                putString(EXTRA_TYPE, type)
            }
        }
    }
}