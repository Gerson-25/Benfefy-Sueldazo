package com.appbenefy.sueldazo.ui.profile.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.ui.coupon.model.FeaturedCouponResponse
import com.appbenefy.sueldazo.ui.coupon.ui.FeaturedCouponDiff
import com.appbenefy.sueldazo.ui.profile.model.SavingDetails
import com.appbenefy.sueldazo.ui.profile.model.TransactionsByDateDataModel
import com.appbenefy.sueldazo.ui.profile.ui.TransactionDiff
import com.appbenefy.sueldazo.ui.profile.ui.activities.TransactionsActivity
import com.appbenefy.sueldazo.utils.Helpers
import kotlinx.android.synthetic.main.data_transaction_section_item.view.*
import kotlinx.android.synthetic.main.header_transaction_section_item.view.*
import java.text.DateFormat
import javax.inject.Inject
import kotlin.properties.Delegates

class TransactionsAdapter @Inject constructor() :
        PagedListAdapter<FeaturedCouponResponse, TransactionsAdapter.BaseViewHolder<*>>(
            FeaturedCouponDiff()
        ) {

    private var activity: TransactionsActivity? = null

    fun setActivity(activity: TransactionsActivity) { this.activity = activity }

    internal var collection: List<SavingDetails> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when(viewType) {
            TransactionsByDateDataModel.HEADER -> {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.header_transaction_section_item, parent, false)
                HeaderViewHolder(view)
            }
            TransactionsByDateDataModel.DATA -> {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.data_transaction_section_item, parent, false)
                DataViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        collection[position].let {
            when (holder) {
                is HeaderViewHolder -> holder.bind(it, position)
                is DataViewHolder -> holder.bind(it, position)
                else -> throw IllegalArgumentException()
            }
        }
    }

//    override fun getItemViewType(position: Int): Int {
//        return collection[position].type
//    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T, position: Int)
    }

    inner class HeaderViewHolder(val view: View): BaseViewHolder<SavingDetails>(view) {
        override fun bind(item: SavingDetails, position: Int) {
//            view.monthNameId.text = item.month
//
//            view.setOnClickListener { activity?.collapseMonth(item.numberMonth, item.year) }
        }
    }

    inner class DataViewHolder(val view: View): BaseViewHolder<SavingDetails>(view) {
        override fun bind(item: SavingDetails, position: Int) {
//            if (item.transaction.visible) {
//                view.containerCard.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
//                view.titleId.text = item.transaction.title
//                view.dateId.text = "${view.context.getString(R.string.transaction_date)} ${Helpers.dateToStr(item.transaction.transactionDate, DateFormat.LONG)}"
//            } else
//                view.containerCard.layoutParams = RelativeLayout.LayoutParams(0, 0)
//
//            view.setOnClickListener { activity?.openDetail(item.transaction.idTransaction) }
        }
    }

}