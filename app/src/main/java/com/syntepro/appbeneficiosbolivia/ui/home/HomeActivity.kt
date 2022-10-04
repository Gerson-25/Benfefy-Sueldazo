package com.syntepro.appbeneficiosbolivia.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.appinvite.FirebaseAppInvite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.zxing.integration.android.IntentIntegrator
import com.merckers.core.exception.Failure
import com.merckers.core.extension.failure
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.core.AndroidApplication
import com.syntepro.appbeneficiosbolivia.core.app.ErrorMessage
import com.syntepro.appbeneficiosbolivia.core.di.ApplicationComponent
import com.syntepro.appbeneficiosbolivia.database.DataBaseAdapter
import com.syntepro.appbeneficiosbolivia.entity.service.PresentDetail
import com.syntepro.appbeneficiosbolivia.entity.service.UpdateActualCountryRequest
import com.syntepro.appbeneficiosbolivia.room.database.RoomDataBase
import com.syntepro.appbeneficiosbolivia.room.entity.CountryUser
import com.syntepro.appbeneficiosbolivia.service.NetworkService2
import com.syntepro.appbeneficiosbolivia.service.NotificationService
import com.syntepro.appbeneficiosbolivia.service.RetrofitClientInstance
import com.syntepro.appbeneficiosbolivia.ui.benefy.PromotionalCodeActivity
import com.syntepro.appbeneficiosbolivia.ui.commerce.ui.activities.CommerceDetail2Activity
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.BestDiscountRequest
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.FeaturedCouponRequest
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.activities.CouponDetail2Activity
import com.syntepro.appbeneficiosbolivia.ui.home.model.*
import com.syntepro.appbeneficiosbolivia.ui.home.viewModel.HomeViewModel
import com.syntepro.appbeneficiosbolivia.ui.login.LogoutService
import com.syntepro.appbeneficiosbolivia.ui.login.WelcomeActivity
import com.syntepro.appbeneficiosbolivia.ui.menu.*
import com.syntepro.appbeneficiosbolivia.ui.notifications.model.NotificationCountRequest
import com.syntepro.appbeneficiosbolivia.ui.notifications.ui.activities.NotificationsActivity
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities.ShopDetailActivity
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities.SuccessGiftUserActivity
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.userCountry
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.userTimeZone
import kotlinx.android.synthetic.main.app_bar_main.ntfId
import kotlinx.android.synthetic.main.app_bar_main.scanId
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {

    // Abel Acosta
    private lateinit var navView: NavigationView
    private lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var homeViewModel: HomeViewModel

    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as AndroidApplication).appComponent
    }

    private val adapter by lazy { DataBaseAdapter(this.applicationContext) }
    private val roomDataBase by lazy { RoomDataBase.getRoomDatabase(this) }
    private lateinit var profileImage: ImageView
    private var analytics: FirebaseAnalytics? = null
    private var changeCountry: Spinner? = null
    private var nomCountry: ArrayList<String>? = arrayListOf()
    private val flags = intArrayOf(R.drawable.sv, R.drawable.bo, R.drawable.gt)

    @RequiresApi(Build.VERSION_CODES.M)
    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_drawer)
        appComponent.inject(this)

        homeViewModel = viewModel(viewModelFactory) {
            failure(failure, ::handleError)
        }

        // Initialize SQLite
        adapter.createDatabase()

        // Dynamic Link
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                // Get deep link from result (may be null if no link is found)
                if (pendingDynamicLinkData != null) {
                    analytics = FirebaseAnalytics.getInstance(this@HomeActivity)
                    val deepLink = pendingDynamicLinkData.link
                    val country = deepLink?.getQueryParameter("country")
                    val couponId = deepLink?.getQueryParameter("idCoupon")
                    val commerceId = deepLink?.getQueryParameter("commerce")
                    val productId = deepLink?.getQueryParameter("idProduct")
                    val type = deepLink?.getQueryParameter("productType")?.toInt()
                    val promotionalCode = deepLink?.getQueryParameter("couponCodePromotional")
                    country?.let {
                        if (it != userCountry) {
                            val builder = AlertDialog.Builder(this@HomeActivity)
                            adapter.open()
                            val cu = adapter.getCountryInfoAbr(it)
                            adapter.close()
                            builder.setMessage(getString(R.string.deep_coupon_redirect) + cu.nombre + getString(R.string.deep_coupon_redirect_2))
                            builder.setPositiveButton(getString(R.string.yes)) { dialog: DialogInterface, _: Int ->
                                val tz = TimeZone.getTimeZone(userTimeZone)
                                val c = Calendar.getInstance(tz)
                                roomDataBase.accessDao().dropCountry()
                                val newCountryUser = CountryUser()
                                newCountryUser.id = 1
                                newCountryUser.fechaActualizacion = c.time
                                adapter.open()
                                val selectedCountry = adapter.getCountryConfiguration(cu.nombre)
                                newCountryUser.pais = selectedCountry.nombre
                                newCountryUser.abr = selectedCountry.abreviacion
                                newCountryUser.codArea = selectedCountry.codigoArea
                                newCountryUser.moneda = selectedCountry.moneda
                                newCountryUser.timeZone = selectedCountry.timeZone
                                adapter.close()

                                val user = Constants.userProfile
                                user?.actualCountry = selectedCountry.abreviacion
                                user?.areaCode = selectedCountry.codigoArea
                                user?.currency = selectedCountry.moneda
                                Constants.userProfile = user
                                Functions.savePersistentProfile(this@HomeActivity)
                                updateUserCountry(selectedCountry.abreviacion)

                                roomDataBase.accessDao().addCountryUser(newCountryUser)
                                dialog.cancel()

                                loadGlobalData()

                                if (!couponId.isNullOrEmpty()) {
                                    val intent = Intent(this, CouponDetail2Activity::class.java)
                                    intent.putExtra("couponId", couponId)
                                    startActivity(intent)
                                    overridePendingTransition(R.anim.fade_in_desv, R.anim.fade_out_desv)
                                } else if (!commerceId.isNullOrEmpty()) {
                                    val intent = Intent(this@HomeActivity, CommerceDetail2Activity::class.java)
                                    intent.putExtra("commerceId", commerceId)
                                    startActivity(intent)
                                    overridePendingTransition(R.anim.fade_in_desv, R.anim.fade_out_desv)
                                } else if (!promotionalCode.isNullOrEmpty()) {
                                    val intent = Intent(this@HomeActivity, PromotionalCodeActivity::class.java)
                                    intent.putExtra("allowAccess", true)
                                    intent.putExtra("automaticAssign", true)
                                    intent.putExtra("promotionalCode", promotionalCode)
                                    startActivityForResult(intent, 175)
                                } else if (!productId.isNullOrEmpty()) {
                                    val intent = Intent(this@HomeActivity, ShopDetailActivity::class.java)
                                    intent.putExtra("couponId", productId)
                                    intent.putExtra("type", type)
                                    startActivity(intent)
                                    overridePendingTransition(R.anim.fade_in_desv, R.anim.fade_out_desv)
                                } else Log.e("Error", "No deep Link")
                            }

                            builder.setNegativeButton(getString(R.string.no)) { dialog: DialogInterface, _: Int -> dialog.cancel() }

                            val dialog = builder.create()
                            dialog.setCancelable(false)
                            dialog.show()
                        } else {
                            if (!couponId.isNullOrEmpty()) {
                                val intent = Intent(this, CouponDetail2Activity::class.java)
                                intent.putExtra("couponId", couponId)
                                startActivity(intent)
                                overridePendingTransition(R.anim.fade_in_desv, R.anim.fade_out_desv)
                            } else if (!commerceId.isNullOrEmpty()) {
                                val intent = Intent(this@HomeActivity, CommerceDetail2Activity::class.java)
                                intent.putExtra("commerceId", commerceId)
                                startActivity(intent)
                                overridePendingTransition(R.anim.fade_in_desv, R.anim.fade_out_desv)
                            } else if (!promotionalCode.isNullOrEmpty()) {
                                val intent = Intent(this@HomeActivity, PromotionalCodeActivity::class.java)
                                intent.putExtra("allowAccess", true)
                                intent.putExtra("automaticAssignment", true)
                                intent.putExtra("promotionalCode", promotionalCode)
                                startActivityForResult(intent, 175)
                            } else if (!productId.isNullOrEmpty()) {
                                val intent = Intent(this@HomeActivity, ShopDetailActivity::class.java)
                                intent.putExtra("couponId", productId)
                                intent.putExtra("type", type)
                                startActivity(intent)
                                overridePendingTransition(R.anim.fade_in_desv, R.anim.fade_out_desv)
                            } else Log.e("Error", "No deep Link")
                        }
                    }
                    val invite = FirebaseAppInvite.getInvitation(pendingDynamicLinkData)
                    if (invite != null) {
                        val invitationId = invite.invitationId
                        if (!TextUtils.isEmpty(invitationId)) Log.e("Invitation ID", invitationId)
                    }
                }
            }.addOnFailureListener(this) { e: java.lang.Exception? -> Log.w("Dynamic Link", "getDynamicLink:onFailure", e) }

        // GPS Permission
        if (!runtimePermissions()) enableService()

        // Abel Acosta
        initNavigation()
        // Load Global data
        loadGlobalData()

        // Get the primary text color of the theme
        val typedValue = TypedValue()
        val theme = this.theme
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
        @SuppressLint("Recycle") val arr = this.obtainStyledAttributes(typedValue.data, intArrayOf(android.R.attr.textColorPrimary))
        val primaryColor = arr.getColor(0, -1)

        scanId.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Scanner")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(false)
            integrator.setOrientationLocked(false)
            integrator.setBarcodeImageEnabled(false)
            integrator.initiateScan()
        }

        ntfId.setOnClickListener {
            val intent = Intent(this@HomeActivity, NotificationsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 175 && resultCode == Activity.RESULT_OK) {
            data?.let {
                val code = it.getBooleanExtra("code", false)
                val model = it.getSerializableExtra("model") as? PresentDetail
                if (code) getPurchaseProducts()
                model?.let { pd ->
                    getPurchaseProducts()
                    val intent = Intent(this@HomeActivity, SuccessGiftUserActivity::class.java)
                    intent.putExtra("model", pd)
                    startActivity(intent)
                }
            }
        } else {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) Toast.makeText(this, getString(R.string.scanner), Toast.LENGTH_SHORT).show()
                else startActivity(ScannerActivity.getIntent(this, result.contents, getString(R.string.campain)))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) enableService()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        try {
            val extras = intent?.extras
            if (extras != null) {
                Log.e("NewIntent", "Ok data")
                val url = extras.getBoolean("paid", false)
                val loyaltyPush = extras.getBoolean("loyaltyPush")
                val planType = extras.getInt("planType")
                val idPlan = extras.getString("idPlan")
                Log.e("NewIntent", "Ok method - $url $loyaltyPush $planType $idPlan")
                if (url) navController.navigate(R.id.nav_benfy)
                else if (loyaltyPush) {
                    Constants.idPushPlan = idPlan
                    Constants.idPlanType = planType
                    navController.navigate(R.id.nav_lealtad)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Web Service Functions
     */
    private fun updateUserCountry(newCountry: String?) {
        val request = with(UpdateActualCountryRequest()) {
            country = Constants.userProfile?.country
            language = 1
            idUser = Constants.userProfile?.idUser
            idUserFirebase = Constants.userProfile?.idUserFirebase
            actualCountry = newCountry
            this
        }
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2).create(NetworkService2::class.java)
        scopeMainThread.launch {
            try {
                val response = apiService.updateActualCountry(request)
                when {
                    response.isSuccessful -> {
                        val ret = response.body()!!
                        if (ret.isSuccess) Log.e("Update", "Success")
                        else Log.e("Error", "${ret.code}")
                    }
                    else -> { Log.e("Error", "${response.message()} - ${response.errorBody()}") }
                }
            } catch (e: Exception) {
                Log.e("Exception", e.message ?: e.cause?.message ?: e.cause.toString())
            }
        }
    }


    private fun runtimePermissions(): Boolean {
        return Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
    }

    private fun enableService() {
        val i = Intent(applicationContext, NotificationService::class.java)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) this@HomeActivity.startService(i) else this@HomeActivity.startForegroundService(i)
    }

    private fun updateUI() {
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            LogoutService.firebaseLogout()
        }
        LoginManager.getInstance().logOut()
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    // Abel Acosta
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initNavigation() {
        navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // Las opciones que van aqui aparecen con icon burguer, el resto con flecha back

        NavigationUI.setupWithNavController(bottom_navigation_view, navController)
        // setupActionBarWithNavController(navController, appBarConfiguration)

        if (Constants.userProfile?.actualCountry == "BO") {
            try {
                navController.navigate(R.id.nav_benfy)
            } catch (e: Exception) { Log.e("Exception", e.printStackTrace().toString()) }
        }

        // Extras
        // Reescribir esta invocacion
        val extras = intent.extras
        if (extras != null) {
            val url = extras.getBoolean("paid", false)
            val loyaltyPush = extras.getBoolean("loyaltyPush")
            val planType = extras.getInt("planType")
            val idPlan = extras.getString("idPlan")
            if (url) navController.navigate(R.id.nav_benfy)
            else if (loyaltyPush) {
                Constants.idPushPlan = idPlan
                Constants.idPlanType = planType
//                val navController = Navigation.findNavController(this@HomeActivity, R.id.nav_host_fragment)
//                navController.navigateUp()
//                val bundle = bundleOf("planId" to idPlan)
                navController.navigate(R.id.nav_lealtad)
            }
        }
    }

    inline fun <reified T : ViewModel> viewModel(
        factory: ViewModelProvider.Factory,
        body: T.() -> Unit
    ): T {
        val vm = ViewModelProviders.of(this, factory)[T::class.java]
        vm.body()
        return vm
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        // setupBottomNavigationBar()
    }

    override fun onSupportNavigateUp(): Boolean {
        // val navController = findNavController(R.id.nav_host_fragment)
        // return navController.navigateUp(appBarConfiguration)|| super.onSupportNavigateUp()
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    private fun getCategories() {
        val request = CategoryRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            filterType = 1
        )
        homeViewModel.loadCategories(request)
    }

    private fun getParams() {
        val request = ParameterRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage()
        )
        homeViewModel.loadParams(request)
    }

    private fun getCounter() {
        val request = NotificationCountRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            idUser = Constants.userProfile?.idUser ?: ""
        )
        homeViewModel.notificationCounter(request)
    }

    private fun getBestDiscounts() {
        val request = BestDiscountRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            idUser = Constants.userProfile?.idUser ?: "",
            sortType = BestDiscountRequest.SORT_TYPE_DISCOUNT,
            longitude = null,
            latitude = null
        )
        homeViewModel.loadBestDiscounts(request)
    }

    private fun getBanners() {
        val request = BannerRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage()
        )
        homeViewModel.loadBanners(request)
    }

    private fun getPurchaseProducts() {
        val request = PurchasedProductsRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = 1,
            recordsNumber = Constants.LIST_PAGE_SIZE,
            pageNumber = 1,
            sortType = 1,
            idUser = Constants.userProfile?.idUser ?: ""
        )
        homeViewModel.loadPurchaseProducts(request)
    }

    private fun getStates() {
        val request = StatesRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = 1
        )
        homeViewModel.loadStates(request)
    }

    private fun getFeaturedCoupons() {
        val request = FeaturedCouponRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = 1,
            idUser = Constants.userProfile?.idUser ?: ""
        )
        homeViewModel.loadFeaturedCoupons(request)
    }

    private fun getUserSavings() {
        val request = UserSavingsRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = 1,
            idUserFirebase = Constants.userProfile?.idUserFirebase ?: ""
        )
        homeViewModel.loadUserSavings(request)
    }

    private fun handleError(failure: Failure?) {
        val parentLayout = findViewById<View>(android.R.id.content)
        ErrorMessage.notifyWithAction(
            parentLayout,
            failure!!.getMessage()
                ?: "",
            R.string.action_close
        ) {}
    }

    /**
     * Gerson Aquino 28JUN2021
     *
     *getGiftCards() method needs to be
     * called after items are loaded to
     * verify if it is necesary
     * to show giftcards first
     *
     */
    private fun loadGlobalData() {
        getCategories()
        getParams()
        getBestDiscounts()
        getBanners()
        getPurchaseProducts()
        getStates()
        getFeaturedCoupons()
        getUserSavings()
        if (Constants.TOKEN.isNotEmpty()) getCounter()
    }

    /**
     * Gerson Aquino 28JUN2021
     *
     *companion object was created to control the time
     * to show articles or giftcard on HomeFragment
     *
     */
    companion object {
        var make_sort = false
    }
}
