package com.example.rastreioonibus.http

import com.example.rastreioonibus.Linhas
import com.example.rastreioonibus.Paradas
import com.example.rastreioonibus.PosVeiculos
import com.example.rastreioonibus.PrevChegada
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RastreioOnibusApi {

    @GET("Posicao")
    fun getPosVeiculos(): Response<PosVeiculos>

    @GET("/Linha/Buscar?termosBusca=8000")
    suspend fun getLinhas(): Response<List<Linhas>>

    @GET("Parada")
    fun getParadas(): Response<Paradas>

    @GET("Previsao?codigoParada={codigoParada}")
    fun getPrevChegadas(): Response<PrevChegada>

    @POST("Login/Autenticar")
    suspend fun autenticar(@Query("token") token: String): Response<Boolean>
}