package com.example.rastreioonibus.presentation.map

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AppMapFragment : SupportMapFragment() {
    private val viewModel: MapsViewModel by inject()
    private var googleMap: GoogleMap? = null

    private var listPosVehicles = listOf<Vehicles>()
    private var listParades = listOf<Parades>()

    private lateinit var cardLoading: CardView
    private lateinit var txtMessage: TextView
    private lateinit var progressBar: ProgressBar

    private var haveInternet = false
    private var hasFilled = false

    override fun getMapAsync(callback: OnMapReadyCallback) {

        cardLoading = requireActivity().cardLoading
        txtMessage = requireActivity().txtMessage
        progressBar = requireActivity().progressBar

        super.getMapAsync {
            googleMap = it
            networkCallback()

            if(!haveInternet){
                showMessageProblem(
                    resources.getString(R.string.txt_no_conection),
                    View.GONE,
                    View.VISIBLE
                )
            }

            callback.onMapReady(googleMap!!)
        }
    }

    private fun init() {
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
            showMessageProblem(
                resources.getString(R.string.txt_not_successful_response),
                View.GONE,
                View.VISIBLE
            )
        }

        viewModel.endLoading.observe(this) {
            if (it && haveInternet) {
                showMessageLoading(View.GONE)
                fillMap(map)
            } else {
                showMessageLoading(View.VISIBLE)
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

    private fun showMessageLoading(progressBarVisibility: Int){
        cardLoading.visibility = progressBarVisibility
        progressBar.visibility = View.VISIBLE
        txtMessage.visibility = View.VISIBLE
        txtMessage.text = resources.getString(R.string.txt_loading_message)
    }

    fun showMessageProblem(
        message: String = "",
        progressBarVisibility: Int,
        cardLoadingVisibility: Int
    ) {
        txtMessage.text = message
        progressBar.visibility = progressBarVisibility
        cardLoading.visibility = cardLoadingVisibility
    }

    private fun networkCallback() {

        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                haveInternet = true

                lifecycleScope.launch {
                    if (!hasFilled) {
                        init()
                        initMap(googleMap)
                        hasFilled = true
                    }

                    showMessageProblem(
                        progressBarVisibility = View.GONE,
                        cardLoadingVisibility = View.GONE
                    )
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                lifecycleScope.launch {
                    showMessageProblem(
                        resources.getString(R.string.txt_no_conection),
                        View.GONE,
                        View.VISIBLE
                    )
                }
            }
        })
    }
}