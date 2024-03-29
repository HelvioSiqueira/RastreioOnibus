package com.helvio.rastreioonibus.presentation.util

import android.content.Context
import android.os.Looper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.helvio.rastreioonibus.R
import com.helvio.rastreioonibus.domain.model.Parades
import com.helvio.rastreioonibus.domain.model.Vehicles
import android.os.Handler

fun GoogleMap.addMarkersToMap(
    context: Context,
    listOfVehicles: List<Vehicles>?,
    listOfParades: List<Parades>
) {

    val bitmapCache = MyBitmapCache(context, 1)
    val bus = bitmapCache.getBitmap(R.drawable.bus_svg, R.color.light_blue_300)
    val stop = bitmapCache.getBitmap(R.drawable.stop_svg, R.color.light_blue_700)

    listOfParades.forEach { parade ->

        addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(stop!!))
                .position(LatLng(parade.latitude, parade.longitude))
                .title(parade.codeOfParade.toString())
                .snippet("parada")
        )
    }

    Handler(Looper.getMainLooper()).postDelayed({

        listOfVehicles?.forEach { vehicle ->
            this.addMarker(
                MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(bus!!))
                    .position(LatLng(vehicle.latitude, vehicle.longitude))
                    .title(vehicle.prefixOfVehicle)
                    .snippet("veiculo")
            )
        }
    }, 32)
}