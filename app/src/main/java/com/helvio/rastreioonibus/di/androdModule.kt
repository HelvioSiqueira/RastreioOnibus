package com.helvio.rastreioonibus.di

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import com.helvio.rastreioonibus.data.http.OlhoVivoApi
import com.helvio.rastreioonibus.data.repository.HttpRepository
import com.helvio.rastreioonibus.data.util.API
import com.helvio.rastreioonibus.domain.usecase.*
import com.helvio.rastreioonibus.presentation.map.MapsViewModel
import com.google.gson.GsonBuilder
import com.helvio.rastreioonibus.BuildConfig
import com.helvio.rastreioonibus.presentation.util.ConnectivityState
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val androidModule = module {
    single { this }

    single {
        HttpRepository(api = get())
    }

    single {
        MapsViewModel(manager = get(), app = androidApplication())
    }

    single {
        AuthenticationUseCase(repo = get())
    }

    single {
        GetPosVehiclesUseCase(repo = get())
    }

    single {
        GetPosVehiclesByLineUseCase(repo = get())
    }

    single {
        GetParadesUseCase(repo = get())
    }

    single {
        GetParadesByLineUseCase(repo = get())
    }

    single {
        GetPrevArrivalUseCase(repo = get())
    }

    single {
        GetLinesUseCase(repo = get())
    }

    single {
        RastreioOnibusManagerUseCase(
            authenticate = get(),
            getPosVehicles = get(),
            getPosVehiclesByLineUseCase = get(),
            getParades = get(),
            getParadesByLineUseCase = get(),
            getPrevArrival = get(),
            getLines = get()
        )
    }

    single {
        val logLevel =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        val interceptor = HttpLoggingInterceptor().setLevel(logLevel)

        val httpClient = OkHttpClient.Builder().addInterceptor(interceptor)

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

    single {
        ConnectivityState(
            connectivityManager = androidApplication().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        )
    }
}