package com.example.rastreioonibus.presentation.util

import android.content.Context
import com.example.rastreioonibus.R
import com.example.rastreioonibus.domain.model.Parades
import com.example.rastreioonibus.domain.model.Vehicles
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

fun GoogleMap.addMarkersToMap(
    context: Context,
    listOfVehicles: List<Vehicles>,
    listOfParades: List<Parades>
) {

    val bitmapCache = MyBitmapCache(context, 1)
    val bus = bitmapCache.getBitmap(R.drawable.bus_svg)
    val stop = bitmapCache.getBitmap(R.drawable.stop_svg)

    listOfParades.forEach { parade ->
        addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(stop!!))
                .position(LatLng(parade.latitude, parade.longitude))
                .title(parade.codeOfParade.toString())
                .snippet("parada")
        )
    }

    listOfVehicles.forEach { vehicle ->
        this.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(bus!!))
                .position(LatLng(vehicle.latitude, vehicle.longitude))
                .title(vehicle.prefixOfVehicle)
                .snippet("veiculo")
        )
    }


}