package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.PlanByCommerceResponse

class PlanByCommerceDiff: DiffUtil.ItemCallback<PlanByCommerceResponse>() {
    override fun areItemsTheSame(oldItem: PlanByCommerceResponse, newItem: PlanByCommerceResponse): Boolean {
        return oldItem.idLoyaltyPlan == newItem.idLoyaltyPlan
    }

    override fun areContentsTheSame(oldItem: PlanByCommerceResponse, newItem: PlanByCommerceResponse): Boolean {
        return oldItem == newItem
    }

}