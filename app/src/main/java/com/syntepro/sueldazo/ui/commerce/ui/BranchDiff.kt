package com.syntepro.sueldazo.ui.commerce.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.sueldazo.ui.commerce.model.BranchResponse

class BranchDiff: DiffUtil.ItemCallback<BranchResponse>() {
    override fun areItemsTheSame(oldItem: BranchResponse, newItem: BranchResponse): Boolean {
        return oldItem.idRubro == newItem.idRubro
    }

    override fun areContentsTheSame(oldItem: BranchResponse, newItem: BranchResponse): Boolean {
        return oldItem == newItem
    }
}