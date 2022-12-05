package com.example.rastreioonibus

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.rastreioonibus.mapsInicio.MapsViewModel
import com.example.rastreioonibus.model.Paradas
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class AppMapFragment : SupportMapFragment() {
    private val viewModel: MapsViewModel by inject()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var listPosVeiculos = listOf<Veiculos>()
    private var listParadas = listOf<Paradas>()

    private var googleMap: GoogleMap? = null

    override fun getMapAsync(callback: OnMapReadyCallback) {
        super.getMapAsync {
            googleMap = it
            initMap(googleMap)
            callback.onMapReady(googleMap!!)
        }
    }

    private fun initMap(map: GoogleMap?) {
        val origin = LatLng(-23.561706, -46.655981)

        scope.launch {
            getDatas()

            joinAll()
        }

        googleMap = map?.apply {
            mapType = GoogleMap.MAP_TYPE_SATELLITE
        }

        googleMap?.run {
            animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 20F))
        }
    }

    suspend fun getDatas() {
        viewModel.autenticar(requireContext())

        listPosVeiculos = viewModel.getPosVeiculos()
        listParadas = viewModel.getParadas("")

        Log.d("HSV", listParadas.size.toString())

    }
}