package com.syntepro.appbeneficiosbolivia.ui.profile.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.appbeneficiosbolivia.ui.profile.model.TransactionsByDateDataModel

class TransactionDiff: DiffUtil.ItemCallback<TransactionsByDateDataModel>() {
    override fun areItemsTheSame(oldItem: TransactionsByDateDataModel, newItem: TransactionsByDateDataModel): Boolean {
        return oldItem.transaction.idTransaction == newItem.transaction.idTransaction
    }

    override fun areContentsTheSame(oldItem: TransactionsByDateDataModel, newItem: TransactionsByDateDataModel): Boolean {
        return oldItem == newItem
    }
}