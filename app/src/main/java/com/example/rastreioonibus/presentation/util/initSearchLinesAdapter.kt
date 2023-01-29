package com.example.rastreioonibus.presentation.util

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rastreioonibus.databinding.SearchLinesLayoutBinding
import com.example.rastreioonibus.presentation.adapter.SearchLinesAdapter

fun SearchLinesLayoutBinding.initSearchLinesAdapter(
    context: Context,
    searchAdapter: SearchLinesAdapter
) {
    val rv = this.rvSearch
    rv.adapter = searchAdapter
    rv.layoutManager = LinearLayoutManager(context)
}