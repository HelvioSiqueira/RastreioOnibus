package com.example.rastreioonibus.domain.usecase

data class RastreioOnibusManagerUseCase(
    val authenticate: AuthenticationUseCase,
    val getPosVehicles: GetPosVehiclesUseCase,
    val getParades: GetParadesUseCase
)