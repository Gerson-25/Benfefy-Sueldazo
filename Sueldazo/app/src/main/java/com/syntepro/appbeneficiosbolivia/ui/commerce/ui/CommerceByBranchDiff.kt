package com.syntepro.appbeneficiosbolivia.ui.commerce.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceByBranchResponse

class CommerceByBranchDiff : DiffUtil.ItemCallback<CommerceByBranchResponse>() {
    override fun areItemsTheSame(oldItem: CommerceByBranchResponse, newItem: CommerceByBranchResponse): Boolean {
        return oldItem.branchId == newItem.branchId
    }

    override fun areContentsTheSame(oldItem: CommerceByBranchResponse, newItem: CommerceByBranchResponse): Boolean {
        return oldItem.branchId == newItem.branchId
                && oldItem.name == newItem.name
    }

}