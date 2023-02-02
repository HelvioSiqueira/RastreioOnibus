package com.helvio.rastreioonibus.presentation.util

import android.content.Context
import com.helvio.rastreioonibus.databinding.ActivityMainBinding
import com.helvio.rastreioonibus.databinding.SearchBusAndStopLayoutBinding
import com.helvio.rastreioonibus.databinding.SearchLinesLayoutBinding
import com.helvio.rastreioonibus.presentation.adapter.CustomPagerAdapter

fun ActivityMainBinding.setupTabAndPager(
    bindingSearchStopAndVehicles: SearchBusAndStopLayoutBinding,
    bindingSearchLines: SearchLinesLayoutBinding,
    context: Context
){

    val layouts = arrayOf(bindingSearchStopAndVehicles, bindingSearchLines)


    val adapter = CustomPagerAdapter(context, layouts)
    viewPager.adapter = adapter

    tabLayout.setupWithViewPager(viewPager)
}