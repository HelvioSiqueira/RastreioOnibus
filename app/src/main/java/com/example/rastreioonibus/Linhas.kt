package com.example.rastreioonibus

import java.io.Serializable

data class Linhas(

    //Código identificador da linha
    val cl: Int = 0,

    // Indica se uma linha opera no modo circular
    val lc: Boolean = false,

    //Informa a primeira parte do letreiro numérico da linha
    val lt: String = "",

    // Informa a segunda parte do letreiro numérico da linha, que indica se a linha opera nos modos:
    //BASE (10), ATENDIMENTO (21, 23, 32, 41)
    val sl: Int = 0,

    // Informa o sentido ao qual a linha atende,
    // onde 1 significa Terminal Principal para Terminal Secundário e 2 para Terminal Secundário para Terminal Principal
    val tl: Int = 0,

    //Informa o letreiro descritivo da linha no sentido Terminal Principal para Terminal Secundário
    val tp: String = "",

    //Informa o letreiro descritivo da linha no sentido Terminal Secundário para Terminal Principal
    val ts: String = ""
): Serializable
