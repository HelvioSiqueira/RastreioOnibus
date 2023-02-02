package com.helvio.rastreioonibus.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.helvio.rastreioonibus.data.http.OlhoVivoApi
import com.helvio.rastreioonibus.domain.model.*
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

    suspend fun getPosVehicles(): Response<PosVehicles> =
        api.getPosVehicles(certificado)

    suspend fun getPosVehiclesByLine(idLine: Int): Response<PosVehiclesByLines> =
        api.getPosVehiclesByLine(certificado, idLine)

    suspend fun getLines(term: String): Response<List<Lines>> =
        api.getLines(certificado, term)

    suspend fun getParades(term: String): Response<List<Parades>> =
        api.getParades(certificado, term)

    suspend fun getParadesByLine(idLine: Int): Response<List<Parades>> =
        api.getParadesByLine(certificado, idLine)

    suspend fun getPrevArrival(idParade: Int): Response<PrevArrival> =
        api.getPrevArrival(certificado, idParade)

    fun setCertificate(cert: String) {
        certificado = cert
    }
}