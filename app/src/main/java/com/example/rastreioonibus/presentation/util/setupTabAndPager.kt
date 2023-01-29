package com.example.rastreioonibus.presentation.util

import android.content.Context
import com.example.rastreioonibus.databinding.ActivityMainBinding
import com.example.rastreioonibus.databinding.SearchBusAndStopLayoutBinding
import com.example.rastreioonibus.databinding.SearchLinesLayoutBinding
import com.example.rastreioonibus.presentation.adapter.CustomPagerAdapter

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