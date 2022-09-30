package com.syntepro.appbeneficiosbolivia.ui.home.ui.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.home.ui.ArticlesDiff
import com.syntepro.appbeneficiosbolivia.ui.home.ui.fragments.HomeFragment
import com.syntepro.appbeneficiosbolivia.ui.shop.model.ArticleResponse
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities.ShopActivity
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
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
            Functions.showRoundedImage(model.commerceImageUrl, itemView.commerceImageId)
            itemView.titleCouponId.text = model.title
            itemView.subtitleCouponId.text = model.subtitle

            if (model.percentage != 0.0) {
                itemView.couponOriginalPriceId.text = Html.fromHtml("${Constants.userProfile?.currency ?: "Bs"} <del>${format.format(model.regularPrice)}</del>")
                val discount = (model.regularPrice) * (model.percentage / 100.0f)
                val newPrice = model.regularPrice - discount
                itemView.couponPriceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(newPrice)}"
            } else {
                itemView.couponOriginalPriceId.visibility = View.GONE
                itemView.couponPriceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.regularPrice)}"
            }

            if (model.favorite) {
                val animScale = AnimationUtils.loadAnimation(itemView.context, R.anim.scale_fav)
                itemView.favorite.startAnimation(animScale)
                itemView.favorite.setBackgroundResource(R.drawable.ic_unmark_fav)
            } else
                itemView.favorite.setBackgroundResource(R.drawable.ic_mark_fav)

            itemView.favorite.setOnClickListener {
                activity?.favorite(model, position)
                fragment?.favorite(model, position)
                model.favorite = !model.favorite
            }

            itemView.itemId.setOnClickListener {
                activity?.openArticleDetail(model.articleId ?: "")
                fragment?.openArticleDetail(model.articleId ?: "")
            }
        }
    }
}
