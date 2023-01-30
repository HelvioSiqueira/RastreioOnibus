package com.example.rastreioonibus.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rastreioonibus.R
import com.example.rastreioonibus.databinding.ItemVehicleBinding
import com.example.rastreioonibus.domain.model.PrevVehicle

class VehiclesListAdapter(
    private val context: Context
) :
    ListAdapter<PrevVehicle, VehiclesListAdapter.LinesViewHolder>(VehiclesListAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LinesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_vehicle, parent, false)
        )

    override fun onBindViewHolder(holder: LinesViewHolder, position: Int) {
        holder.apply {
            val line = getItem(position)
            bind(line)
        }
    }

    inner class LinesViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val binding = ItemVehicleBinding.bind(itemView)

        fun bind(vehicle: PrevVehicle) =
            binding.apply {
                txtPrefixVehicle.text =
                    context.resources.getString(R.string.txt_sign_bus, vehicle.prefixOfVehicle)
                txtExpectedTime.text = context.resources.getString(
                    R.string.txt_arrival_forecast,
                    vehicle.expectedArrivalTime
                )
            }
    }

    private companion object : DiffUtil.ItemCallback<PrevVehicle>() {

        override fun areItemsTheSame(
            oldItem: PrevVehicle,
            newItem: PrevVehicle
        ): Boolean {
            return oldItem.prefixOfVehicle == newItem.prefixOfVehicle
        }

        override fun areContentsTheSame(
            oldItem: PrevVehicle,
            newItem: PrevVehicle
        ): Boolean {
            return oldItem == newItem
        }
    }
}