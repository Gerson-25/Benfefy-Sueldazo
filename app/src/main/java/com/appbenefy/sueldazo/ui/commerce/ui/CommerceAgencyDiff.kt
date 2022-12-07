package com.appbenefy.sueldazo.ui.commerce.ui

import androidx.recyclerview.widget.DiffUtil
import com.appbenefy.sueldazo.ui.commerce.model.CommerceAgencyResponse

class CommerceAgencyDiff: DiffUtil.ItemCallback<CommerceAgencyResponse>() {
    override fun areItemsTheSame(oldItem: CommerceAgencyResponse, newItem: CommerceAgencyResponse): Boolean {
        return oldItem.idAgency == newItem.idAgency
    }

    override fun areContentsTheSame(oldItem: CommerceAgencyResponse, newItem: CommerceAgencyResponse): Boolean {
        return oldItem == newItem
    }

}