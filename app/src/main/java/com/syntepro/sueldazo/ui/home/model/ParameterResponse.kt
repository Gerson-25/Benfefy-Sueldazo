package com.syntepro.sueldazo.ui.home.model

import androidx.annotation.Keep

@Keep
data class ParameterResponse(
        val SUDAMERICANA_PARAMETROS: List<SudamericanaData>?,
        val transactionsType: List<TransactionData>?
)