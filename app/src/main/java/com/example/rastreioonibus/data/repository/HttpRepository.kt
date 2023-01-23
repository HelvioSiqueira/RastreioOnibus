package com.example.rastreioonibus.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.rastreioonibus.data.http.OlhoVivoApi
import com.example.rastreioonibus.domain.model.Lines
import com.example.rastreioonibus.domain.model.Parades
import com.example.rastreioonibus.domain.model.PosVehicles
import com.example.rastreioonibus.domain.model.PrevArrival
import retrofit2.Response

class HttpRepository(private val api: OlhoVivoApi) {

    private var certificado: String = ""

    suspend fun authenticator(context: Context): Response<Boolean> {
        val ai: ApplicationInfo = context.packageManager
            .getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )

        val key = ai.metaData["keyValue"]

        return api.authenticate(key.toString())
    }

    suspend fun getPosVehicles(): Response<PosVehicles> {
        return api.getPosVehicles(certificado)
    }

    suspend fun getLines(idLine: Int): Response<List<Lines>> {
        return api.getLines(certificado, idLine)
    }

    suspend fun getParades(term: String): Response<List<Parades>> {
        return api.getParades(certificado, term)
    }

    suspend fun getPrevArrival(idParade: Int): Response<PrevArrival> {
        return api.getPrevArrival(certificado, idParade)
    }

    fun setCertificate(cert: String){
        certificado = cert
    }
}