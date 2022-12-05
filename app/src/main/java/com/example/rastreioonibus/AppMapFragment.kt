package com.example.rastreioonibus

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class AppMapFragment : SupportMapFragment() {
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

        googleMap = map?.apply {
            mapType = GoogleMap.MAP_TYPE_SATELLITE
        }

        googleMap?.run {
            animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 20F))
        }
    }
}