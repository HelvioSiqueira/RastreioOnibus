package com.example.rastreioonibus

import android.content.Context
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rastreioonibus.http.HttpRepository
import com.example.rastreioonibus.model.Paradas
import kotlinx.coroutines.launch
import java.lang.Exception

class MapsViewModel(private val repo: HttpRepository) : ViewModel() {

    val error = MutableLiveData<String>()

    val listPosVeiculos = MutableLiveData<List<Veiculos>>()
    val listParadas = MutableLiveData<List<Paradas>>()

    val isAutenticate = MutableLiveData<Boolean>().apply {
        value = false
    }

    val mediator = MediatorLiveData<Boolean>().apply {
        addSource(listPosVeiculos) {
            this.value =
                it.isNotEmpty() && listParadas.value?.isNotEmpty() ?: false
        }
        addSource(listParadas) {
            this.value =
                it.isNotEmpty() && listPosVeiculos.value?.isNotEmpty() ?: false
        }
    }

    fun autenticar(context: Context) {
        viewModelScope.launch {
            try {
                val certificado = repo.autenticar(context).headers()["Set-Cookie"]

                if (certificado != null) {
                    repo.setCertificado(certificado)
                    isAutenticate.postValue(true)

                } else {
                    Log.d("HSV", "Cookie nulo")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getPosVeiculos() {
        viewModelScope.launch {
            val response = repo.getPosVeiculos()

            if (response.isSuccessful) {
                listPosVeiculos.value =
                    response.body()?.l?.flatMap(PosLinhas::vs) ?: emptyList()

            } else {
                haveError(response.message())
            }
        }
    }

    fun getParadas(term: String) {
        viewModelScope.launch {
            val response = repo.getParadas(term)

            if (response.isSuccessful) {
                listParadas.value =
                    response.body() ?: emptyList()

            } else {
                haveError(response.message())
            }
        }
    }

    private fun haveError(error: String) {
        this.error.value = error
    }
}