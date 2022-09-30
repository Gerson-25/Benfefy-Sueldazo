package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.LoyaltyPlanByUser
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.LoyaltyPlanDiff
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.fragments.LoyaltyPlanFragment
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.rv_loyalty_plan_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class LoyaltyPlanAdapter @Inject constructor() :
        PagedListAdapter<LoyaltyPlanByUser, LoyaltyPlanAdapter.ViewHolder>(
                LoyaltyPlanDiff()
        ) {

    private var fragment: LoyaltyPlanFragment? = null

    internal var collection: List<LoyaltyPlanByUser> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    fun parentFragment(fragment: LoyaltyPlanFragment) {
        this.fragment = fragment
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_loyalty_plan_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it) }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(model: LoyaltyPlanByUser) {
            Functions.showRoundedImage(model.commerceImage, view.imageId)
            view.nameId.text = model.commerceName
            view.planNameId.text = model.name
            view.unlinkId.setOnClickListener { fragment?.unlink(model.idLoyaltyPlan) }
            view.conditionsId.setOnClickListener { fragment?.showTerms(model) }
        }


    }

}