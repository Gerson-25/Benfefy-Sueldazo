package com.syntepro.sueldazo.ui.commerce.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.sueldazo.ui.commerce.model.CommerceByBranchDataModel


class CommerceByBranchesDiff : DiffUtil.ItemCallback<CommerceByBranchDataModel>() {
    override fun areItemsTheSame(oldItem: CommerceByBranchDataModel, newItem: CommerceByBranchDataModel): Boolean {
        return oldItem.branchId == newItem.branchId
    }

    override fun areContentsTheSame(oldItem: CommerceByBranchDataModel, newItem: CommerceByBranchDataModel): Boolean {
        return oldItem.branchId == newItem.branchId
                && oldItem.name == newItem.name
    }

}