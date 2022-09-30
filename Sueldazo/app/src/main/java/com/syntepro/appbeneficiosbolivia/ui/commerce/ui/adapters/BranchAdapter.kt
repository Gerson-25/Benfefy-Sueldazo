package com.syntepro.appbeneficiosbolivia.ui.commerce.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.BranchResponse
import com.syntepro.appbeneficiosbolivia.ui.commerce.ui.BranchDiff
import com.syntepro.appbeneficiosbolivia.ui.commerce.ui.activities.BranchFilterActivity
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.branch_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class BranchAdapter @Inject constructor() :
        PagedListAdapter<BranchResponse, BranchAdapter.ViewHolder>(
                BranchDiff()
        ) {

    private var activity: BranchFilterActivity? = null
    private var checkedItems: MutableList<CheckBox>? = mutableListOf()
    private var branchesId: MutableList<String>? = mutableListOf()

    fun setActivity(activity: BranchFilterActivity) { this.activity = activity }

    internal var collection: List<BranchResponse> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size
    fun getBranches() = branchesId
    fun getAllCheckBoxes() = checkedItems

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.branch_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it) }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(model: BranchResponse) {
            Constants.branchFilteredList?.let {
                branchesId = it
                if (it.contains(model.idRubro)) view.selectedBranch.isChecked = true
            }
            Functions.showRoundedImage(model.imagen, view.branchImageId)
            view.branchNameId.text = model.nombre
            checkedItems?.add(view.selectedBranch)

            view.selectedBranch.setOnClickListener {
                if (view.selectedBranch.isSelected) branchesId?.remove(model.idRubro)
                else branchesId?.add(model.idRubro)
            }
        }
    }

}