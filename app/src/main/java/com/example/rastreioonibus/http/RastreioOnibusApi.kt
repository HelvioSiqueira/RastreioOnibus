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
    suspend fun getPosVeiculos(@Header("Cookie") certificado: String): Response<PosVeiculos>

    @GET("Linha/Buscar")
    suspend fun getLinhas(@Header("Cookie") certificado: String, @Query("termosBusca") linha: Int): Response<List<Linhas>>

    @GET("Parada/Buscar")
    suspend fun getParadas(@Header("Cookie") certificado: String, @Query("termosBusca") termo: String): Response<List<Paradas>>

    @GET("Previsao/Parada")
    suspend fun getPrevChegadas(@Header("Cookie") certificado: String, @Query("codigoParada") codigo: Int): Response<PrevChegada>

    @POST("Login/Autenticar")
    suspend fun autenticar(@Query("token") token: String): Response<Boolean>
}