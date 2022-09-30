package com.syntepro.appbeneficiosbolivia.ui.home.model

import com.syntepro.appbeneficiosbolivia.ui.shop.model.ArticleResponse
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