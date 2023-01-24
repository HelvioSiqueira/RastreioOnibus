package com.example.rastreioonibus.presentation.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rastreioonibus.databinding.LayoutDetailsBinding
import com.example.rastreioonibus.presentation.adapter.LinesListAdapter
import kotlinx.coroutines.android.awaitFrame
import org.koin.android.ext.android.inject

class DetailsDialog : Fragment() {

    private val viewModel: MapsViewModel by inject()

    private lateinit var npParade: TextView
    private lateinit var edParade: TextView

    private lateinit var binding: LayoutDetailsBinding

    private lateinit var adapter: LinesListAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutDetailsBinding.inflate(layoutInflater)
        npParade = binding.npParada
        edParade = binding.edParada

        val idParadeOrVehicle = arguments?.getString(EXTRA_TITLE)
        val parade = viewModel.getSelectedParade(idParadeOrVehicle ?: "706334")

        npParade.text = parade?.nameOfParade
        edParade.text = parade?.addressOfParade

        viewModel.getArrivalVehicles(parade?.codeOfParade ?: 0)

        initLinesListAdapter()

        viewModel.listOfArrivalLines.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root
    }

    private fun initLinesListAdapter() {
        adapter = LinesListAdapter(requireContext())
        recyclerView = binding.rvListLines
        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    companion object {
        const val DIALOG_TAG = "detailsDialog"
        private const val EXTRA_TITLE = "title"

        fun newInstance(idParadeOrVehicle: String) = DetailsDialog().apply {
            arguments = Bundle().apply {
                putString(EXTRA_TITLE, idParadeOrVehicle)
            }
        }
    }
}