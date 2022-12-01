package com.example.rastreioonibus.http

import com.example.rastreioonibus.KEY
import retrofit2.Response

class HttpUtils(private val api: RastreioOnibusApi) {

    suspend fun autenticar(): Response<Boolean> {
        return api.autenticar(KEY)
    }
}