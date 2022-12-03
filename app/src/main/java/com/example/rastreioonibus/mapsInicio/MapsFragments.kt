package com.example.rastreioonibus.mapsInicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rastreioonibus.databinding.LayoutMapsFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MapsFragments: Fragment() {
    private val viewModel: MapsViewModel by inject()

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

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

        scope.launch{
            viewModel.autenticar(requireContext())
        }
    }

    companion object{
        const val TAG_FRAGMENT_MAPS = "tagFragmentMaps"

        fun newInstance() = MapsFragments()
    }
}