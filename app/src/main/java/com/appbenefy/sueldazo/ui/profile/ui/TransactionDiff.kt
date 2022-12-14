package com.appbenefy.sueldazo.ui.profile.ui

import androidx.recyclerview.widget.DiffUtil
import com.appbenefy.sueldazo.ui.profile.model.TransactionsByDateDataModel

class TransactionDiff: DiffUtil.ItemCallback<TransactionsByDateDataModel>() {
    override fun areItemsTheSame(oldItem: TransactionsByDateDataModel, newItem: TransactionsByDateDataModel): Boolean {
        return oldItem.transaction == newItem.transaction
    }

    override fun areContentsTheSame(oldItem: TransactionsByDateDataModel, newItem: TransactionsByDateDataModel): Boolean {
        return oldItem == newItem
    }
}