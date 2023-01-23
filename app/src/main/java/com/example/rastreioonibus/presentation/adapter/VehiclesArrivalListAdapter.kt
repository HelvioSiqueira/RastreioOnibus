package com.example.rastreioonibus.presentation.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rastreioonibus.domain.model.PrevVehicle

class VehiclesArrivalListAdapter() :
    ListAdapter<PrevVehicle, VehiclesArrivalListAdapter.PrevVehicleViewHolder>(VehiclesArrivalListAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrevVehicleViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: PrevVehicleViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    inner class PrevVehicleViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

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