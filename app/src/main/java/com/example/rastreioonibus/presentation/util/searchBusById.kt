package com.example.rastreioonibus.presentation.util

import com.example.rastreioonibus.databinding.ActivityMainBinding
import com.example.rastreioonibus.databinding.FilterLayoutBinding
import com.example.rastreioonibus.presentation.map.MapsViewModel

fun MapsViewModel.search(binding: FilterLayoutBinding) {
    val textSearchBusLine = binding.inputTextLine.text.toString()
    val textSearchStopByLocation = binding.inputTextParade.text.toString()
    val textSearchStopByLine = binding.inputTextParadeLine.text.toString()

    textSearchBusLine.let {
        if (it.isNotBlank()) {
            this.getPosVehiclesByLine(it.toInt())
        }
    }

    textSearchStopByLocation.let {
        if (it.isNotBlank()) {
            this.getParades(it)
        }
    }

    textSearchStopByLine.let {
        if (it.isNotBlank()) {
            this.getParadesByLine(it.toInt())
        }
    }
}