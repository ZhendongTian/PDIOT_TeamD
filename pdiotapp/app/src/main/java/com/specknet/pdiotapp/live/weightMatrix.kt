package com.specknet.pdiotapp.live


data class weightMatrix(
    val matrixList: String
) {
    constructor(columns: List<String>) : this(
        matrixList = columns[0]
    )
}
