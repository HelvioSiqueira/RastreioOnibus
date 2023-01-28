package com.example.rastreioonibus.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.rastreioonibus.R
import com.example.rastreioonibus.databinding.ActivityMainBinding
import com.example.rastreioonibus.databinding.FilterLayoutBinding
import com.example.rastreioonibus.databinding.SearchLayoutBinding
import com.example.rastreioonibus.domain.model.Parades
import com.example.rastreioonibus.domain.model.Vehicles
import com.example.rastreioonibus.presentation.map.DetailsDialog
import com.example.rastreioonibus.presentation.map.MapsViewModel
import com.example.rastreioonibus.presentation.adapter.CustomPagerAdapter
import com.example.rastreioonibus.presentation.util.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

// Obter localização atual do usuario
// Exibir informações sobre as linhas
// Colocar anuncio no app
// Melhorar ui de lista de previsão de chegada
// Melhorar ui de formulario de filtro
// Mudar fonte das letras
class MainActivity : AppCompatActivity() {

    private val viewModel: MapsViewModel by inject()
    private lateinit var googleMap: GoogleMap

    private var listPosVehicles = listOf<Vehicles>()
    private var listParades = listOf<Parades>()

    private lateinit var binding: ActivityMainBinding
    private lateinit var behaviorDetailsParades: BottomSheetBehavior<LinearLayout>
    private lateinit var behaviorFilter: BottomSheetBehavior<ConstraintLayout>

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bindingFilter = FilterLayoutBinding.inflate(layoutInflater)
        val viewPager = binding.viewPager
        val bindingSearch = SearchLayoutBinding.inflate(layoutInflater)
        val tabLayout = binding.tabLayout

        val layouts = arrayOf(bindingFilter, bindingSearch)

        val adapter = CustomPagerAdapter(this@MainActivity, layouts)
        viewPager.adapter = adapter

        tabLayout.setupWithViewPager(viewPager)

        var layoutId = 0

        val fragmentMap = supportFragmentManager
            .findFragmentById(R.id.fragmentMap) as SupportMapFragment
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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

        bindingFilter.apply {
            inputTextParade.doOnTextChanged { text, _, _, count ->
                if (count > 0) {
                    layoutTextParadeLine.isEnabled = false
                } else if (text.isNullOrBlank()) {
                    layoutTextParadeLine.isEnabled = true
                }
            }
        }

        bindingFilter.apply {
            inputTextParadeLine.doOnTextChanged { text, _, _, count ->
                if (count > 0) {
                    layoutTextParade.isEnabled = false
                } else if (text.isNullOrBlank()) {
                    layoutTextParade.isEnabled = true
                }
            }
        }

        behaviorFilter.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    binding.fabFilter.setImageResource(R.drawable.baseline_filter_alt_24)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

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

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        binding.fabFilter.setImageResource(R.drawable.baseline_filter_alt_24)
                        layoutId = 0
                    }
                    1 -> {
                        binding.fabFilter.setImageResource(R.drawable.baseline_search_24)
                        layoutId = 1
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.fabFilter.setOnClickListener {
            if (behaviorFilter.state == BottomSheetBehavior.STATE_EXPANDED && layoutId == 0) {
                viewModel.search(bindingFilter)
            } else {
                behaviorDetailsParades.state = BottomSheetBehavior.STATE_HIDDEN
                behaviorFilter.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        fragmentMap.getMapAsync {
            googleMap = it
            connectivityManager.networkCallback(
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
            if (it.isNotEmpty()) {
                listPosVehicles = it
            }
        }
    }

    private fun initMap(map: GoogleMap) {
        val origin = LatLng(-23.561706, -46.655981)

        googleMap = map.apply {
            setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MainActivity, R.raw.map_style))
            animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 15F))
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isScrollGesturesEnabled = true
        }

        viewModel.error.observe(this) {
            binding.showMessageProblem(
                resources.getString(R.string.txt_not_successful_response)
            )
        }

        viewModel.endLoading.observe(this) {
            if (it) {
                binding.occultMessageLoading()
                fillMap(map)
            } else {
                binding.showMessageLoading(this)
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
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
            binding.occultMessageProblem()
        }
    }

    private fun callbackOnLost() {
        lifecycleScope.launch {
            binding.showMessageProblem(resources.getString(R.string.txt_no_conection))
        }
    }
}
