package com.example.rastreioonibus.presentation.map

import android.content.Context
import android.net.ConnectivityManager
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.rastreioonibus.R
import com.example.rastreioonibus.domain.model.Parades
import com.example.rastreioonibus.domain.model.Vehicles
import com.example.rastreioonibus.presentation.util.ConnectivityState
import com.example.rastreioonibus.presentation.util.StatesOfCardMessage
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AppMapFragment : SupportMapFragment() {
    private val viewModel: MapsViewModel by inject()
    private lateinit var googleMap: GoogleMap

    private var listPosVehicles = listOf<Vehicles>()
    private var listParades = listOf<Parades>()
    private var hasFilled = false

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var connectivityState: ConnectivityState

    private lateinit var statesOfCardMessage: StatesOfCardMessage

    private lateinit var behaviorDetailsParades: BottomSheetBehavior<LinearLayout>
    private lateinit var behaviorFilter: BottomSheetBehavior<ConstraintLayout>

    override fun getMapAsync(callback: OnMapReadyCallback) {

        statesOfCardMessage = StatesOfCardMessage(requireActivity())

        connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityState = ConnectivityState(connectivityManager)

        behaviorDetailsParades =
            BottomSheetBehavior.from(requireActivity().bottomSheetDetailsParades)
        behaviorFilter = BottomSheetBehavior.from(requireActivity().bottomSheetFilter)

        behaviorDetailsParades.state = BottomSheetBehavior.STATE_HIDDEN
        behaviorFilter.state = BottomSheetBehavior.STATE_HIDDEN

        super.getMapAsync {
            googleMap = it
            connectivityState.networkCallback(
                ::callbackOnAvailable,
                ::callbackOnLost
            )
            callback.onMapReady(googleMap)
        }

        if (!connectivityState.haveInternetOnInitApp()) {
            statesOfCardMessage.showMessageProblem(resources.getString(R.string.txt_no_conection))
        }

        requireActivity().fabFilter.setOnClickListener {
            behaviorDetailsParades.state = BottomSheetBehavior.STATE_HIDDEN
            behaviorFilter.state = BottomSheetBehavior.STATE_EXPANDED
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

    private fun initMap(map: GoogleMap) {
        val origin = LatLng(-23.561706, -46.655981)

        googleMap = map.apply {
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }

        googleMap.run {
            animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 15F))
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isScrollGesturesEnabled = true
        }

        viewModel.error.observe(this) {
            statesOfCardMessage
                .showMessageProblem(
                    resources.getString(R.string.txt_not_successful_response)
                )
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

    private fun fillMap(googleMap: GoogleMap) {
        googleMap.setOnCameraMoveListener {
            if (behaviorDetailsParades.state == BottomSheetBehavior.STATE_EXPANDED) {
                behaviorDetailsParades.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        googleMap.run {

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

            googleMap.setOnMarkerClickListener {

                if (it.snippet == "veiculo") {
                    it.showInfoWindow()
                } else {
                    val fragment = DetailsDialog.newInstance(it.title ?: "")

                    behaviorDetailsParades.state = BottomSheetBehavior.STATE_EXPANDED

                    requireActivity().supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details, fragment, DetailsDialog.DIALOG_TAG)
                        .commit()
                }

                true
            }
        }
    }

    private fun callbackOnAvailable(){
        lifecycleScope.launch {
            if (!hasFilled) {
                initLists()
                initMap(googleMap)
                hasFilled = true
            }

            statesOfCardMessage.occultMessageProblem()
        }
    }

    private fun callbackOnLost(){
        lifecycleScope.launch {
            statesOfCardMessage
                .showMessageProblem(resources.getString(R.string.txt_no_conection))
        }
    }
}