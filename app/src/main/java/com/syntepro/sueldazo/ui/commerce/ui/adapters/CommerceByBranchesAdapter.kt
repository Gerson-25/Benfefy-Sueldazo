package com.syntepro.sueldazo.ui.commerce.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.ui.commerce.model.CommerceByBranchDataModel
import com.syntepro.sueldazo.ui.commerce.ui.CommerceByBranchesDiff
import com.syntepro.sueldazo.ui.commerce.ui.activities.Commerce2Activity
import kotlinx.android.synthetic.main.header_commerce_section_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class CommerceByBranchesAdapter @Inject constructor() :
        PagedListAdapter<CommerceByBranchDataModel, CommerceByBranchesAdapter.BaseViewHolder<*>>(
                CommerceByBranchesDiff()
        ) {

    private var activity: Commerce2Activity? = null

    fun setActivity(activity: Commerce2Activity) {this.activity = activity}

    internal var collection: List<CommerceByBranchDataModel> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            CommerceByBranchDataModel.HEADER -> {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.header_commerce_section_item, parent, false)
                HeaderViewHolder(view)
            }
            CommerceByBranchDataModel.DATA -> {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.data_commerce_section_item, parent, false)
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

    override fun getItemViewType(position: Int): Int {
        return collection[position].type
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T, position: Int)
    }

    inner class HeaderViewHolder(val view: View) : BaseViewHolder<CommerceByBranchDataModel>(view) {
        override fun bind(item: CommerceByBranchDataModel, position: Int) {
            if (item.commerceCount > 3) {
                view.allCommerce.visibility = View.VISIBLE
                view.allCommerce.isEnabled = true
            } else {
                view.allCommerce.visibility = View.INVISIBLE
                view.allCommerce.isEnabled = false
            }

            view.branchNameId.text = item.name
            view.totalCommerceCountId.text = "(${item.commerceCount})"
            view.allCommerce.setOnClickListener { activity?.showAll(item) }
        }
    }

    inner class DataViewHolder(val view: View) : BaseViewHolder<CommerceByBranchDataModel>(view) {
        override fun bind(item: CommerceByBranchDataModel, position: Int) {

            view.setOnClickListener { activity?.openCommerce(item) }
        }
    }

}