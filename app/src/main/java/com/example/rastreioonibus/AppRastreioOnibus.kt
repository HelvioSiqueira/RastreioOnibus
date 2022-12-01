package com.example.rastreioonibus

import android.app.Application
import com.example.rastreioonibus.di.androidModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AppRastreioOnibus: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@AppRastreioOnibus)
            modules(androidModule)
        }
    }
}