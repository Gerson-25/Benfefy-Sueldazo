package com.appbenefy.sueldazo.ui.home

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
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.core.AndroidApplication
import com.appbenefy.sueldazo.core.app.ErrorMessage
import com.appbenefy.sueldazo.core.di.ApplicationComponent
import com.appbenefy.sueldazo.database.DataBaseAdapter
import com.appbenefy.sueldazo.entity.service.PresentDetail
import com.appbenefy.sueldazo.entity.service.UpdateActualCountryRequest
import com.appbenefy.sueldazo.room.database.RoomDataBase
import com.appbenefy.sueldazo.room.entity.CountryUser
import com.appbenefy.sueldazo.service.NetworkService2
import com.appbenefy.sueldazo.service.NotificationService
import com.appbenefy.sueldazo.service.RetrofitClientInstance
import com.appbenefy.sueldazo.ui.benefy.PromotionalCodeActivity
import com.appbenefy.sueldazo.ui.commerce.ui.activities.CommerceDetail2Activity
import com.appbenefy.sueldazo.ui.coupon.ui.activities.CouponDetail2Activity
import com.appbenefy.sueldazo.ui.coupon.ui.activities.RatingActivity
import com.appbenefy.sueldazo.ui.home.model.*
import com.appbenefy.sueldazo.ui.home.viewModel.HomeViewModel
import com.appbenefy.sueldazo.ui.login.CredentialsActivity
import com.appbenefy.sueldazo.ui.login.LogoutService
import com.appbenefy.sueldazo.ui.login.WelcomeActivity
import com.appbenefy.sueldazo.ui.menu.*
import com.appbenefy.sueldazo.ui.notifications.model.NotificationCountRequest
import com.appbenefy.sueldazo.ui.notifications.ui.activities.NotificationsActivity
import com.appbenefy.sueldazo.utils.Constants
import com.appbenefy.sueldazo.utils.Functions
import com.appbenefy.sueldazo.utils.Functions.Companion.userCountry
import com.appbenefy.sueldazo.utils.Functions.Companion.userTimeZone
import com.appbenefy.sueldazo.utils.UserType
import kotlinx.android.synthetic.main.app_bar_main.*
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
    private var analytics: FirebaseAnalytics? = null
    private var userName = ""

    @RequiresApi(Build.VERSION_CODES.M)
    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_drawer)
        appComponent.inject(this)

        val extras = intent.extras
        if (extras != null) {
            userName = extras.getString("userName", "")!!
        }

        homeViewModel = viewModel(viewModelFactory) {
            failure(failure, ::handleError)
        }

        showData()

        // Initialize SQLite
        adapter.createDatabase()

        // Dynamic Link
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                // Get deep link from result (may be null if no link is found)
                if (Constants.TYPE_OF_USER == UserType.ANONYMOUSE_USER) return@addOnSuccessListener
                if (pendingDynamicLinkData != null) {
                    analytics = FirebaseAnalytics.getInstance(this@HomeActivity)
                    val deepLink = pendingDynamicLinkData.link
                    val country = deepLink?.getQueryParameter("country")
                    val couponId = deepLink?.getQueryParameter("idCoupon")
                    val commerceId = deepLink?.getQueryParameter("commerce")
                    val productId = deepLink?.getQueryParameter("idProduct")
                    val type = deepLink?.getQueryParameter("productType")?.toInt()
                    val promotionalCode = deepLink?.getQueryParameter("couponCodePromotional")
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
                    } else Log.e("Error", "No deep Link")
                    val invite = FirebaseAppInvite.getInvitation(pendingDynamicLinkData)
                    if (invite != null) {
                        val invitationId = invite.invitationId
                        if (!TextUtils.isEmpty(invitationId)) Log.e("Invitation ID", invitationId)
                    }
                }
            }.addOnFailureListener(this) { e: java.lang.Exception? -> Log.w("Dynamic Link", "getDynamicLink:onFailure", e) }

        // GPS Permission
//        if (!runtimePermissions()) enableService()

        // Abel Acosta
        initNavigation()
        // Load Global data
//        loadGlobalData()

        ntfId.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("qrCode", "")
            intent.putExtra("productId", "")
            intent.putExtra("productType", 0)
            startActivity(intent)
        }

        // Get the primary text color of the theme
        val typedValue = TypedValue()
        val theme = this.theme
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
        @SuppressLint("Recycle") val arr = this.obtainStyledAttributes(typedValue.data, intArrayOf(android.R.attr.textColorPrimary))
        val primaryColor = arr.getColor(0, -1)


//        ntfId.setOnClickListener {
//            val intent = Intent(this@HomeActivity, NotificationsActivity::class.java)
//            startActivity(intent)
//        }
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
            }
        } else {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) Toast.makeText(this, getString(R.string.scanner), Toast.LENGTH_SHORT).show()
                else startActivity(ScannerActivity.getIntent(this, result.contents, getString(R.string.campain)))
            }
        }
    }

    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.exit_app))
            builder.setPositiveButton(getString(R.string.yes)) { _: DialogInterface?, _: Int ->
                val intent = Intent(this, CredentialsActivity::class.java)
                startActivity(intent)
                this.finish()
            }
            builder.setNegativeButton(getString(R.string.no)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            val dialog = builder.create()
            dialog.show()
        } else super.onBackPressed()
    }

    private fun showData(){
        val user = Constants.userProfile
        user?.let {
            welcomeId.text = "Hola ${it.names ?: ""},"
            checkProfile.text = "Bienvenido a App Sueldazo"
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) enableService()
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
                navController.navigate(R.id.nav_home)
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
//        val request = BestDiscountRequest(
//            country = Constants.userProfile?.actualCountry ?: "BO",
//            language = Functions.getLanguage(),
//            idUser = Constants.userProfile?.idUser ?: "",
//            sortType = BestDiscountRequest.SORT_TYPE_DISCOUNT,
//            longitude = null,
//            latitude = null
//        )
//        homeViewModel.loadBestDiscounts(request)
    }

    private fun getBanners() {
        val request = BannerRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            filterType = 0
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
