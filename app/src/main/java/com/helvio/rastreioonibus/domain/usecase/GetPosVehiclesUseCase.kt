package com.helvio.rastreioonibus.domain.usecase

import com.helvio.rastreioonibus.data.repository.HttpRepository
import com.helvio.rastreioonibus.domain.model.PosVehicles

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