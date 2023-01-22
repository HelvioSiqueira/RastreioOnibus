package com.example.rastreioonibus.presentation.map

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.lifecycle.lifecycleScope
import com.example.rastreioonibus.R
import com.example.rastreioonibus.domain.model.Parades
import com.example.rastreioonibus.domain.model.Vehicles
import com.example.rastreioonibus.presentation.util.StatesOfCardMessage
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AppMapFragment : SupportMapFragment() {
    private val viewModel: MapsViewModel by inject()
    private var googleMap: GoogleMap? = null

    private var listPosVehicles = listOf<Vehicles>()
    private var listParades = listOf<Parades>()

    private lateinit var connectivityManager: ConnectivityManager

    private var hasFilled = false

    private lateinit var statesOfCardMessage: StatesOfCardMessage

    override fun getMapAsync(callback: OnMapReadyCallback) {

        statesOfCardMessage = StatesOfCardMessage(requireActivity())

        connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        super.getMapAsync {
            googleMap = it
            networkCallback()
            callback.onMapReady(googleMap!!)
        }

        if(!haveInternetOnInitApp()){
            statesOfCardMessage.showMessageProblem(resources.getString(R.string.txt_no_conection))
        }
    }

    private fun initLists() {
        viewModel.authenticate(requireContext())

        viewModel.isAuthenticate.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getPosVehicles()
                viewModel.getParades("")
            }
        }

        viewModel.listParades.observe(viewLifecycleOwner) {
            listParades = it
        }

        viewModel.listPosVehicles.observe(viewLifecycleOwner) {
            listPosVehicles = it
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
            statesOfCardMessage.showMessageProblem(resources.getString(R.string.txt_not_successful_response))
        }

        viewModel.endLoading.observe(this) {
            if (it) {
                statesOfCardMessage.occultMessageLoading()
                fillMap(map)
            } else {
                statesOfCardMessage.showMessageLoading()
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

    private fun networkCallback() {
        connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                lifecycleScope.launch {
                    if (!hasFilled) {
                        initLists()
                        initMap(googleMap)
                        hasFilled = true
                    }

                    statesOfCardMessage.occultMessageProblem()
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                lifecycleScope.launch {
                    statesOfCardMessage.showMessageProblem(resources.getString(R.string.txt_no_conection))
                }
            }
        })
    }

    private fun haveInternetOnInitApp(): Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.run {
                this.activeNetwork != null && this.getNetworkCapabilities(this.activeNetwork) != null
            }
        } else {
            connectivityManager.run {
                this.activeNetwork != null && this.isDefaultNetworkActive
            }
        }
    }

}