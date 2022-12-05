package com.example.rastreioonibus.mapsInicio

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.rastreioonibus.PosLinhas
import com.example.rastreioonibus.Veiculos
import com.example.rastreioonibus.http.HttpRepository
import com.example.rastreioonibus.model.Paradas
import java.lang.Exception

class MapsViewModel(private val repo: HttpRepository): ViewModel() {

    suspend fun autenticar(context: Context){
        try{
            val certificado = repo.autenticar(context).headers()["Set-Cookie"]

            if(certificado != null){
                repo.setCertificado(certificado)
            } else {
                Log.d("HSV", "Cookie nulo")
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    suspend fun getPosVeiculos(): List<Veiculos> {
        return repo.getPosVeiculos()!!.l.flatMap(PosLinhas::vs)
    }

    suspend fun getParadas(term: String): List<Paradas> {
        return repo.getParadas(term) ?: emptyList()
    }
}