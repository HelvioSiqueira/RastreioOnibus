package com.example.rastreioonibus.domain.usecase

import com.example.rastreioonibus.data.repository.HttpRepository
import com.example.rastreioonibus.domain.model.PrevArrival
import com.example.rastreioonibus.domain.model.PrevLine
import com.example.rastreioonibus.domain.model.PrevVehicle

class GetPrevArrivalUseCase(
    private val repo: HttpRepository
) {

    suspend operator fun invoke(
        hasError: (String) -> Unit,
        id: Int
    ): PrevArrival? {

        val response = repo.getPrevArrival(id)

        return if (response.isSuccessful) {
            response.body()
        } else {
            hasError(response.message())
            null
        }
    }
}