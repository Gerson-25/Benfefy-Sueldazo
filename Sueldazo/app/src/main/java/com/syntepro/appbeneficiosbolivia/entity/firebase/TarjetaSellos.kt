package com.syntepro.appbeneficiosbolivia.entity.firebase

import com.google.firebase.firestore.DocumentId
import java.util.*

class TarjetaSellos {
    @DocumentId
    var documentId: String = ""
    var inicio: Date = Date()
    var fin: Date? = null
    var sellosRequeridos: Int = 0   // Cantidad de sellos de la tarjeta
    var sellosObtenidos: Int = 0    // Cantidad de sellos obtenidos
    var estatus: Int = STATUS_INPROGRESS
    var fechaCompletada: Date? = null
    var fechaCanje: Date? = null
    var fechaUltimoSello: Date? = null

    companion object {
        const val STATUS_INPROGRESS = 1  // Cuando esta creada y puede tener cero o total sellos - 1
        const val STATUS_COMPLETED = 2  // Cuando se ha completado pero aun no se canjea
        const val STATUS_REDEEMED = 3  // Cuando ya se ha canjeado
        const val STATUS_EXPIRED = 4  // Cuando la fecha maxima de la promocion ha sucedido antes del canje o completarla
    }

}