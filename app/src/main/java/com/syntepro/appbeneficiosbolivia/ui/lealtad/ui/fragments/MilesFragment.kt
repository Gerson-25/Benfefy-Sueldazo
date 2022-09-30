package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.core.app.ErrorMessage
import com.syntepro.appbeneficiosbolivia.core.base.BaseFragment
import com.syntepro.appbeneficiosbolivia.core.base.viewModel
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.activities.CouponDetail2Activity
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.PlanMilesDetailResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.PlanDetailObject
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.adapters.MilesAdapter
import com.syntepro.appbeneficiosbolivia.ui.lealtad.viewmodel.LoyaltyPlansDetailViewModel
import com.syntepro.appbeneficiosbolivia.ui.wallet.MilesGoalsInfoDialog
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Helpers
import kotlinx.android.synthetic.main.fragment_corporate.listId
import kotlinx.android.synthetic.main.fragment_miles.*
import kotlinx.android.synthetic.main.miles_empty_layout.*
import java.text.DateFormat
import javax.inject.Inject

class MilesFragment : BaseFragment() {
    @Inject
    lateinit var  milesAdapter: MilesAdapter
    private lateinit var planDetailViewModel: LoyaltyPlansDetailViewModel
    var idPlan: String? = ""
    var idGoal: String? = null

    override fun layoutId() = R.layout.fragment_miles

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        planDetailViewModel = viewModel(viewModelFactory) {
            observe(miles, :: detailResponse)
            failure(failure, ::handleFailure)
        }

        milesAdapter.parentFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        if (arguments?.getString("planId").isNullOrEmpty()) {
            milesHeaderId.visibility = View.GONE
            emptyId.visibility = View.VISIBLE
            listId.visibility = View.INVISIBLE
        } else if (!Constants.idPushPlan.isNullOrEmpty()) {
            PlanDetailObject.getPlanDetail(Constants.idPushPlan!!, planDetailViewModel::milesPlanDetail)
            Constants.idPushPlan = ""
            Constants.idPlanType = 0
        } else
            PlanDetailObject.getPlanDetail(arguments?.getString("planId")!!, planDetailViewModel::milesPlanDetail)

        milesHeaderId.setOnClickListener {
            idPlan?.let {
                val intent = Intent(requireContext(), MilesGoalsInfoDialog::class.java)
                intent.putExtra("goalId", idGoal)
                intent.putExtra("planId", idPlan)
                intent.putExtra("userPlanId", arguments?.getString("planId"))
                startActivityForResult(intent, 825)
            }
        }

        explorePlans.setOnClickListener { openLoyaltyPlans() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 825 && resultCode == Activity.RESULT_OK) {
            data?.let {
                PlanDetailObject.getPlanDetail(it.getStringExtra("userPlanId") ?: "", planDetailViewModel::milesPlanDetail)
            }
        }
    }

    private fun initList() {
        listId.setHasFixedSize(true)
//        listId.layoutManager = GridLayoutManager(requireContext(), 2)
        listId.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        listId.itemAnimator = DefaultItemAnimator()
        listId.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
        listId.adapter = milesAdapter
    }

    private fun detailResponse(detail: BaseResponse<PlanMilesDetailResponse>?) {
        detail?.let {
            if (it.isSuccess) {
                Constants.isSelectedPlan = detail.data?.idPlan
                idPlan = detail.data?.idPlan
                idGoal = detail.data?.idGoal
                milesAdapter.collection = detail.data?.couponList.orEmpty()
                milesGoalId.text = "${detail.data?.milesGoal}"
                actualMilesId.text = "${detail.data?.miles}"
                advancedProgressBar.progress = detail.data?.goalAdvance ?: 0
                detail.data?.goalDate?.let { gd -> advancedDateId.text = "Fecha objetivo: ${Helpers.dateToStr(gd, DateFormat.MEDIUM)}" }
                advancedId.text = "Mi Avance: ${detail.data?.goalAdvance?.toDouble()}%"
            } else
                ErrorMessage.notifyWithAction(requireView(), it.description, R.string.action_close) {  }
        }
    }

    private fun openLoyaltyPlans() {
        val navHostFragment = parentFragment as NavHostFragment?
        val parent: LoyaltyPlanFragment? = navHostFragment!!.parentFragment as LoyaltyPlanFragment?
        parent?.view?.findNavController()?.navigate(R.id.actionNavToAddNavId)
    }

    fun openDetail(id: String) {
        val intent = Intent(requireContext(), CouponDetail2Activity::class.java)
        intent.putExtra("couponId", id)
        intent.putExtra("loyaltyType", 1)
        startActivity(intent)
    }

}