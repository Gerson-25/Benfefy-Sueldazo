package com.appbenefy.sueldazo.ui.commerce.model

import com.appbenefy.sueldazo.entity.firebase.Comercio
import com.appbenefy.sueldazo.entity.firebase.Rubro

interface OnCategoryClickListener {
    fun onItemClicked(rubro: Rubro, comercio: Comercio?)
}
