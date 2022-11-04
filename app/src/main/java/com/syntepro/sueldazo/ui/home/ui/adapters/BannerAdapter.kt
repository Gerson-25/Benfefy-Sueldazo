package com.syntepro.sueldazo.ui.home.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.ui.home.ui.fragments.BenefyFragment
import com.syntepro.sueldazo.ui.home.model.BannerResponse
import com.syntepro.sueldazo.ui.home.ui.BannerDiff
import com.syntepro.sueldazo.ui.home.ui.fragments.HomeFragment
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.banner_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class BannerAdapter @Inject constructor() :
        PagedListAdapter<BannerResponse, BannerAdapter.ViewHolder>(
                BannerDiff()
        ) {

    private var fragment: HomeFragment? = null

    fun parentFragment(fragment: HomeFragment) { this.fragment = fragment }

    internal var collection: List<BannerResponse> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.banner_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it) }
    }

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(model: BannerResponse) {
            Functions.showImage(model.urlImage, itemView.couponImageId)
            itemView.titleCouponId.text = model.campaingName
            itemView.itemId.setOnClickListener { fragment?.openBanner(model) }
        }
    }

}