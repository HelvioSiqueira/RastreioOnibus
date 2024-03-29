package com.helvio.rastreioonibus.domain.usecase

data class RastreioOnibusManagerUseCase(
    val authenticate: AuthenticationUseCase,
    val getPosVehicles: GetPosVehiclesUseCase,
    val getPosVehiclesByLineUseCase: GetPosVehiclesByLineUseCase,
    val getParades: GetParadesUseCase,
    val getParadesByLineUseCase: GetParadesByLineUseCase,
    val getPrevArrival: GetPrevArrivalUseCase,
    val getLines: GetLinesUseCase
)