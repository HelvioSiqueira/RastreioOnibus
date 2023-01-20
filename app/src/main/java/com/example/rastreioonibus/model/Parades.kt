package com.example.rastreioonibus.model

data class Parades(
    //Código identificador da parada
    val cp: Int = 0,

    //Nome da parada
    val np: String = "",

    // Endereço de localização da parada
    val ed: String = "",

    //Informação de latitude da localização da parada
    val py: Double = 0.0,

    //Informação de longitude da localização da parada
    val px: Double = 0.0
)