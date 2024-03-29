package com.helvio.rastreioonibus.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.helvio.rastreioonibus.R
import com.helvio.rastreioonibus.databinding.ItemLineBinding
import com.helvio.rastreioonibus.domain.model.PrevLine
import com.helvio.rastreioonibus.domain.model.PrevVehicle

class LinesListAdapter(
    private val context: Context
) : ListAdapter<PrevLine, LinesListAdapter.LinesViewHolder>(LinesListAdapter) {

    private var listOfVehicles = listOf<PrevVehicle>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LinesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_line, parent, false)
        )

    override fun onBindViewHolder(holder: LinesViewHolder, position: Int) {
        holder.apply {
            val line = getItem(position)
            listOfVehicles = line.vehicles
            bind(line)
        }
    }

    inner class LinesViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val binding = ItemLineBinding.bind(itemView)
        private lateinit var adapter: VehiclesListAdapter
        private lateinit var recyclerView: RecyclerView

        fun bind(line: PrevLine) = binding.apply {
            txtSign.text = line.signOfLine
            txtOrigin.text = line.singOfOriginOfLine
            txtDestiny.text = line.signOfDestinyOfLine

            initRvVehicles(rvListVehicles)

            adapter.submitList(line.vehicles)
        }

        private fun initRvVehicles(rvListVehicles: RecyclerView) {
            adapter = VehiclesListAdapter(context)
            recyclerView = rvListVehicles
            recyclerView.adapter = adapter

            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    recyclerView.context,
                    DividerItemDecoration.VERTICAL
                )
            )

            recyclerView.layoutManager = LinearLayoutManager(context)
        }
    }

    private companion object : DiffUtil.ItemCallback<PrevLine>() {

        override fun areItemsTheSame(
            oldItem: PrevLine,
            newItem: PrevLine
        ): Boolean {
            return oldItem.codeOfLine == newItem.codeOfLine
        }

        override fun areContentsTheSame(
            oldItem: PrevLine,
            newItem: PrevLine
        ): Boolean {
            return oldItem == newItem
        }
    }
}