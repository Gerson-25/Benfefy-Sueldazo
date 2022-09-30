package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui

import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.PlanByUserDetailRequest
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.fragments.LoyaltyInfoFragment
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions

object PlanDetailObject {

    fun getPlanDetail(idPlan: String, vm: (PlanByUserDetailRequest)-> Unit) {
        val request = PlanByUserDetailRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                recordsNumber = Constants.LIST_PAGE_SIZE,
                pageNumber = 1,
                idPlanUser = idPlan
        )
        vm(request)
    }
}