package com.example.rastreioonibus.http

import com.example.rastreioonibus.Linhas
import com.example.rastreioonibus.Paradas
import com.example.rastreioonibus.PosVeiculos
import com.example.rastreioonibus.PrevChegada
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface RastreioOnibusApi {

    @GET("Posicao")
    fun getPosVeiculos(@Header("Cookie") certificado: String): Response<PosVeiculos>

    @GET("/Linha/Buscar?termosBusca=8000")
    suspend fun getLinhas(@Header("Cookie") certificado: String): Response<List<Linhas>>

    @GET("Parada")
    fun getParadas(@Header("Cookie") certificado: String): Response<Paradas>

    @GET("Previsao?codigoParada={codigoParada}")
    fun getPrevChegadas(@Header("Cookie") certificado: String): Response<PrevChegada>

    @POST("Login/Autenticar")
    suspend fun autenticar(@Query("token") token: String): Response<Boolean>
}