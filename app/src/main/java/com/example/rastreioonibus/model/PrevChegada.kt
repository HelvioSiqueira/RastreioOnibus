package com.example.rastreioonibus

data class PrevChegada(

    // Horário de referência da geração das informações
    val hr: String = "",

    //Representa um ponto de parada
    val p: PrevParada? = null
)

data class PrevParada(
    //Relação de veículos localizados onde:
    val cp: Int = 0,

    // Nome da parada
    val np: String = "",

    //Informação de latitude da localização do veículo
    val py: Double = 0.0,

    //Informação de longitude da localização do veículo
    val px: Double = 0.0,

    // Relação de linhas localizadas
    val l: List<PrevLinha> = emptyList()
)

data class PrevLinha(

    // Letreiro completo
    val c: String = "",

    //Código identificador da linha
    val cl: Int = 0,

    // Sentido de operação onde 1 significa de
    // Terminal Principal para Terminal Secundário e 2 de Terminal Secundário para Terminal Principal
    val sl: Int = 0,

    //Letreiro de destino da linha
    val lt0: String = "",

    //Letreiro de origem da linha
    val lt1: String = "",

    //Quantidade de veículos localizados
    val qv: Int = 0,

    //Relação de veículos localizados
    val vs: List<PrevVeiculo> = emptyList()
)

data class PrevVeiculo(
    //Prefixo do veículo
    val p: String = "",

    //Horário previsto para chegada do veículo no ponto de parada relacionado
    val t: String = "",

    //Indica se o veículo é (true) ou não (false) acessível para pessoas com deficiência
    val a: Boolean = false,

    //Indica o horário universal (UTC) em que a localização foi capturada.
    // Essa informação está no padrão ISO 8601
    val ta: String = "",

    // Informação de latitude da localização do veículo
    val px: Double = 0.0,

    //Informação de longitude da localização do veículo
    val py: Double = 0.0
)