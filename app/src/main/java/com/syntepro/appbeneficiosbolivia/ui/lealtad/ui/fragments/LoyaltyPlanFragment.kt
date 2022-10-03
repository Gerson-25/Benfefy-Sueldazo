package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.core.base.BaseFragment
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.entity.service.Category
import com.syntepro.appbeneficiosbolivia.ui.home.HomeActivity
import com.syntepro.appbeneficiosbolivia.ui.home.viewModel.HomeViewModel
import com.syntepro.appbeneficiosbolivia.ui.lealtad.TermsActivity
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.Loyalty
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.LoyaltyPlanByUser
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.LoyaltyPlanListResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.UnlinkPlanRequest
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.adapters.LoyaltyPlanAdapter
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.readUserInfo
import kotlinx.android.synthetic.main.add_loyalty_plan_dialog.listId
import kotlinx.android.synthetic.main.fragment_loyalty_plan.*
import javax.inject.Inject

class LoyaltyPlanFragment : BaseFragment() {
    @Inject
    lateinit var planAdapter: LoyaltyPlanAdapter

    private lateinit var linearLayoutManager: LinearLayoutManager
    private var plansList: LoyaltyPlanListResponse? = null
    private val homeViewModel: HomeViewModel by activityViewModels()
    private var currentGraph: Int = 0
    private var currentVisibleItemRecyclerView = -1
    var currentSelectedPlan: Int = 0

    override fun layoutId() = R.layout.fragment_loyalty_plan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setUpToolbar()

        this.observe(homeViewModel.categories, ::categoriesResponse)
        this.observe(homeViewModel.plans, ::plansResponse)
        this.observe(homeViewModel.unlink, ::unlinkResponse)
        this.failure(homeViewModel.failure, ::handleFailure)

        swipeRefreshLayout.setOnRefreshListener {
            (activity as HomeActivity). getLoyaltyPlans()
            fragmentManager?.beginTransaction()
                    ?.detach(LoyaltyPlanFragment())
                    ?.attach(LoyaltyPlanFragment())
                    ?.commit()
            swipeRefreshLayout.isRefreshing = false
        }

        initList()
        initControls()
        readUserInfo(userImageId, welcomeId, null)
    }

    private fun setUpToolbar() {
        val mainActivity = activity as HomeActivity
        mainActivity.setSupportActionBar(toolbarId)
        val navController = NavHostFragment.findNavController(this)
        val appBarConfiguration = mainActivity.appBarConfiguration
        NavigationUI.setupActionBarWithNavController(mainActivity, navController, appBarConfiguration)
    }

    private fun initControls() {
        informationId.setOnClickListener { view?.findNavController()?.navigate(R.id.actionNavToAddNavId) }
        planTypeId.setOnClickListener { showMenu(it) }
        addPlanId.setOnClickListener { openLoyaltyPlans(currentSelectedPlan) }
        qrId.setOnClickListener { Functions.showUserQR(requireContext()) }
        leftId.setOnClickListener { leftItem(currentVisibleItemRecyclerView) }
        rightId.setOnClickListener { rightItem(currentVisibleItemRecyclerView) }
    }

    private fun openLoyaltyPlans(plan: Int) {
        //val action = LoyaltyInfoFragmentDirections.actionNavLealtadToAddPlanFragment(plan)
        view?.findNavController()?.navigate(R.id.actionNavToAddNavId)
    }

    private fun initList() {
        linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        listId.layoutManager = linearLayoutManager
        listId.setHasFixedSize(true)
        listId.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        listId.adapter = planAdapter
        listId.addOnScrollListener(recyclerViewOnScrollListener)
        planAdapter.parentFragment(this)
    }

    private val recyclerViewOnScrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
//                    val visibleItemCount: Int = linearLayoutManager.childCount
//                    val totalItemCount: Int = linearLayoutManager.itemCount
                    val firstVisibleItemPosition: Int = linearLayoutManager.findFirstVisibleItemPosition()
                    Log.e(
                            " Position",
                            " currentVisibleItemRecyclerView $firstVisibleItemPosition"
                    )
                    if (currentVisibleItemRecyclerView != firstVisibleItemPosition) {
                        if (linearLayoutManager.itemCount != 0) {
                            listId.smoothScrollToPosition(firstVisibleItemPosition)
                            currentVisibleItemRecyclerView = firstVisibleItemPosition
                            val value = planAdapter.collection
                            if (value.isNotEmpty()) setNavigation(value[currentVisibleItemRecyclerView].idLoyaltyPlan)
                        }
                    }
                }
            }

    private fun categoriesResponse(categories: BaseResponse<List<Category>>?) {

    }

    private fun plansResponse(plans: BaseResponse<LoyaltyPlanListResponse>?) {
        plansList = plans?.data
        var planToView = getFirstPlanType()
        if (Constants.idPlanType != null && Constants.idPlanType != 0) planToView = Constants.idPlanType ?: getFirstPlanType()
        setPlanType(planToView)
        showData(planToView)
    }

    private fun unlinkResponse(response: BaseResponse<Boolean>?) {
        response?.data?.let {
            if (it) {
                (activity as HomeActivity).getLoyaltyPlans()
                fragmentManager?.beginTransaction()
                        ?.detach(LoyaltyPlanFragment())
                        ?.attach(LoyaltyPlanFragment())
                        ?.commit()
            } else Log.e("Plan User", "Error -> ${response.description}")
        }
    }

    /**
     * App Functions
     */
    private fun showData(planToView: Int) {
        when(planToView) {
            Loyalty.LOYALTY_PLAN_CORPORATE -> planAdapter.collection = plansList?.corporatePlans.orEmpty()
            Loyalty.LOYALTY_PLAN_SEALS -> planAdapter.collection = plansList?.cardPlans.orEmpty()
            Loyalty.LOYALTY_PLAN_MILES -> planAdapter.collection = plansList?.milesPlan.orEmpty()
        }
    }

    private fun TextView.leftDrawable(@DrawableRes id: Int = 0, size: Int) {
        val drawable = ContextCompat.getDrawable(context, id)
        drawable?.setBounds(0, 0, size, size)
        this.setCompoundDrawables(drawable, null, null, null)
    }

    private fun showMenu(anchor: View?): Boolean {
        val popup = PopupMenu(context, anchor)
        popup.menuInflater.inflate(R.menu.plan_type_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            val id = when(it.itemId) {
                R.id.sealId -> Loyalty.LOYALTY_PLAN_SEALS
                R.id.corporateId -> Loyalty.LOYALTY_PLAN_CORPORATE
                else -> Loyalty.LOYALTY_PLAN_MILES
            }
            setPlanType(id)
            showData(id)
            true
        }
        popup.show()
        return true
    }

    private fun getFirstPlanType(): Int =
        if(!plansList?.corporatePlans.isNullOrEmpty()) Loyalty.LOYALTY_PLAN_CORPORATE
        else if(!plansList?.milesPlan.isNullOrEmpty())  Loyalty.LOYALTY_PLAN_MILES
        else  Loyalty.LOYALTY_PLAN_SEALS

    private fun setPlanType(planType: Int) {
        when(planType) {
            Loyalty.LOYALTY_PLAN_CORPORATE -> {
                planNameId.text = getString(R.string.corporate_label)
                planNameId.leftDrawable(R.drawable.ic_lealtad_corporativo, 50)
                currentGraph = R.id.nav_corporate_id
                currentSelectedPlan = Loyalty.LOYALTY_PLAN_CORPORATE
                plansList?.corporatePlans?.let {
                    if (it.isNotEmpty()) setNavigation(it[0].idLoyaltyPlan)
                    else setNavigation("")
                } ?: run {
                    setNavigation("")
                }
            }
            Loyalty.LOYALTY_PLAN_SEALS -> {
                planNameId.text = getString(R.string.seal_label)
                planNameId.leftDrawable(R.drawable.ic_lealtad_sellos, 50)
                currentGraph = R.id.nav_seals_id
                currentSelectedPlan = Loyalty.LOYALTY_PLAN_SEALS
                plansList?.cardPlans?.let {
                    if (it.isNotEmpty()) setNavigation(it[0].idLoyaltyPlan)
                    else setNavigation("")
                } ?: run {
                    setNavigation("")
                }
            }
            Loyalty.LOYALTY_PLAN_MILES -> {
                planNameId.text = getString(R.string.miles_label)
                planNameId.leftDrawable(R.drawable.ic_lealtad_millas, 50)
                currentGraph = R.id.nav_miles_id
                currentSelectedPlan = Loyalty.LOYALTY_PLAN_MILES
                plansList?.milesPlan?.let {
                    if (it.isNotEmpty()) setNavigation(it[0].idLoyaltyPlan)
                    else setNavigation("")
                } ?: run {
                    setNavigation("")
                }
            }
        }
    }

    fun setNavigation(planId: String) {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.local_nav_host_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.loyalty_detail_nav)
        graph.startDestination = currentGraph
        val bundle = bundleOf("planId" to planId)
        navHostFragment.navController.setGraph(graph, bundle)
    }

    private fun leftItem(position: Int) {
        if (position > 0) {
            val toPosition = position - 1
//            currentVisibleItemRecyclerView = toPosition
            listId.smoothScrollToPosition(toPosition)
//            val value = planAdapter.collection
//            if (value.isNotEmpty()) setNavigation(value[toPosition].idLoyaltyPlan)
        }
    }

    private fun rightItem(position: Int) {
        if (position < linearLayoutManager.itemCount - 1) {
            val toPosition = position + 1
//            currentVisibleItemRecyclerView = toPosition
            listId.smoothScrollToPosition(toPosition)
//            val value = planAdapter.collection
//            if (value.isNotEmpty()) setNavigation(value[toPosition].idLoyaltyPlan)
        }
    }

    fun unlink(id: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.unlink_loyalty_plan))
        builder.setPositiveButton(getString(R.string.yes)) { _: DialogInterface?, _: Int ->
            val request = UnlinkPlanRequest(
                    country = Constants.userProfile?.actualCountry ?: "BO",
                    language = Functions.getLanguage(),
                    idUserPlan = id,
                    username = "${Constants.userProfile?.names} ${Constants.userProfile?.lastNames}"
            )
            homeViewModel.unlinkPlan(request)
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()
    }

    fun showTerms(model: LoyaltyPlanByUser) {
        val intent = Intent(requireContext(), TermsActivity::class.java)
        intent.putExtra("planId", Constants.isSelectedPlan)
        intent.putExtra("mCommerceName", model.commerceName)
        intent.putExtra("mCommerceImage", model.commerceImage)
        intent.putExtra("mRubroName", "")
        intent.putExtra("planDesc", "")
        intent.putExtra("planName", model.name)
        intent.putExtra("mCode", "")
        intent.putExtra("affiliate", false)
        startActivity(intent)
    }

}