package com.syntepro.appbeneficiosbolivia.ui.commerce.model

import com.syntepro.appbeneficiosbolivia.entity.firebase.Comercio
import com.syntepro.appbeneficiosbolivia.entity.firebase.Rubro

interface OnCategoryClickListener {
    fun onItemClicked(rubro: Rubro, comercio: Comercio?)
}
