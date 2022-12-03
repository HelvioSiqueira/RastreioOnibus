package com.example.rastreioonibus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.rastreioonibus.mapsInicio.MapsFragments

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showMapsFragment()
    }

    private fun showMapsFragment(){
        val fragment = MapsFragments.newInstance()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainLayout, fragment, MapsFragments.TAG_FRAGMENT_MAPS)
            .commit()
    }
}