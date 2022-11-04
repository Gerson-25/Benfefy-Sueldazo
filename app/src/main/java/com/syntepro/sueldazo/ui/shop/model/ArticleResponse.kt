package com.syntepro.sueldazo.ui.shop.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.syntepro.sueldazo.entity.service.Agency
import java.io.Serializable
import java.util.*

@Keep
class ArticleResponse: Serializable {
    @SerializedName("idArticulo")
    var articleId: String? = null
    @SerializedName("idComercio")
    var commerceId: String? = null
    @SerializedName("idCategoriaCampana")
    var couponCategoryId: String? = null
    @SerializedName("porcentaje")
    var percentage: Double = 0.0
    @SerializedName("titulo")
    var title: String? = null
    @SerializedName("subtitulo")
    var subtitle: String? = null
    @SerializedName("precioRegular")
    var regularPrice: Double = 0.0
    @SerializedName("vigenciaInicio")
    var effectiveStart: Date = Date()
    @SerializedName("vigenciaFin")
    var effectiveEnd: Date = Date()
    @SerializedName("terminosCondiciones")
    var conditions: String? = null
    @SerializedName("urlImagen")
    var imageUrl: String? = null
    @SerializedName("urlComercio")
    var commerceImageUrl: String? = null
    @SerializedName("comercioNombre")
    var commerceName: String? = null
    var idProductType: Int = 0
    var description: String? = null
    var allowsDelivery: Boolean = false
    var maxDistanceDelivery: Double = 0.0
    var deliveryType: Int = 0
    var dispatchPoints: List<DispatchPoints>? = null
    var deliveryPrices: List<DeliveryPrices>? = null
    @SerializedName("sucursales")
    var agencies: List<Agency>? = null
    @SerializedName("favorito")
    var favorite: Boolean = false

    companion object {
        const val PERSONAL_ACCIDENT_INSURANCE = 5
        const val PET_INSURANCE = 6
        const val COVID_INSURANCE = 11
    }
}
