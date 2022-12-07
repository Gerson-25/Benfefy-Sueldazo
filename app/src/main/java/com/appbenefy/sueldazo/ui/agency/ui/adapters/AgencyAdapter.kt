package com.appbenefy.sueldazo.ui.agency.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.ui.agency.model.AgencyResponse
import com.appbenefy.sueldazo.ui.agency.ui.AgencyDiff
import com.appbenefy.sueldazo.ui.coupon.AgencyActivity
import kotlinx.android.synthetic.main.agency_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class AgencyAdapter
@Inject constructor() :
        PagedListAdapter<AgencyResponse, AgencyAdapter.ViewHolder>(
                AgencyDiff()
        ) {

    private var activity: AgencyActivity? = null

    fun setActivity(activity: AgencyActivity) { this.activity = activity }

    internal var collection: List<AgencyResponse> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.agency_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it) }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(model: AgencyResponse) {
            view.agencyNameId.text = if (model.agencyName.isNullOrEmpty()) model.sucursalName
            else model.agencyName
            view.agencyAddressId.text = model.address

            view.setOnClickListener { activity?.openAgency(model) }
        }
    }

}
