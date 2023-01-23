package com.example.rastreioonibus.domain.usecase

import com.example.rastreioonibus.data.repository.HttpRepository
import com.example.rastreioonibus.domain.model.PrevLine
import com.example.rastreioonibus.domain.model.PrevVehicle

class GetPrevArrival(
    private val repo: HttpRepository
) {

    suspend operator fun invoke(
        id: Int,
        hasError: (String) -> Unit
    ): List<PrevVehicle>? {

        val response = repo.getPrevArrival(id)

        var listOfVehicles: List<PrevVehicle>? = listOf()

        if (response.isSuccessful) {
            listOfVehicles =
                response.body()?.pointOfParade?.lines?.flatMap(PrevLine::vehicles)
        } else {
            hasError(response.message())
        }

        return listOfVehicles
    }
}