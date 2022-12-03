package com.example.rastreioonibus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rastreioonibus.databinding.LayoutMapsFragmentBinding
import com.google.android.gms.maps.MapFragment

class MapsFragments: Fragment() {
    private val binding: LayoutMapsFragmentBinding by lazy {
        LayoutMapsFragmentBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object{
        const val TAG_FRAGMENT_MAPS = "tagFragmentMaps"

        fun newInstance() = MapsFragments()
    }
}