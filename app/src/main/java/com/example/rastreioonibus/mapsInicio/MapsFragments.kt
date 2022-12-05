package com.example.rastreioonibus.mapsInicio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import com.example.rastreioonibus.AppMapFragment
import com.example.rastreioonibus.PosLinhas
import com.example.rastreioonibus.R
import com.example.rastreioonibus.Veiculos
import com.example.rastreioonibus.databinding.LayoutMapsFragmentBinding
import com.example.rastreioonibus.model.Paradas
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.parceler.Parcels

class MapsFragments : Fragment() {
    private val viewModel: MapsViewModel by inject()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val binding: LayoutMapsFragmentBinding by lazy {
        LayoutMapsFragmentBinding.inflate(layoutInflater)
    }

    private var googleMap: GoogleMap? = null

    private var listPosVeiculos = listOf<Veiculos>()
    private var listParadas = listOf<Paradas>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d("HSV", "Id: ${parentFragmentManager.findFragmentById(R.id.fragmentMap)}")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scope.launch {
            viewModel.autenticar(requireContext())

            listPosVeiculos = viewModel.getPosVeiculos()
            listParadas = viewModel.getParadas("")

            Log.d("HSV", listParadas.size.toString())
        }
    }


    companion object {
        const val TAG_FRAGMENT_MAPS = "tagFragmentMaps"
        private const val EXTRA_SUPPORT_MAP_FRAGMENT = "extraMapFragment"

        fun newInstance() = MapsFragments()
    }
}