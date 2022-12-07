package com.appbenefy.sueldazo.ui.profile.model

import androidx.annotation.Keep

@Keep
class TransactionsByDateDataModel {
    var type: Int = 0

    // Month
    var month: String = ""
    var numberMonth: Int = 0
    var year: Int = 0

    // Transaction
    lateinit var transaction: TransactionResponse

    companion object {
        const val HEADER = 1
        const val DATA = 2
    }
}