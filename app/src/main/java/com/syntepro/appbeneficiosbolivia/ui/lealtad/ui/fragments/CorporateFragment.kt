package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.core.app.ErrorMessage
import com.syntepro.appbeneficiosbolivia.core.base.BaseFragment
import com.syntepro.appbeneficiosbolivia.core.base.viewModel
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.FavoriteData
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.activities.CouponDetail2Activity
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.CorporateCoupon
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.PlanCorporateDetailResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.PlanDetailObject
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.adapters.CouponAdapter
import com.syntepro.appbeneficiosbolivia.ui.lealtad.viewmodel.LoyaltyPlansDetailViewModel
import com.syntepro.appbeneficiosbolivia.utils.Constants
import kotlinx.android.synthetic.main.corporate_empty_layout.*
import kotlinx.android.synthetic.main.corporate_item.view.*
import kotlinx.android.synthetic.main.coupon_empty_layout.*
import kotlinx.android.synthetic.main.fragment_corporate.*
import javax.inject.Inject

class CorporateFragment : BaseFragment() {

    @Inject
    lateinit var  couponAdapter: CouponAdapter
    private lateinit var planDetailViewModel: LoyaltyPlansDetailViewModel

    override fun layoutId() = R.layout.fragment_corporate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        planDetailViewModel = viewModel(viewModelFactory) {
            observe(corporate, ::detailResponse)
            failure(failure, ::handleFailure)
        }

        couponAdapter.parentFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        if (arguments?.getString("planId").isNullOrEmpty()) {
            corporateEmptyId.visibility = View.VISIBLE
            listId.visibility = View.GONE
        } else {
            listId.visibility = View.VISIBLE
            PlanDetailObject.getPlanDetail(arguments?.getString("planId")!!, planDetailViewModel::corporatePlanDetail)
        }

        explorePlans.setOnClickListener {
            openLoyaltyPlans()
        }
    }

    private fun initList() {
        listId.setHasFixedSize(true)
        listId.layoutManager = GridLayoutManager(requireContext(), 2)
        listId.adapter = couponAdapter
    }

    private fun detailResponse(detail: BaseResponse<PlanCorporateDetailResponse>?) {
        if (detail?.data?.couponList.isNullOrEmpty()) emptyId.visibility = View.VISIBLE else emptyId.visibility = View.GONE
        detail?.let {
            if (it.isSuccess) {
                Constants.isSelectedPlan = detail.data?.idPlan
                couponAdapter.collection = detail.data?.couponList.orEmpty()
            } else
                ErrorMessage.notifyWithAction(requireView(), it.description, R.string.action_close) { }
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
        startActivity(intent)
    }

    fun favorite(item: Any, position: Int) {
        val model = item as CorporateCoupon
        if (model.favorite)
            FavoriteData.removeFavorite(model.idCampana) { message: String, result: Boolean ->
                if (result) {
                    val viewHolder = listId.findViewHolderForAdapterPosition(position) as CouponAdapter.ViewHolder
                    val animScale = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_fav)
                    viewHolder.view.favorite.startAnimation(animScale)
                    viewHolder.view.favorite.setBackgroundResource(R.drawable.ic_mark_fav)
                } else Log.e("Favorite", message)
            }
        else
            FavoriteData.addFavorite(model.idCampana) { message: String, result: Boolean ->
                if (result) {
                    val viewHolder = listId.findViewHolderForAdapterPosition(position) as CouponAdapter.ViewHolder
                    val animScale = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_fav)
                    viewHolder.view.favorite.startAnimation(animScale)
                    viewHolder.view.favorite.setBackgroundResource(R.drawable.ic_unmark_fav)
                } else Log.e("Favorite", message)
            }
    }

}