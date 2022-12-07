package com.appbenefy.sueldazo.ui.home.model

import io.realm.internal.Keep
import java.io.Serializable

@Keep
class ArticleResponseDataModel: Serializable {
    var type: Int = 0

    var id: String = ""

    companion object {
        const val DATA = 1
        const val FOOTER = 2
    }
}