package com.syntepro.sueldazo.ui.home.model

import com.syntepro.sueldazo.ui.shop.model.ArticleResponse
import io.realm.internal.Keep
import java.io.Serializable

@Keep
class ArticleResponseDataModel: Serializable {
    var type: Int = 0

    var id: String = ""

    lateinit var article: ArticleResponse

    companion object {
        const val DATA = 1
        const val FOOTER = 2
    }
}