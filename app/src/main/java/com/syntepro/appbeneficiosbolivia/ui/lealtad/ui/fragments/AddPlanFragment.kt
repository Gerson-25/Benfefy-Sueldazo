package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.core.base.BaseFragment
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.entity.service.Category
import com.syntepro.appbeneficiosbolivia.ui.home.HomeActivity
import com.syntepro.appbeneficiosbolivia.ui.home.model.CategoryRequest
import com.syntepro.appbeneficiosbolivia.ui.home.viewModel.HomeViewModel
import com.syntepro.appbeneficiosbolivia.ui.lealtad.AddLoyaltyPlanDialog
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.Commerce
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.CommerceRequest
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.adapters.CategoryAdapter
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.adapters.CommerceAdapter
import com.syntepro.appbeneficiosbolivia.ui.location.LocationService
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.fragment_add_plan.*
import kotlinx.android.synthetic.main.fragment_add_plan.toolbarId
import kotlinx.android.synthetic.main.fragment_loyalty_plan.listId
import javax.inject.Inject

class AddPlanFragment : BaseFragment() {
    @Inject
    lateinit var categoryAdapter: CategoryAdapter
    @Inject
    lateinit var commerceAdapter: CommerceAdapter

    private val fragmentArgs: LoyaltyPlanFragmentArgs by navArgs()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private var lastLongitude: Double? = null
    private var lastLatitude: Double? = null

    override fun layoutId() = R.layout.fragment_add_plan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        val planType = fragmentArgs.planType
        this.observe(homeViewModel.categories, ::categoriesResponse)
        this.failure(homeViewModel.failure, ::handleFailure)
        this.observe(homeViewModel.commerce, ::commerceResponse)
        initList()
        commerceAdapter.setFragment(this)
        categoryAdapter.parentFragment(this)

        if (runtimePermissions()){
            LocationService.getFusedLocation(requireActivity()){ long, lat ->
                lastLatitude = lat
                lastLongitude = long
                getCategories()
            }
        } else getCategories()
    }

    private fun setUpToolbar() {
        val mainActivity = activity as HomeActivity
        mainActivity.setSupportActionBar(toolbarId)
        val navController = NavHostFragment.findNavController(this)
        val appBarConfiguration = mainActivity.appBarConfiguration
        NavigationUI.setupActionBarWithNavController(mainActivity,navController,appBarConfiguration)
    }

    private fun initList() {
        listId.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        listId.layoutManager = linearLayoutManager
        listId.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        listId.adapter = categoryAdapter

        commerceListId.setHasFixedSize(true)
        val llm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        commerceListId.layoutManager = llm
        commerceListId.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        commerceListId.adapter = commerceAdapter
    }

    private fun runtimePermissions(): Boolean {
        return Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission(requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCategories() {
        val request = CategoryRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            filterType = 3
        )
        homeViewModel.loadCategories(request)
    }

    fun getCommerce(category: String) {
        val request = CommerceRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            recordsNumber = Constants.LIST_PAGE_SIZE,
            pageNumber = 1,
            idCategory = category,
            loyaltyPlan = true,
            idUser = Constants.userProfile?.idUser ?: "",
            longitude = lastLatitude,
            latitude = lastLongitude
        )
        homeViewModel.commerceByCategory(request)
    }

    private fun categoriesResponse(categories: BaseResponse<List<Category>>?) {
        if(!categories?.data.isNullOrEmpty()) {
            categoryAdapter.collection = categories?.data!!
            getCommerce(categories.data[0].idCategory!!)
        }
    }

    private fun commerceResponse(list: BaseResponse<List<Commerce>>?) {
        commerceAdapter.collection = list?.data.orEmpty()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        homeViewModel.clearCommerceData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.clearCommerceData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AddLoyaltyPlanDialog.ADD_LOYALTY_PLAN_DIALOG_ID && resultCode == AppCompatActivity.RESULT_OK) {
            // Reload loyalty plans
            (activity as HomeActivity).getLoyaltyPlans()
        }
    }

    fun addPlan(model: Commerce) {
        val intent = Intent(requireContext(), AddLoyaltyPlanDialog::class.java)
        intent.putExtra("commerceId", model.idComercio)
        intent.putExtra("commerceImage", model.urlImage)
        intent.putExtra("rubroName", model.categoryName)
        intent.putExtra("commerceName", model.nombre)
        intent.putExtra("commerceBanner", "")
        startActivityForResult(intent, AddLoyaltyPlanDialog.ADD_LOYALTY_PLAN_DIALOG_ID)
    }

}