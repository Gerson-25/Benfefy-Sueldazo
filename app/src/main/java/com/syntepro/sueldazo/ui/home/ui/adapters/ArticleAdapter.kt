package com.syntepro.sueldazo.ui.home.ui.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.ui.home.ui.ArticlesDiff
import com.syntepro.sueldazo.ui.home.ui.fragments.HomeFragment
import com.syntepro.sueldazo.ui.shop.model.ArticleResponse
import com.syntepro.sueldazo.ui.shop.ui.activities.ShopActivity
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.discount_item.view.*
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.properties.Delegates

class ArticleAdapter@Inject constructor() :
    PagedListAdapter<ArticleResponse, ArticleAdapter.ViewHolder>(
        ArticlesDiff()
    ) {

    private var fragment: HomeFragment? = null
    private var activity: ShopActivity? = null
    private val format = DecimalFormat("###,##0.0#")

    fun parentFragment(fragment: HomeFragment) { this.fragment = fragment }
    fun parentActivity(activity: ShopActivity) { this.activity = activity }

    internal var collection: List<ArticleResponse> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.discount_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it) }
    }

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(model: ArticleResponse) {
            Functions.showImage(model.imageUrl, itemView.couponImageId)
            Functions.showRoundedImage(model.commerceImageUrl, itemView.couponImageId)
            itemView.titleCouponId.text = model.title

            itemView.itemId.setOnClickListener {
                activity?.openArticleDetail(model.articleId ?: "")
                fragment?.openArticleDetail(model.articleId ?: "")
            }
        }
    }
}
