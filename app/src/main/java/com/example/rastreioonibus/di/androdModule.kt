package com.example.rastreioonibus.di

import com.example.rastreioonibus.utils.API
import com.example.rastreioonibus.http.HttpRepository
import com.example.rastreioonibus.http.OlhoVivoApi
import com.example.rastreioonibus.mapsInicio.MapsViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import com.google.gson.GsonBuilder
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

val androidModule = module {
    single { this }

    factory {
        HttpRepository(api = get())
    }

    factory {
        MapsViewModel(repo = get())
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

        retrofit.create(OlhoVivoApi::class.java)
    }
}