package com.example.rastreioonibus.http

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.rastreioonibus.model.Linhas
import com.example.rastreioonibus.model.Paradas
import com.example.rastreioonibus.PosVeiculos
import com.example.rastreioonibus.PrevChegada
import retrofit2.Response

class HttpRepository(private val api: OlhoVivoApi) {

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

    suspend fun getPosVeiculos(): Response<PosVeiculos> {
        return api.getPosVeiculos(certificado)
    }

    suspend fun getLinhas(idLinha: Int): List<Linhas>? {
        return api.getLinhas(certificado, idLinha).body()
    }

    suspend fun getParadas(term: String): Response<List<Paradas>> {
        return api.getParadas(certificado, term)
    }

    suspend fun getPrevChegadas(idParada: Int): PrevChegada? {
        return api.getPrevChegadas(certificado, idParada).body()
    }

    fun setCertificado(cert: String){
        certificado = cert
    }
}