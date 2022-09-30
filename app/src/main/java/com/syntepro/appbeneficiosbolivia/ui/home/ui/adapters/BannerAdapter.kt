package com.syntepro.appbeneficiosbolivia.ui.home.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.home.ui.fragments.BenefyFragment
import com.syntepro.appbeneficiosbolivia.ui.home.model.BannerResponse
import com.syntepro.appbeneficiosbolivia.ui.home.ui.BannerDiff
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.banner_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class BannerAdapter @Inject constructor() :
        PagedListAdapter<BannerResponse, BannerAdapter.ViewHolder>(
                BannerDiff()
        ) {

    private var fragment: BenefyFragment? = null

    fun parentFragment(fragment: BenefyFragment) { this.fragment = fragment }

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
            Functions.showImage(model.urlImage, itemView.imageId)

            itemView.itemId.setOnClickListener { fragment?.openBanner(model) }
        }
    }

}