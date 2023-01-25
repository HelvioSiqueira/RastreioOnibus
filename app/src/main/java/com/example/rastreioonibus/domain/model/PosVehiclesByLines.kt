package com.example.rastreioonibus.domain.model

import com.google.gson.annotations.SerializedName

data class PosVehiclesByLines(

    @SerializedName("vs")
    val listOfVehicles: List<Vehicles> = emptyList()
)
