package com.example.rastreioonibus.presentation.map

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rastreioonibus.domain.model.*
import com.example.rastreioonibus.domain.usecase.RastreioOnibusManagerUseCase
import kotlinx.coroutines.launch

class MapsViewModel(
    private val manager: RastreioOnibusManagerUseCase
) : ViewModel() {

    val error = MutableLiveData<String>()

    val listPosVehicles = MutableLiveData<List<Vehicles>>()
    val listParades = MutableLiveData<List<Parades>>()
    val listOfArrivalLines = MutableLiveData<List<PrevLine>>()

    val isAuthenticate = MutableLiveData<Boolean>().apply {
        value = false
    }

    val endLoading = MediatorLiveData<Boolean>().apply {
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
            isAuthenticate.value = manager.authenticate(context)
        }
    }

    fun getPosVehicles() {
        viewModelScope.launch {
            listPosVehicles.value = manager.getPosVehicles(::haveError)
        }
    }

    fun getPosVehiclesByLine(idLine: Int) {
        viewModelScope.launch {
            listPosVehicles.value = manager.getPosVehiclesByLineUseCase(::haveError, idLine)
        }
    }

    fun getParades(term: String) {
        viewModelScope.launch {
            listParades.value = manager.getParades(::haveError, term)
        }
    }

    fun getParadesByLine(idLine: Int) {
        viewModelScope.launch {
            listParades.value = manager.getParadesByLineUseCase(::haveError, idLine)
        }
    }

    fun getArrivalVehicles(id: Int) {
        viewModelScope.launch {
            listOfArrivalLines.value =
                manager.getPrevArrival(::haveError, id)?.pointOfParade?.lines
        }
    }

    fun getSelectedParade(id: String) = listParades.value?.find { it.codeOfParade == id.toInt() }

    fun getSelectedVehicle(id: String) = listPosVehicles.value?.find { it.prefixOfVehicle == id }

    private fun haveError(error: String) {
        this.error.value = error
    }
}