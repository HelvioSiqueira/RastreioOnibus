package com.example.rastreioonibus.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rastreioonibus.R
import com.example.rastreioonibus.databinding.ActivityMainBinding
import com.example.rastreioonibus.databinding.SearchBusAndStopLayoutBinding
import com.example.rastreioonibus.databinding.SearchLinesLayoutBinding
import com.example.rastreioonibus.domain.model.Parades
import com.example.rastreioonibus.domain.model.Vehicles
import com.example.rastreioonibus.presentation.adapter.CustomPagerAdapter
import com.example.rastreioonibus.presentation.adapter.SearchLinesAdapter
import com.example.rastreioonibus.presentation.map.DetailsDialog
import com.example.rastreioonibus.presentation.map.MapsViewModel
import com.example.rastreioonibus.presentation.util.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

// Obter localização atual do usuario
// Fazer com camera vá para local pesquisado
class MainActivity : AppCompatActivity() {

    private val viewModel: MapsViewModel by inject()
    private lateinit var googleMap: GoogleMap

    private var listPosVehicles: List<Vehicles>? = listOf()
    private var listParades = listOf<Parades>()

    private lateinit var binding: ActivityMainBinding
    private lateinit var searchAdapter: SearchLinesAdapter
    private lateinit var behaviorDetailsParades: BottomSheetBehavior<LinearLayout>
    private lateinit var behaviorFilter: BottomSheetBehavior<ConstraintLayout>

    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var locationProviderClient: FusedLocationProviderClient
    private lateinit var origin: LatLng

    private lateinit var connectivityManager: ConnectivityManager

    private var hasFilled = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this)
        binding.adView.loadAd(AdRequest.Builder().build())

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()

        val bindingSearchStopAndVehicles = SearchBusAndStopLayoutBinding.inflate(layoutInflater)
        val bindingSearchLines = SearchLinesLayoutBinding.inflate(layoutInflater)
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val layouts = arrayOf(bindingSearchStopAndVehicles, bindingSearchLines)

        val adapter = CustomPagerAdapter(this@MainActivity, layouts)
        viewPager.adapter = adapter

        tabLayout.setupWithViewPager(viewPager)

        var layoutId = 0

        val fragmentMap = supportFragmentManager
            .findFragmentById(R.id.fragmentMap) as SupportMapFragment
        connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        initSearchLinesAdapter(bindingSearchLines)
        setUrlOnPrivacyPolicy(bindingSearchStopAndVehicles)

        behaviorDetailsParades =
            BottomSheetBehavior.from(binding.bottomSheetDetailsParades).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
        behaviorFilter = BottomSheetBehavior.from(binding.bottomSheetFilter).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        if (!connectivityManager.haveInternetOnInitApp()) {
            binding.showMessageProblem(resources.getString(R.string.txt_no_conection))
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

        binding.fabFilter.setOnClickListener {
            if (behaviorFilter.state == BottomSheetBehavior.STATE_EXPANDED) {

                when (layoutId) {
                    0 -> viewModel.search(bindingSearchStopAndVehicles)
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
            }
        }

        viewModel.isListParadesEmpty.observe(this) {
            if (it) {
                Toast.makeText(this, "Paradas não encotrados", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.listLines.observe(this) {
            searchAdapter.submitList(it)
        }

        viewModel.error.observe(this) {
            binding.showMessageProblem(
                resources.getString(R.string.txt_not_successful_response)
            )
        }

        fragmentMap.getMapAsync {
            googleMap = it
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    requestLocationPermission()

                    connectivityManager.networkCallback(
                        ::callbackOnAvailable,
                        ::callbackOnLost
                    )
                } else {
                    origin = LatLng(-23.561706, -46.655981)
                    animateCamera(origin)

                    connectivityManager.networkCallback(
                        ::callbackOnAvailable,
                        ::callbackOnLost
                    )
                }
            }
        }
    }

    private fun initSearchLinesAdapter(bindingSearch: SearchLinesLayoutBinding) {
        searchAdapter = SearchLinesAdapter(this)
        val rv = bindingSearch.rvSearch

        rv.adapter = searchAdapter
        rv.layoutManager = LinearLayoutManager(this)
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
        }

        viewModel.listPosVehicles.observe(this) {
            listPosVehicles = it
        }
    }

    private fun initMap(map: GoogleMap) {
        googleMap = map.apply {
            setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MainActivity, R.raw.map_style))
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isScrollGesturesEnabled = true
        }

        viewModel.endLoading.observe(this) {
            if (it) {
                binding.occultMessageLoading()

                if(!hasFilled){
                    fillMap(map)
                    hasFilled = true
                }
            } else {
                binding.showMessageLoading(this)
            }
        }
    }

    private fun fillMap(googleMap: GoogleMap) {
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
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE
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

    private fun animateCamera(latLng: LatLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    private fun setUrlOnPrivacyPolicy(bindingSearchStopAndVehicles: SearchBusAndStopLayoutBinding) {
        val textUrl = "Politica de Privacidade"
        val spannable = SpannableString(textUrl)

        spannable.setSpan(
            URLSpan("https://www.freeprivacypolicy.com/live/a1bcd0d2-b1e0-42af-8c12-81dd84fa82cc"),
            textUrl.indexOf(textUrl),
            textUrl.indexOf(textUrl) + textUrl.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )

        bindingSearchStopAndVehicles.txtPrivacyPolicy.apply {
            movementMethod = LinkMovementMethod.getInstance()
            text = spannable
        }
    }

    private fun callbackOnAvailable() {
        lifecycleScope.launch {
            initLists()
            initMap(googleMap)
            binding.occultMessageProblem()
        }
    }

    private fun callbackOnLost() {
        lifecycleScope.launch {
            binding.showMessageProblem(resources.getString(R.string.txt_no_conection))
        }
    }
}
