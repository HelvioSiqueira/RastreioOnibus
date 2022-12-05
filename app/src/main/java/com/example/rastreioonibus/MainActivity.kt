package com.example.rastreioonibus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.rastreioonibus.mapsInicio.MapsFragments

class MainActivity : AppCompatActivity() {

    private val fragmentMap: AppMapFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragmentMap) as AppMapFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentMap.getMapAsync {
            
        }

    }

    private fun showMapsFragment() {
        val fragment = MapsFragments.newInstance()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainLayout, fragment, MapsFragments.TAG_FRAGMENT_MAPS)
            .commit()
    }
}