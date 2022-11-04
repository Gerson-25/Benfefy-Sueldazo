package com.syntepro.sueldazo.ui.shop.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class UserArticleGift(
        val nameSendingUser: String,
        val nameReceivingUser: String,
        val emailReceivingUser: String,
        val dedicatory: String?
): Serializable