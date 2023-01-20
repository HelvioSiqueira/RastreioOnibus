package com.example.rastreioonibus.model

import com.google.gson.annotations.SerializedName

data class PosVehicles(
    @SerializedName("hr")
    val hourReference: String = "",

    @SerializedName("l")
    val lines: List<PosLines> = emptyList()
)

