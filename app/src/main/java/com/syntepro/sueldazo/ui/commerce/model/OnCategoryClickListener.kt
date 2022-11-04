package com.syntepro.sueldazo.ui.commerce.model

import com.syntepro.sueldazo.entity.firebase.Comercio
import com.syntepro.sueldazo.entity.firebase.Rubro

interface OnCategoryClickListener {
    fun onItemClicked(rubro: Rubro, comercio: Comercio?)
}
