package com.helvio.rastreioonibus.presentation.map

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.helvio.rastreioonibus.domain.model.*
import com.helvio.rastreioonibus.domain.usecase.RastreioOnibusManagerUseCase
import kotlinx.coroutines.launch

class MapsViewModel(
    private val manager: RastreioOnibusManagerUseCase
) : ViewModel() {

    val error = MutableLiveData<String>()

    val listPosVehicles = MutableLiveData<List<Vehicles>>()
    var listPosVehiclesAndLine: PosVehicles? = null
    val listParades = MutableLiveData<List<Parades>>()
    val listOfArrivalLines = MutableLiveData<List<PrevLine>>()
    var listLines = MutableLiveData<List<Lines>>()

    val isListPosVehiclesEmpty = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isListParadesEmpty = MutableLiveData<Boolean>().apply {
        value = false
    }
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
            listPosVehiclesAndLine = manager.getPosVehicles(::haveError)
            listPosVehicles.value = listPosVehiclesAndLine?.lines?.flatMap(PosLines::listOfVehicles)
        }
    }

    fun getPosVehiclesByLine(idLine: Int) {

        viewModelScope.launch {
            val resultList = manager.getPosVehiclesByLineUseCase(::haveError, idLine)

            if (resultList.isNotEmpty()) {
                listPosVehicles.value = resultList
            } else {
                isListPosVehiclesEmpty.value = true
            }
        }

    }

    fun getParades(term: String) {
        viewModelScope.launch {
            listParades.value = manager.getParades(::haveError, term)
        }
    }

    fun getParadesByLine(idLine: Int) {
        viewModelScope.launch {
            val resultList = manager.getParadesByLineUseCase(::haveError, idLine)

            if (resultList.isNotEmpty()) {
                listParades.value = resultList
            } else {
                isListParadesEmpty.value = true
            }
        }
    }

    fun getArrivalVehicles(id: Int) {
        viewModelScope.launch {
            listOfArrivalLines.value =
                manager.getPrevArrival(::haveError, id)?.pointOfParade?.lines
        }
    }

    fun getLines(term: String) {
        viewModelScope.launch {
            listLines.value = manager.getLines(::haveError, term)
        }
    }

    fun getSelectedParade(id: String) = listParades.value?.find { it.codeOfParade == id.toInt() }

    private fun haveError(error: String) {
        this.error.value = error
    }
}