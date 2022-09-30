package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.core.base.BaseFragment
import com.syntepro.appbeneficiosbolivia.ui.home.viewModel.HomeViewModel
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.Loyalty
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.LoyaltyPlanListResponse
import kotlinx.android.synthetic.main.fragment_loyalty_info.*

class LoyaltyInfoFragment: BaseFragment() {

    private val loyaltyPlanViewModel: HomeViewModel by activityViewModels()

    override fun layoutId() = R.layout.fragment_loyalty_info

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.observe(loyaltyPlanViewModel.plans, ::plansResponse)
        this.failure(loyaltyPlanViewModel.failure, ::handleFailure)
        showProgress()
        //getLoyaltyPlans()
        corporateId.setOnClickListener { openLoyaltyPlans(Loyalty.LOYALTY_PLAN_CORPORATE) }
        milesId.setOnClickListener { openLoyaltyPlans(Loyalty.LOYALTY_PLAN_MILES) }
        sealsId.setOnClickListener { openLoyaltyPlans(Loyalty.LOYALTY_PLAN_SEALS) }
    }

    private fun openLoyaltyPlans(plan: Int) {
        val action = LoyaltyInfoFragmentDirections.actionNavLealtadToAddPlanFragment(plan)
        view?.findNavController()?.navigate(action)
    }

    private fun plansResponse(response: BaseResponse<LoyaltyPlanListResponse>?) {
        val view = view ?: return
        if (response?.data?.cardPlans.isNullOrEmpty() &&
                response?.data?.corporatePlans.isNullOrEmpty() &&
                response?.data?.milesPlan.isNullOrEmpty())
            showView()
        else
            findNavController(view).navigate(
                    LoyaltyInfoFragmentDirections.actionNavLealtadToNavLoyaltyPlan())
    }

    private fun showView() {
        hideProgress()
        mainId.visibility = View.VISIBLE
    }

}