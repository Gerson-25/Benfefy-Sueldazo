package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.lealtad.AddLoyaltyPlanDialog
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.PlanByCommerceResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.PlanByCommerceDiff
import kotlinx.android.synthetic.main.rv_plan_by_commerce_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class PlanByCommerceAdapter @Inject constructor() :
        PagedListAdapter<PlanByCommerceResponse, PlanByCommerceAdapter.ViewHolder>(
                PlanByCommerceDiff()
        ) {

    private var activity: AddLoyaltyPlanDialog? = null
    private var lastPlanId: PlanByCommerceResponse? = null
    private var _planCode: String? = null

    fun setActivity(activity: AddLoyaltyPlanDialog) {this.activity = activity}

    internal var collection: List<PlanByCommerceResponse> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size
    fun getLastPlan() = lastPlanId
    fun getPlanCode() = _planCode

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_plan_by_commerce_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it) }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(model: PlanByCommerceResponse) {
            //            view.codeParentId.visibility = if(model.codeRequired) View.VISIBLE else View.GONE
            view.nameId.text = model.name
            view.descripcionId.text = model.description
            view.setOnClickListener {
                lastPlanId = model
                if (model.codeRequired) activity?.showCode()
                else activity?.showTerms()
//                if (model.codeRequired && view.codeParentId.editText?.text.isNullOrEmpty()) {
//                    view.codeParentId.requestFocus()
//                    view.codeParentId.error = view.context.getString(R.string.required_label)
//                    return@setOnClickListener
//                }
//                lastPlanId = model
//                _planCode = view.codeParentId.editText?.text.toString()
//                activity?.showTerms()
            }
        }
    }

}