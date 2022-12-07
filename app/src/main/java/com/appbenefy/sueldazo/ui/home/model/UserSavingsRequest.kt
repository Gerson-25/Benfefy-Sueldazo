package com.appbenefy.sueldazo.ui.home.model

import io.realm.internal.Keep

@Keep
data class UserSavingsRequest(
    var country: String,
    var language: Int,
    var idUserFirebase: String
)