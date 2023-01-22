package com.example.rastreioonibus.domain.usecase

import android.content.Context
import android.util.Log
import com.example.rastreioonibus.data.repository.HttpRepository

class AuthenticationUseCase(
    private val repo: HttpRepository
) {

    suspend operator fun invoke(context: Context): Boolean {
        var isAuthenticate = false

        try {
            val certificate = repo.authenticator(context).headers()["Set-Cookie"]

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