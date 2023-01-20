package com.example.rastreioonibus

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rastreioonibus.databinding.ActivityMainBinding
import com.mancj.materialsearchbar.MaterialSearchBar

class MainActivity :
    AppCompatActivity(),
    MaterialSearchBar.OnSearchActionListener{

    private val fragmentMap: AppMapFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragmentMap) as AppMapFragment
    }

    private lateinit var searchBar: MaterialSearchBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchBar = binding.searchBar

        searchBar.inflateMenu(R.menu.menu)

        searchBar.menu.setOnMenuItemClickListener{
            onItemClicked(it)
            true
        }

        fragmentMap.getMapAsync {

        }
    }

    override fun onSearchStateChanged(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onSearchConfirmed(text: CharSequence?) {
        TODO("Not yet implemented")
    }

    override fun onButtonClicked(buttonCode: Int) {
        TODO("Not yet implemented")
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
