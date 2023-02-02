package com.helvio.rastreioonibus.domain.usecase

import com.helvio.rastreioonibus.data.repository.HttpRepository
import com.helvio.rastreioonibus.domain.model.PrevArrival

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