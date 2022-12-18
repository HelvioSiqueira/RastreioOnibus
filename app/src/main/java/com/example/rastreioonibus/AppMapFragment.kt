package com.example.rastreioonibus

import android.util.Log
import com.example.rastreioonibus.model.Paradas
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class AppMapFragment : SupportMapFragment() {
    private val viewModel: MapsViewModel by inject()

    private var listPosVeiculos = listOf<Veiculos>()
    private var listParadas = listOf<Paradas>()

    private var googleMap: GoogleMap? = null

    override fun getMapAsync(callback: OnMapReadyCallback) {

        viewModel.autenticar(requireContext())

        viewModel.isAutenticate.observe(this) {
            if (it) {
                viewModel.getPosVeiculos()
                viewModel.getParadas("")
            }
        }

        viewModel.listParadas.observe(this) {
            listParadas = it
        }

        viewModel.listPosVeiculos.observe(this) {
            listPosVeiculos = it
        }

        super.getMapAsync {
            googleMap = it
            initMap(googleMap)
            callback.onMapReady(googleMap!!)
        }
    }

    private fun initMap(map: GoogleMap?) {
        googleMap = map?.apply {
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }

        val origin = LatLng(-23.561706, -46.655981)

        googleMap?.run {
            animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 15F))
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isScrollGesturesEnabled = true
        }

        viewModel.mediator.observe(this) {
            if (it) {
                fillMap(map)
            }
        }

        Log.d("HSV", listParadas.size.toString())
    }

    private fun fillMap(googleMap: GoogleMap?) {

        googleMap?.run {

            listParadas.forEach {
                addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_parada))
                        .position(LatLng(it.py, it.px))
                        .title(it.np)
                        .snippet("${it.cp} - ${it.ed}")
                )
            }

            listPosVeiculos.forEach {
                addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_bus))
                        .position(LatLng(it.py, it.px))
                        .title(it.p)
                        .snippet((if (it.a) "Veiculo acessível para pessoas com deficiencia" else "Veiculo não acessível para pessoas com deficiencia"))
                )
            }
        }
    }
}