package com.appbenefy.sueldazo.ui.agency.ui

import androidx.recyclerview.widget.DiffUtil
import com.appbenefy.sueldazo.ui.agency.model.AgencyResponse

class AgencyDiff : DiffUtil.ItemCallback<AgencyResponse>() {
    override fun areItemsTheSame(oldItem: AgencyResponse, newItem: AgencyResponse): Boolean {
        return oldItem.idAgency == newItem.idAgency
    }

    override fun areContentsTheSame(oldItem: AgencyResponse, newItem: AgencyResponse): Boolean {
        return oldItem == newItem
    }

}