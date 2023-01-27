package com.example.rastreioonibus.domain.usecase

import com.example.rastreioonibus.data.repository.HttpRepository
import com.example.rastreioonibus.domain.model.PosLines
import com.example.rastreioonibus.domain.model.PosVehicles
import com.example.rastreioonibus.domain.model.Vehicles

class GetPosVehiclesUseCase(
    private val repo: HttpRepository
) {

    suspend operator fun invoke(
        haveError: (String) -> Unit
    ): PosVehicles? {

        var listPosVehicles: PosVehicles? = null
        val response = repo.getPosVehicles()

        if (response.isSuccessful) {
            listPosVehicles = response.body()

        } else {
            haveError(response.message())
        }

        return listPosVehicles
    }
}