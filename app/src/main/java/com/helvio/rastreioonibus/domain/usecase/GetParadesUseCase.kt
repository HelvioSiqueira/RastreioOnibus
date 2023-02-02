package com.helvio.rastreioonibus.domain.usecase

import com.helvio.rastreioonibus.data.repository.HttpRepository
import com.helvio.rastreioonibus.domain.model.Parades

class GetParadesUseCase(
    private val repo: HttpRepository
) {

    suspend operator fun invoke(
        haveError: (String) -> Unit,
        term: String
    ): List<Parades> {

        var listParades = listOf<Parades>()
        val response = repo.getParades(term)

        if (response.isSuccessful) {
            listParades =
                response.body() ?: emptyList()

        } else {
            haveError(response.message())
        }

        return listParades
    }
}