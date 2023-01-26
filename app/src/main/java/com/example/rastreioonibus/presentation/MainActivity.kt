package com.example.rastreioonibus.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.rastreioonibus.R
import com.example.rastreioonibus.databinding.ActivityMainBinding
import com.example.rastreioonibus.domain.model.Parades
import com.example.rastreioonibus.domain.model.Vehicles
import com.example.rastreioonibus.presentation.map.DetailsDialog
import com.example.rastreioonibus.presentation.map.MapsViewModel
import com.example.rastreioonibus.presentation.util.ConnectivityState
import com.example.rastreioonibus.presentation.util.MyBitmapCache
import com.example.rastreioonibus.presentation.util.StatesOfCardMessage
import com.example.rastreioonibus.presentation.util.toBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity :
    AppCompatActivity() {

    private val viewModel: MapsViewModel by inject()
    private lateinit var googleMap: GoogleMap

    private var listPosVehicles = listOf<Vehicles>()
    private var listParades = listOf<Parades>()

    private lateinit var statesOfCardMessage: StatesOfCardMessage

    private lateinit var behaviorDetailsParades: BottomSheetBehavior<LinearLayout>
    private lateinit var behaviorFilter: BottomSheetBehavior<ConstraintLayout>

    private lateinit var bitmapCache: MyBitmapCache

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentMap = supportFragmentManager
            .findFragmentById(R.id.fragmentMap) as SupportMapFragment
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val connectivityState = ConnectivityState(connectivityManager)

        bitmapCache = MyBitmapCache(this, 1)

        statesOfCardMessage = StatesOfCardMessage(this, binding)

        behaviorDetailsParades =
            BottomSheetBehavior.from(binding.bottomSheetDetailsParades)
        behaviorFilter = BottomSheetBehavior.from(binding.bottomSheetFilter)

        behaviorDetailsParades.state = BottomSheetBehavior.STATE_HIDDEN
        behaviorFilter.state = BottomSheetBehavior.STATE_HIDDEN

        if (!connectivityState.haveInternetOnInitApp()) {
            statesOfCardMessage.showMessageProblem(resources.getString(R.string.txt_no_conection))
        }

        binding.fabFilter.setOnClickListener {
            behaviorDetailsParades.state = BottomSheetBehavior.STATE_HIDDEN
            behaviorFilter.state = BottomSheetBehavior.STATE_EXPANDED

            Log.d("HSV", viewModel.endLoading.value.toString())
            viewModel.getPosVehiclesByLine(2506)
            viewModel.getParadesByLine(2506)


        }

        fragmentMap.getMapAsync {
            googleMap = it
            connectivityState.networkCallback(
                ::callbackOnAvailable,
                ::callbackOnLost
            )
        }
    }

    private fun initLists() {
        viewModel.authenticate(this)

        viewModel.isAuthenticate.observe(this) {
            if (it) {
                viewModel.getPosVehicles()
                viewModel.getParades("")
            }
        }

        viewModel.listParades.observe(this) {
            listParades = it

            val first = it.first()

            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        first.latitude,
                        first.longitude
                    ), 15f
                )
            )
        }

        viewModel.listPosVehicles.observe(this) {
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
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isScrollGesturesEnabled = true
        }

        viewModel.error.observe(this) {
            statesOfCardMessage.showMessageProblem(
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

        googleMap.clear()

        googleMap.run {

            val stop = bitmapCache.getBitmap(R.drawable.stop_svg)

            listParades.forEach { parade ->
                addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(stop!!))
                        .position(LatLng(parade.latitude, parade.longitude))
                        .title(parade.codeOfParade.toString())
                        .snippet("parada")
                )
            }

            val bus = bitmapCache.getBitmap(R.drawable.bus_svg)

            listPosVehicles.forEach { vehicle ->
                addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(bus!!))
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

                    behaviorFilter.state = BottomSheetBehavior.STATE_HIDDEN
                    behaviorDetailsParades.state = BottomSheetBehavior.STATE_EXPANDED

                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details, fragment, DetailsDialog.DIALOG_TAG)
                        .commit()
                }

                true
            }
        }
    }

    private fun callbackOnAvailable() {
        lifecycleScope.launch {
            initLists()
            initMap(googleMap)
            statesOfCardMessage.occultMessageProblem()
        }
    }

    private fun callbackOnLost() {
        lifecycleScope.launch {
            statesOfCardMessage
                .showMessageProblem(resources.getString(R.string.txt_no_conection))
        }
    }
}
