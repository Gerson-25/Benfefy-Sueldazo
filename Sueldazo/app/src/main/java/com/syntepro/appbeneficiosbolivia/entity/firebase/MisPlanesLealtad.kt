package com.syntepro.appbeneficiosbolivia.entity.firebase

import com.google.firebase.firestore.DocumentId
import java.util.*

class MisPlanesLealtad {
    @DocumentId
    var documentId: String = ""
    var comercio: String = ""
    var plan: String = ""
    var nombrePlan: String = ""
    var categoria: Int = 0
    var activo: Boolean = true
    var fechaAfiliacion = Date()
    var fechaFinAfiliacion: Date? = null
    var fechaFinPrograma: Date? = null  // Fecha de finalizacion del programa
    var usuarioActivo: Boolean = false // Indica si el usuario puede canjear el cupon o no (por impago por ejemplo)
}