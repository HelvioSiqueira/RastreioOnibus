package com.example.rastreioonibus.http

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.example.rastreioonibus.Linhas
import retrofit2.Response
import java.lang.Exception

class HttpUtils(private val api: RastreioOnibusApi) {

    private var certificado: String = ""

    suspend fun autenticar(context: Context): Response<Boolean> {
        val ai: ApplicationInfo = context.packageManager
            .getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )

        val key = ai.metaData["keyValue"]

        return api.autenticar(key.toString())
    }

    suspend fun getLinhas(): List<Linhas>? {
        return api.getLinhas(certificado).body()
    }

    fun setCertificado(cert: String){
        certificado = cert
    }
}