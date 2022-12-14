package com.appbenefy.sueldazo.ui.profile.model

import androidx.annotation.Keep
import com.appbenefy.sueldazo.utils.Functions.Companion.ToCurrencyFormat
import java.util.*

@Keep
data class TransactionResponse(
        val idTransaction: String,
        val idTransactionType: Int,
        val idUser: String,
        val idCommerce: String,
        val title: String,
        val description: String,
        val transactionDate: Date,
        val agencyName: String?,
        val cashier: String?,
        val userGivesName: String?,
        val userReceivesName: String?,
        val articleName: String?,
        var visible: Boolean
)

@Keep
data class SavingDetailsResponse(
        var comprasTotales: Double,
        var ahorro: Double?,
        var porcentajeAhorro: Double,
        var detalle: List<SavingDetails>
){
        fun ahorroCurrency(): String{
                return (ahorro ?: 0.00).ToCurrencyFormat()
        }

        fun comprasCurrency(): String{
                return comprasTotales.ToCurrencyFormat()
        }

        fun porcentajeCurrency(): String{
                return porcentajeAhorro.ToCurrencyFormat()
        }
}

@Keep
data class SavingDetails(
        var nombreComercio: String,
        var nombrePlan: String,
        var gasto: Int,
        var beneficio: Int
) {
        fun gastoCurrency(): String{
                return gasto.ToCurrencyFormat()
        }

        fun beneficioCurrency(): String{
                return beneficio.ToCurrencyFormat()
        }
}

@Keep
data class SavingResumeResponse(
        var comprasTotales: Double,
        var ahorro: Double?,
        var porcentajeAhorro: Double,
        var detalle: List<SavingResume>
){
        fun ahorroCurrency(): String{
                return (ahorro ?: 0.00).ToCurrencyFormat()
        }

        fun comprasCurrency(): String{
                return comprasTotales.ToCurrencyFormat()
        }

        fun porcentajeCurrency(): String{
                return porcentajeAhorro.ToCurrencyFormat()
        }
}

@Keep
data class SavingResume(
        var categoria: String,
        var porcentajeAhorro: String,
)