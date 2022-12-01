package com.example.rastreioonibus.di

import com.example.rastreioonibus.API
import com.example.rastreioonibus.http.HttpUtils
import com.example.rastreioonibus.http.RastreioOnibusApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.dsl.module
import com.google.gson.GsonBuilder
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.create

val androidModule = module {
    single { this }

    factory {
        HttpUtils(api = get())
    }

    single {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(API)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()

        retrofit.create(RastreioOnibusApi::class.java)
    }
}