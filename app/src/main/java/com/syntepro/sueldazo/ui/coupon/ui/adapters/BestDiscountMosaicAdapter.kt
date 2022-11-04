package com.syntepro.sueldazo.ui.coupon.ui.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.ui.coupon.model.BestDiscountResponse
import com.syntepro.sueldazo.ui.coupon.ui.BestDiscountDiff
import com.syntepro.sueldazo.ui.coupon.ui.activities.BestDiscountListActivity
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.coupon_mosaic_item.view.*
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.properties.Delegates

class BestDiscountMosaicAdapter @Inject constructor() :
        PagedListAdapter<BestDiscountResponse, BestDiscountMosaicAdapter.ViewHolder>(
                BestDiscountDiff()
        )
{

    private var activity: BestDiscountListActivity? = null
    private val format = DecimalFormat("###,##0.0#")

    internal var collection: List<BestDiscountResponse> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size
    fun setActivity(activity: BestDiscountListActivity) { this.activity = activity }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.coupon_mosaic_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it, position) }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(model: BestDiscountResponse, position: Int) {

        }
    }

}