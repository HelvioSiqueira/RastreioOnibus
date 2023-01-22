package com.example.rastreioonibus.di

import com.example.rastreioonibus.data.http.OlhoVivoApi
import com.example.rastreioonibus.data.repository.HttpRepository
import com.example.rastreioonibus.data.util.API
import com.example.rastreioonibus.domain.usecase.AuthenticationUseCase
import com.example.rastreioonibus.domain.usecase.GetParadesUseCase
import com.example.rastreioonibus.domain.usecase.GetPosVehiclesUseCase
import com.example.rastreioonibus.domain.usecase.RastreioOnibusManagerUseCase
import com.example.rastreioonibus.presentation.map.MapsViewModel
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val androidModule = module {
    single { this }

    single {
        HttpRepository(api = get())
    }

    single {
        MapsViewModel(manager = get())
    }

    single {
        AuthenticationUseCase(repo = get())
    }

    single {
        GetPosVehiclesUseCase(repo = get())
    }

    single {
        GetParadesUseCase(repo = get())
    }

    single {
        RastreioOnibusManagerUseCase(
            authenticate = get(),
            getPosVehicles = get(),
            getParades = get()
        )
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