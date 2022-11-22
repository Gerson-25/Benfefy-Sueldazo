package com.syntepro.sueldazo.ui.home.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.ui.home.model.SavingCategory
import com.syntepro.sueldazo.ui.home.ui.fragments.HomeFragment
import com.syntepro.sueldazo.ui.shop.model.GiftCard
import com.syntepro.sueldazo.ui.shop.ui.GiftCardDiff
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.gift_cards_item.view.*
import kotlinx.android.synthetic.main.gift_cards_item.view.itemId
import kotlinx.android.synthetic.main.saving_category_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class SavingsCategoryAdapter@Inject constructor() :
        PagedListAdapter<GiftCard, SavingsCategoryAdapter.ViewHolder>(
                GiftCardDiff()
        ) {

    private var fragment: HomeFragment? = null

    fun parentFragment(fragment: HomeFragment) { this.fragment = fragment }

    internal var collection: List<SavingCategory> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.saving_category_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it) }
    }

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(model: SavingCategory) {
            model.color.let {
                if (it.isNotEmpty() && it.startsWith("#"))
                    view.icon.borderColor = Color.parseColor(it)
            }
            view.nameId.text = model.categoryName

        }
    }

}