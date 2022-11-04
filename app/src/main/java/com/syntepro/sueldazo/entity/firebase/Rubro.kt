package com.syntepro.sueldazo.entity.firebase

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import syntepro.util.picker.ListablePicker

class Rubro: ListablePicker {
//    var Id: String = ""
    @DocumentId
    var documentID: String? = null
    var nombre: String? = null
    var imagen: String? = null
    var icono: String? = null
    var totalComercios: List<TotalComercio>? = null
    var paises: List<String>? = null

    constructor() {}
    constructor(documentID: String?, nombre: String?, imagen: String?) {
        this.documentID = documentID
        this.nombre = nombre
        this.imagen = imagen

    }

    @Exclude
    override fun getCodeValue(): String  {return ""}
    @Exclude
    override fun getTitleValue(): String  {return nombre!!}
    @Exclude
    override fun getDescValue(): String  {return ""}

}