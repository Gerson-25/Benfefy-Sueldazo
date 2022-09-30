package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
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
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.FavoriteData
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.activities.CouponDetail2Activity
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.PlanCardDetailResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.StampCoupon
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.PlanDetailObject
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.adapters.StampCouponAdapter
import com.syntepro.appbeneficiosbolivia.ui.lealtad.viewmodel.LoyaltyPlansDetailViewModel
import com.syntepro.appbeneficiosbolivia.ui.wallet.StampsAdapter
import com.syntepro.appbeneficiosbolivia.utils.Constants
import kotlinx.android.synthetic.main.fragment_card.*
import kotlinx.android.synthetic.main.fragment_card.listId
import kotlinx.android.synthetic.main.seals_empty_layout.*
import kotlinx.android.synthetic.main.stampo_coupon_item.view.*
import javax.inject.Inject

class CardFragment : BaseFragment() {

    @Inject
    lateinit var  stampAdapter: StampsAdapter
    @Inject
    lateinit var  stampCouponAdapter: StampCouponAdapter

    private lateinit var planDetailViewModel: LoyaltyPlansDetailViewModel

    override fun layoutId() = R.layout.fragment_card

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        planDetailViewModel = viewModel(viewModelFactory) {
            observe(card, ::detailResponse)
            failure(failure, ::handleFailure)
        }

        stampCouponAdapter.parentFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        if (arguments?.getString("planId").isNullOrEmpty()) {
            stampsId.visibility = View.INVISIBLE
            title.visibility = View.INVISIBLE
            listId.visibility = View.INVISIBLE
            emptyId.visibility = View.VISIBLE
        } else if (!Constants.idPushPlan.isNullOrEmpty()) {
            PlanDetailObject.getPlanDetail(Constants.idPushPlan!!, planDetailViewModel::cardPlanDetail)
            Constants.idPushPlan = ""
            Constants.idPlanType = 0
        } else
            PlanDetailObject.getPlanDetail(arguments?.getString("planId")!!, planDetailViewModel::cardPlanDetail)

        explorePlans.setOnClickListener { openLoyaltyPlans() }
    }

    override fun onDestroy() {
        super.onDestroy()
        arguments?.clear()
    }

    private fun initList() {
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        stampsId.layoutManager = linearLayoutManager
        stampsId.itemAnimator = DefaultItemAnimator()
        stampsId.adapter = stampAdapter

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
        listId.adapter = stampCouponAdapter
    }

    private fun detailResponse(detail: BaseResponse<PlanCardDetailResponse>?) {
        detail?.let {
            if (it.isSuccess) {
                Constants.isSelectedPlan = detail.data?.idPlan
                stampAdapter.collection = detail.data?.cards.orEmpty()
                stampCouponAdapter.collection = detail.data?.couponList.orEmpty()
            }
            else
                ErrorMessage.notifyWithAction(requireView(), it.description, R.string.action_close) {}
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
        intent.putExtra("loyaltyType", 2)
        startActivity(intent)
    }

    fun favorite(item: Any, position: Int) {
        val model = item as StampCoupon
        if (model.favorite)
            FavoriteData.removeFavorite(model.idCampana) { message: String, result: Boolean ->
                if (result) {
                    val viewHolder = listId.findViewHolderForAdapterPosition(position) as StampCouponAdapter.ViewHolder
                    val animScale = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_fav)
                    viewHolder.view.favorite.startAnimation(animScale)
                    viewHolder.view.favorite.setBackgroundResource(R.drawable.ic_mark_fav)
                } else Log.e("Favorite", message)
            }
        else
            FavoriteData.addFavorite(model.idCampana) { message: String, result: Boolean ->
                if (result) {
                    val viewHolder = listId.findViewHolderForAdapterPosition(position) as StampCouponAdapter.ViewHolder
                    val animScale = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_fav)
                    viewHolder.view.favorite.startAnimation(animScale)
                    viewHolder.view.favorite.setBackgroundResource(R.drawable.ic_unmark_fav)
                } else Log.e("Favorite", message)
            }
    }

}