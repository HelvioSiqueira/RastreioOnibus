package com.example.rastreioonibus

import android.content.Context
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rastreioonibus.http.HttpRepository
import com.example.rastreioonibus.model.Parades
import com.example.rastreioonibus.model.PosLines
import com.example.rastreioonibus.model.Vehicles
import kotlinx.coroutines.launch
import java.lang.Exception

class MapsViewModel(private val repo: HttpRepository) : ViewModel() {

    val error = MutableLiveData<String>()

    val listPosVehicles = MutableLiveData<List<Vehicles>>()
    val listParades = MutableLiveData<List<Parades>>()

    val isAuthenticate = MutableLiveData<Boolean>().apply {
        value = false
    }

    val isLoading = MediatorLiveData<Boolean>().apply {
        addSource(listPosVehicles) {
            this.value =
                it.isNotEmpty() && listParades.value?.isNotEmpty() ?: false
        }
        addSource(listParades) {
            this.value =
                it.isNotEmpty() && listPosVehicles.value?.isNotEmpty() ?: false
        }
    }

    fun authenticate(context: Context) {
        viewModelScope.launch {
            try {
                val certificate = repo.authenticator(context).headers()["Set-Cookie"]

                if (certificate != null) {
                    repo.setCertificate(certificate)
                    isAuthenticate.postValue(true)

                } else {
                    Log.d("HSV", "Cookie nulo")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getPosVehicles() {
        viewModelScope.launch {
            val response = repo.getPosVehicles()

            if (response.isSuccessful) {
                listPosVehicles.value =
                    response.body()?.l?.flatMap(PosLines::vs) ?: emptyList()

            } else {
                haveError(response.message())
            }
        }
    }

    fun getParades(term: String) {
        viewModelScope.launch {
            val response = repo.getParades(term)

            if (response.isSuccessful) {
                listParades.value =
                    response.body() ?: emptyList()

            } else {
                haveError(response.message())
            }
        }
    }

    fun getSelectedParade(id: String) = listParades.value?.find { it.cp == id.toInt() }

    fun getSelectedVehicle(id: String) = listPosVehicles.value?.find { it.p == id }

    private fun haveError(error: String) {
        this.error.value = error
    }
}