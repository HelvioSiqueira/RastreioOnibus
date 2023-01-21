package com.example.rastreioonibus.presentation

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rastreioonibus.R
import com.example.rastreioonibus.databinding.ActivityMainBinding
import com.example.rastreioonibus.presentation.map.AppMapFragment
import com.mancj.materialsearchbar.MaterialSearchBar

class MainActivity :
    AppCompatActivity(){

    private val fragmentMap: AppMapFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragmentMap) as AppMapFragment
    }

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        fragmentMap.getMapAsync {

        }
    }

    private fun onItemClicked(menuItem: MenuItem){
        when (menuItem.itemId) {
            R.id.btnSearchLinhas -> {
                Toast.makeText(this, "Pesquisa Linhas", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
}
