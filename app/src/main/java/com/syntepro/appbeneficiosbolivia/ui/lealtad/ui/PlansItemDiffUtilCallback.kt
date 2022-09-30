package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.LoyaltyPlanByUser

class PlansItemDiffUtilCallback : DiffUtil.ItemCallback<LoyaltyPlanByUser>() {
    override fun areItemsTheSame(oldItem: LoyaltyPlanByUser, newItem: LoyaltyPlanByUser): Boolean {
        return oldItem.idLoyaltyPlan == newItem.idLoyaltyPlan
    }

    override fun areContentsTheSame(oldItem: LoyaltyPlanByUser, newItem: LoyaltyPlanByUser): Boolean {
        return oldItem.idLoyaltyPlan == newItem.idLoyaltyPlan
                && oldItem.name == newItem.name
    }

}