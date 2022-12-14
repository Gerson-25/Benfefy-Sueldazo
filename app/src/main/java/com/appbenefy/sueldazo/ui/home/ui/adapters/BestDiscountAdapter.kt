package com.appbenefy.sueldazo.ui.home.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.core.AndroidApplication
import com.appbenefy.sueldazo.core.preference.AppPreference
import com.appbenefy.sueldazo.ui.coupon.model.BestDiscountResponse
import com.appbenefy.sueldazo.ui.coupon.ui.BestDiscountDiff
import com.appbenefy.sueldazo.ui.home.ui.fragments.FavoriteFragment
import com.appbenefy.sueldazo.utils.Constants
import com.appbenefy.sueldazo.utils.Functions
import com.appbenefy.sueldazo.utils.UserType
import kotlinx.android.synthetic.main.best_discounts_item.view.*
import kotlinx.android.synthetic.main.best_discounts_item.view.blockedCoupon
import kotlinx.android.synthetic.main.best_discounts_item.view.commerceImageId
import kotlinx.android.synthetic.main.best_discounts_item.view.itemId
import kotlinx.android.synthetic.main.featured_item.view.*
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.properties.Delegates

class BestDiscountAdapter@Inject constructor(
) :
        PagedListAdapter<BestDiscountResponse, BestDiscountAdapter.ViewHolder>(
                BestDiscountDiff()
        ) {

    private var fragment: FavoriteFragment? = null
    private val format = DecimalFormat("###,##0.0#")

    fun parentFragment(fragment: FavoriteFragment) { this.fragment = fragment }

    internal var collection: List<BestDiscountResponse> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.best_discounts_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it) }
    }

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(model: BestDiscountResponse) {
//            Functions.showImage(model.banner, view.bannerImageId)
            Functions.showImage(model.imagenCampana, view.commerceImageId)

            when(Constants.TYPE_OF_USER){
                UserType.VERIFIED_USER -> view.blockedCoupon.visibility = View.GONE
                else -> view.blockedCoupon.visibility = View.VISIBLE
            }
//            when (model.couponType) {
//                1 -> {
//                    val discount = (model.discountPrice / model.realPrice) * 100
//                    val percent = 100 - discount
//                    view.discountPercentageId.text = "${percent.toInt()}%"
//                }
//                2 -> view.discountPercentageId.text = "${model.discountPrice.toInt()}%"
//                3 -> view.discountPercentageId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.discountPrice.toInt())}"
//                else -> view.discountPercentageId.text = "0%"
//            }

            view.titleCoupon.text = model.titulo
            view.discountPercentageId.text = "Bs${model.precioDescuento.toInt()}"

            view.itemId.setOnClickListener {
                if (fragment?.showBlocked() == true){
                    fragment?.notRegisterUser()
                } else {
                    fragment?.openBestDiscountDetail(model.idCampana)
                }
            }
        }
    }

}