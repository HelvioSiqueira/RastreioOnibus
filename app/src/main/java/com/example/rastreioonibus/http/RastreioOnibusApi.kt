package com.example.rastreioonibus.http

import com.example.rastreioonibus.Linhas
import com.example.rastreioonibus.Paradas
import com.example.rastreioonibus.PosVeiculos
import com.example.rastreioonibus.PrevChegada
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface RastreioOnibusApi {

    @GET("/Posicao")
    fun getPosVeiculos(): Response<PosVeiculos>

    @GET("/Linha")
    fun getLinhas(): Response<Linhas>

    @GET("/Parada")
    fun getParadas(): Response<Paradas>

    @GET("/Previsao?codigoParada={codigoParada}")
    fun getPrevChegadas(): Response<PrevChegada>

    @POST("/Login/Autenticar?token=4c4cf3618c920d03c3882cc024eaa1d37736f9f462ba72b31740d8acfe8e8ab9")
    fun autenticar()
}