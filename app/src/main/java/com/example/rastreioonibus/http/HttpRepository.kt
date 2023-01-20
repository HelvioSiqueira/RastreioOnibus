package com.example.rastreioonibus.http

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.rastreioonibus.model.Lines
import com.example.rastreioonibus.model.Parades
import com.example.rastreioonibus.model.PosVehicles
import com.example.rastreioonibus.model.PrevArrival
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

    suspend fun getLines(idLinha: Int): List<Lines>? {
        return api.getLines(certificado, idLinha).body()
    }

    suspend fun getParades(term: String): Response<List<Parades>> {
        return api.getParades(certificado, term)
    }

    suspend fun getPrevArrival(idParada: Int): PrevArrival? {
        return api.getPrevArrival(certificado, idParada).body()
    }

    fun setCertificate(cert: String){
        certificado = cert
    }
}