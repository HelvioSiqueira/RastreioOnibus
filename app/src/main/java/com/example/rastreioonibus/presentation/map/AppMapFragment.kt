package com.example.rastreioonibus.presentation.map

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.rastreioonibus.R
import com.example.rastreioonibus.domain.model.Parades
import com.example.rastreioonibus.domain.model.Vehicles
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class AppMapFragment : SupportMapFragment() {
    private val viewModel: MapsViewModel by inject()
    private var googleMap: GoogleMap? = null

    private var listPosVehicles = listOf<Vehicles>()
    private var listParades = listOf<Parades>()

    private lateinit var cardLoading: CardView
    private lateinit var txtMessage: TextView
    private lateinit var progressBar: ProgressBar

    override fun getMapAsync(callback: OnMapReadyCallback) {

        cardLoading = requireActivity().cardLoading
        txtMessage = requireActivity().txtMessage
        progressBar = requireActivity().progressBar

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
        val origin = LatLng(-23.561706, -46.655981)

        googleMap = map?.apply {
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }

        googleMap?.run {
            animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 15F))
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isScrollGesturesEnabled = true
        }

        viewModel.error.observe(this) {
            txtMessage.text = resources.getString(R.string.txt_not_successful_response)
            progressBar.visibility = View.GONE
            cardLoading.visibility = View.VISIBLE
        }

        viewModel.endLoading.observe(this) {
            if (it) {
                cardLoading.visibility = View.GONE
                fillMap(map)
            } else {
                cardLoading.visibility = View.VISIBLE
            }
        }
    }

    private fun fillMap(googleMap: GoogleMap?) {
        googleMap?.run {

            listParades.forEach { parade ->
                addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_parada))
                        .position(LatLng(parade.latitude, parade.longitude))
                        .title(parade.codeOfParade.toString())
                        .snippet("parada")
                )
            }

            listPosVehicles.forEach { vehicle ->
                addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_bus))
                        .position(LatLng(vehicle.latitude, vehicle.longitude))
                        .title(vehicle.prefixOfVehicle)
                        .snippet("veiculo")
                )
            }

            googleMap.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener {
                DetailsDialog.newInstance(it.title ?: "", it.snippet!!)
                    .show(childFragmentManager, DetailsDialog.DIALOG_TAG)

                true
            })
        }
    }
}