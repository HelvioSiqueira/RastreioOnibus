package com.example.rastreioonibus

import android.util.Log
import com.example.rastreioonibus.model.Parades
import com.example.rastreioonibus.model.Vehicles
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.android.ext.android.inject

class AppMapFragment : SupportMapFragment() {
    private val viewModel: MapsViewModel by inject()

    private var listPosVehicles = listOf<Vehicles>()
    private var listParades = listOf<Parades>()

    private var googleMap: GoogleMap? = null

    override fun getMapAsync(callback: OnMapReadyCallback) {

        viewModel.authenticate(requireContext())

        viewModel.isAuthenticate.observe(this) {
            if (it) {
                viewModel.getPosVehicles()
                viewModel.getParades("")
            }
        }

        viewModel.listParades.observe(this) {
            listParades = it
        }

        viewModel.listPosVehicles.observe(this) {
            listPosVehicles = it
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

        viewModel.isLoading.observe(this) {
            if (it) {
                fillMap(map)
            }
        }

        Log.d("HSV", listParades.size.toString())
    }

    private fun fillMap(googleMap: GoogleMap?) {

        googleMap?.run {

            listParades.forEach { parada ->
                addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_parada))
                        .position(LatLng(parada.latitude, parada.longitude))
                        .title(parada.codeOfParade.toString())
                        .snippet("parada")
                )
            }

            listPosVehicles.forEach {
                addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_bus))
                        .position(LatLng(it.latitude, it.longitude))
                        .title(it.prefixOfVehicle)
                        .snippet("veiculo")
                )
            }

            googleMap.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener {

                DetailsParade
                    .newInstance(it.title ?: "", it.snippet!!)
                    .show(childFragmentManager, DetailsParade.DIALOG_TAG)

                true
            })
        }
    }
}