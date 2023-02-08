package com.helvio.rastreioonibus.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.helvio.rastreioonibus.R
import com.helvio.rastreioonibus.databinding.ActivityMainBinding
import com.helvio.rastreioonibus.databinding.SearchBusAndStopLayoutBinding
import com.helvio.rastreioonibus.databinding.SearchLinesLayoutBinding
import com.helvio.rastreioonibus.domain.model.Parades
import com.helvio.rastreioonibus.domain.model.Vehicles
import com.helvio.rastreioonibus.presentation.adapter.SearchLinesAdapter
import com.helvio.rastreioonibus.presentation.map.DetailsDialog
import com.helvio.rastreioonibus.presentation.map.MapsViewModel
import com.helvio.rastreioonibus.presentation.util.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import kotlin.coroutines.suspendCoroutine

//Trocar id do admob
class MainActivity : AppCompatActivity() {

    private val viewModel: MapsViewModel by inject()
    private lateinit var googleMap: GoogleMap

    private var listPosVehicles: List<Vehicles>? = listOf()
    private var listParades = listOf<Parades>()

    private var searchAdapter: SearchLinesAdapter = SearchLinesAdapter(this)
    private lateinit var binding: ActivityMainBinding
    private lateinit var behaviorDetailsParades: BottomSheetBehavior<LinearLayout>
    private lateinit var behaviorFilter: BottomSheetBehavior<ConstraintLayout>

    private val locationRequestCode = 1
    private var origin = LatLng(-23.561706, -46.655981)
    private lateinit var locationProviderClient: FusedLocationProviderClient

    private lateinit var connectivityManager: ConnectivityManager

    private var isGpsDialogOpened = false

    private val mapFragment: SupportMapFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapFragment.getMapAsync {
            googleMap = it
        }

        val bindingSearchStopAndVehicles = SearchBusAndStopLayoutBinding.inflate(layoutInflater)
        val bindingSearchLines = SearchLinesLayoutBinding.inflate(layoutInflater)

        val tabLayout = binding.tabLayout
        var layoutId = 0

        MobileAds.initialize(this)
        binding.adView.loadAd(AdRequest.Builder().build())

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        checkGpsStatus(this)

        connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (!connectivityManager.haveInternetOnInitApp()) {
            binding.showMessageProblem(resources.getString(R.string.txt_no_conection))
        }

        binding.setupTabAndPager(
            bindingSearchStopAndVehicles,
            bindingSearchLines,
            this
        )

        bindingSearchStopAndVehicles.setUrlOnPrivacyPolicy()
        bindingSearchLines.initSearchLinesAdapter(this, searchAdapter)

        behaviorDetailsParades =
            BottomSheetBehavior.from(binding.bottomSheetDetailsParades).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
        behaviorFilter = BottomSheetBehavior.from(binding.bottomSheetFilter).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bindingSearchStopAndVehicles.apply {
            inputTextParade.doOnTextChanged { text, _, _, count ->
                if (count > 0) {
                    layoutTextParadeLine.isEnabled = false
                } else if (text.isNullOrBlank()) {
                    layoutTextParadeLine.isEnabled = true
                }
            }
        }

        bindingSearchStopAndVehicles.apply {
            inputTextParadeLine.doOnTextChanged { text, _, _, count ->
                if (count > 0) {
                    layoutTextParade.isEnabled = false
                } else if (text.isNullOrBlank()) {
                    layoutTextParade.isEnabled = true
                }
            }
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> layoutId = 0
                    1 -> layoutId = 1
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.fabSearch.setOnClickListener {
            if (behaviorFilter.state == BottomSheetBehavior.STATE_EXPANDED) {
                when (layoutId) {
                    0 -> {
                        viewModel.search(bindingSearchStopAndVehicles)

                        viewModel.endLoading.observe(this) {
                            if (it) animateCameraWhenEndSearch()
                        }
                    }
                    1 -> {
                        bindingSearchLines.inputTextSearch.text.toString().let {
                            if (it.isNotBlank()) {
                                viewModel.getLines(it)
                            } else {
                                Toast.makeText(this, R.string.txt_without_text, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }

            } else {
                behaviorDetailsParades.state = BottomSheetBehavior.STATE_HIDDEN
                behaviorFilter.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        viewModel.isListPosVehiclesEmpty.observe(this) {
            if (it) {
                Toast.makeText(this, "Veiculos não encotrados", Toast.LENGTH_SHORT).show()
                binding.occultMessageLoading()
            }
        }

        viewModel.isListParadesEmpty.observe(this) {
            if (it) {
                Toast.makeText(this, "Paradas não encotrados", Toast.LENGTH_SHORT).show()
                binding.occultMessageLoading()
            }
        }

        viewModel.listLines.observe(this) {
            searchAdapter.submitList(it)

            if (it.isNotEmpty()) {
                bindingSearchLines.txtEmpty.visibility = View.GONE
            } else {
                bindingSearchLines.txtEmpty.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(this) {
            binding.showMessageProblem(resources.getString(R.string.txt_not_successful_response))
        }

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

        viewModel.endLoading.observe(this) {
            if (it) {
                binding.occultMessageLoading()
                fillMap()
            } else {
                binding.showMessageLoading(this)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            locationRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    requestLocationPermission()

                    connectivityManager.networkCallback(
                        ::callbackOnAvailable,
                        ::callbackOnLost
                    )
                } else {
                    animateCamera(origin)
                    connectivityManager.networkCallback(
                        ::callbackOnAvailable,
                        ::callbackOnLost
                    )
                }
            }
        }
    }

    private fun initMap(map: GoogleMap) {
        googleMap = map.apply {
            setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MainActivity, R.raw.map_style))
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isScrollGesturesEnabled = true
        }
    }

    private fun fillMap() {
        googleMap.apply {
            setOnCameraMoveListener {
                if (behaviorDetailsParades.state == BottomSheetBehavior.STATE_EXPANDED) {
                    behaviorDetailsParades.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }

            clear()

            addMarkersToMap(this@MainActivity, listPosVehicles, listParades)

            setOnMarkerClickListener {

                if (it.snippet == "veiculo" || it.title == "Localização Atual") {
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

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationRequestCode
            )
        } else {
            locationProviderClient.lastLocation.addOnSuccessListener {
                origin = LatLng(it.latitude, it.longitude)
                animateCamera(origin)

                connectivityManager.networkCallback(
                    ::callbackOnAvailable,
                    ::callbackOnLost
                )
            }
        }
    }

    private fun animateCameraWhenEndSearch() {
        if (listParades.isNotEmpty()) {
            listParades.first().let {
                animateCamera(LatLng(it.latitude, it.longitude))
            }

        } else if (listPosVehicles?.isNotEmpty() == true) {
            listPosVehicles?.first()?.let {
                animateCamera(LatLng(it.latitude, it.longitude))
            }
        }
    }

    private fun animateCamera(latLng: LatLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    private fun callbackOnAvailable() {
        lifecycleScope.launch {
            viewModel.authenticate(this@MainActivity)
            initMap(googleMap)
            binding.occultMessageProblem()
        }
    }

    private fun callbackOnLost() {
        lifecycleScope.launch {
            binding.showMessageProblem(resources.getString(R.string.txt_no_conection))
        }
    }

    private fun checkGpsStatus(activity: Activity) {
        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .build()

        val builder = LocationSettingsRequest.Builder().apply {
            addLocationRequest(locationRequest)
            setAlwaysShow(true)
        }

        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
                } catch (sendIntentException: IntentSender.SendIntentException) {

                }
            }
        }
    }

    private fun isGpsEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    companion object {
        private const val REQUEST_CHECK_SETTINGS = 1
    }
}
