package com.example.rastreioonibus.model

data class PosVehicles(
    // Horário de referência da geração das informações
    val hr: String = "",

    // Relação de linhas localizadas
    val l: List<PosLines> = emptyList()
)

data class PosLines(
    //Letreiro completo
    val c: String = "",

    //Código identificador da linha
    val cl: Int = 0,

    //Sentido de operação onde
    // 1 significa de Terminal Principal para Terminal Secundário e 2 de Terminal Secundário para Terminal Principal
    val sl: Int = 0,

    // Letreiro de destino da linha
    val lt0: String = "",

    //Letreiro de origem da linha
    val lt1: String = "",

    //Quantidade de veículos localizados
    val qv: Int = 0,

    // Relação de veículos localizados
    val vs: List<Vehicles> = emptyList()
)

data class Vehicles(
    // Prefixo do veículo
    val p: String = "",

    //Indica se o veículo é (true) ou não (false) acessível para pessoas com deficiência
    val a: Boolean = false,

    // Indica o horário universal (UTC) em que a localização foi capturada. Essa informação está no padrão ISO 8601
    val ta: String = "",

    //Informação de latitude da localização do veículo
    val py: Double = 0.0,

    //Informação de longitude da localização do veículo
    val px: Double = 0.0
)
