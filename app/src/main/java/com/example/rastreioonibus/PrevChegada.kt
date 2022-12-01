package com.example.rastreioonibus

data class PrevChegada(
    val hr: String = "",
    val l: List<PosLinhas> = emptyList()
)
