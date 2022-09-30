package com.syntepro.appbeneficiosbolivia.ui.home.model

import io.realm.internal.Keep

@Keep
data class UserSavingsRequest(
    var country: String,
    var language: Int,
    var idUserFirebase: String
)