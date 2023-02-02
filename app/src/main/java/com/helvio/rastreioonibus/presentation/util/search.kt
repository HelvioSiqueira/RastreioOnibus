package com.helvio.rastreioonibus.presentation.util

import com.helvio.rastreioonibus.databinding.SearchBusAndStopLayoutBinding
import com.helvio.rastreioonibus.presentation.map.MapsViewModel

fun MapsViewModel.search(
    binding: SearchBusAndStopLayoutBinding
) {
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