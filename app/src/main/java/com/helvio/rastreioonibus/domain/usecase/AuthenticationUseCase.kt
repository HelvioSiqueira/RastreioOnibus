package com.helvio.rastreioonibus.domain.usecase

import android.content.Context
import android.util.Log
import com.helvio.rastreioonibus.data.repository.HttpRepository

class AuthenticationUseCase(
    private val repo: HttpRepository
) {

    suspend operator fun invoke(context: Context): Boolean {
        var isAuthenticate = false

        try {
            val certificate = repo.authenticator(context).headers().toMultimap()["Set-Cookie"]?.first()

            if (certificate != null) {
                repo.setCertificate(certificate)
                isAuthenticate = true

            } else {
                Log.d("HSV", "Cookie nulo")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isAuthenticate = false
        }

        return isAuthenticate
    }
}