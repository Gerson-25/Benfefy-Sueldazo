package com.syntepro.sueldazo.entity.firebase

import com.google.firebase.firestore.Exclude
import syntepro.util.picker.ListablePicker

class ComercioPickList: ListablePicker {
    var position: Int = 0
    var comercio: SeguirComercio? = null

    @Exclude
    override fun getCodeValue(): String  {return ""}
    @Exclude
    override fun getTitleValue(): String  {return comercio?.nombre?:""}
    @Exclude
    override fun getDescValue(): String  {return ""}
}