package com.appbenefy.sueldazo.ui.commerce.ui

import androidx.recyclerview.widget.DiffUtil
import com.appbenefy.sueldazo.ui.commerce.model.CommerceByBranchDataModel


class CommerceByBranchesDiff : DiffUtil.ItemCallback<CommerceByBranchDataModel>() {
    override fun areItemsTheSame(oldItem: CommerceByBranchDataModel, newItem: CommerceByBranchDataModel): Boolean {
        return oldItem.branchId == newItem.branchId
    }

    override fun areContentsTheSame(oldItem: CommerceByBranchDataModel, newItem: CommerceByBranchDataModel): Boolean {
        return oldItem.branchId == newItem.branchId
                && oldItem.name == newItem.name
    }

}