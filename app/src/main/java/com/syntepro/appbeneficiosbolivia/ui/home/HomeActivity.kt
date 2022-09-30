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
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
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
import com.squareup.picasso.Picasso
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.core.AndroidApplication
import com.syntepro.appbeneficiosbolivia.core.app.ErrorMessage
import com.syntepro.appbeneficiosbolivia.core.di.ApplicationComponent
import com.syntepro.appbeneficiosbolivia.database.DataBaseAdapter
import com.syntepro.appbeneficiosbolivia.entity.app.Pais
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
import com.syntepro.appbeneficiosbolivia.ui.home.adapter.CustomAdapter
import com.syntepro.appbeneficiosbolivia.ui.home.model.*
import com.syntepro.appbeneficiosbolivia.ui.home.viewModel.HomeViewModel
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.LoyaltyPlanListRequest
import com.syntepro.appbeneficiosbolivia.ui.login.ConditionsActivity
import com.syntepro.appbeneficiosbolivia.ui.login.LogoutService
import com.syntepro.appbeneficiosbolivia.ui.login.WelcomeActivity
import com.syntepro.appbeneficiosbolivia.ui.menu.*
import com.syntepro.appbeneficiosbolivia.ui.notifications.model.NotificationCountRequest
import com.syntepro.appbeneficiosbolivia.ui.notifications.ui.activities.NotificationsActivity
import com.syntepro.appbeneficiosbolivia.ui.shop.model.ArticleRequest
import com.syntepro.appbeneficiosbolivia.ui.shop.model.GiftCardRequest
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities.ShopDetailActivity
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities.SuccessGiftUserActivity
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.showUserQR
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.userCountry
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.userTimeZone
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.app_bar_main.cardImage
import kotlinx.android.synthetic.main.app_bar_main.ntfId
import kotlinx.android.synthetic.main.app_bar_main.scanId
import kotlinx.android.synthetic.main.app_bar_main.userImageId
import kotlinx.android.synthetic.main.app_bar_main.welcomeId
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_benefy.*
import kotlinx.android.synthetic.main.navigation_drawer.*
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
    private lateinit var nameText: TextView
    private lateinit var emailText: TextView
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
                                val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
                                if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)

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

        // Navigation Drawer Assignments3
        val hView = navigation_view.getHeaderView(0)
        val userQR = hView.findViewById<ImageButton>(R.id.qrUser)
        val profileNav = hView.findViewById<LinearLayout>(R.id.navProfileId)
        val conditionsNav = hView.findViewById<LinearLayout>(R.id.navConditionsId)
        val commentsNav = hView.findViewById<LinearLayout>(R.id.navCommentsId)
        val helpNav = hView.findViewById<LinearLayout>(R.id.navHelpId)
        val aboutNav = hView.findViewById<LinearLayout>(R.id.navAboutId)
        val configurationNav = hView.findViewById<LinearLayout>(R.id.navConfigurationId)
        val logoutNav = hView.findViewById<LinearLayout>(R.id.navLogoutId)
        profileImage = hView.findViewById(R.id.circleImageView)
        nameText = hView.findViewById(R.id.txt_nombreHeader)
        emailText = hView.findViewById(R.id.txt_correoHeader)

        // Abel Acosta
        initNavigation()
        // Load Global data
        loadGlobalData()

        changeCountry = hView.findViewById(R.id.navCountryId)

        // Get the primary text color of the theme
        val typedValue = TypedValue()
        val theme = this.theme
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
        @SuppressLint("Recycle") val arr = this.obtainStyledAttributes(typedValue.data, intArrayOf(android.R.attr.textColorPrimary))
        val primaryColor = arr.getColor(0, -1)

        nameText.setTextColor(primaryColor)
        emailText.setTextColor(primaryColor)

        userQR.setOnClickListener { showUserQR(this@HomeActivity) }

        profileNav.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        conditionsNav.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            val intent = Intent(this@HomeActivity, ConditionsActivity::class.java)
            intent.putExtra("provenance", 1)
            startActivity(intent)
        }

        commentsNav.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            val intent = Intent(this@HomeActivity, CommentsActivity::class.java)
            startActivity(intent)
        }

        helpNav.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            val intent = Intent(this@HomeActivity, HelpActivity::class.java)
            startActivity(intent)
        }

        aboutNav.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            val intent = Intent(this@HomeActivity, AboutActivity::class.java)
            startActivity(intent)
        }

        configurationNav.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            val intent = Intent(this@HomeActivity, ConfigurationActivity::class.java)
            startActivity(intent)
        }

        logoutNav.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            roomDataBase.accessDao().dropCountry()
            val serviceIntent = Intent(this@HomeActivity, NotificationService::class.java)
            stopService(serviceIntent)
            val access = roomDataBase.accessDao().access
            if (!access.isNullOrEmpty()) roomDataBase.accessDao().dropAccess()
            updateUI()
        }

        benefits.setOnClickListener {
            // bottom_navigation_view.menu.findItem(R.id.navigationEmpty).isChecked = true
            navController.navigate(R.id.nav_benfy)
        }

        // Get Countries
        adapter.open()
        val country: List<Pais>? = adapter.infoCountry as? List<Pais>

        country?.let {
            for (i in country.indices) {
                nomCountry!!.add(country[i].nombre)
            }
        }

        val customAdapter = CustomAdapter(this, flags, nomCountry)
        changeCountry!!.adapter = customAdapter

        adapter.close()

        try {
            onNewIntent(intent)
            val cu = roomDataBase.accessDao().country
            when (cu.abr) {
                "SV" -> changeCountry!!.setSelection(0)
                "BO" -> changeCountry!!.setSelection(1)
                "GT" -> changeCountry!!.setSelection(2)
                "HND" -> changeCountry!!.setSelection(3)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        changeCountry!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                make_sort = true
                val country = roomDataBase.accessDao().country
                try {
                    if (nomCountry!![i] != country.pais) {
                        val builder = AlertDialog.Builder(this@HomeActivity)
                        builder.setMessage(getString(R.string.country_changed) + " " + nomCountry!![i] + "?")
                        builder.setPositiveButton(getString(R.string.yes)) { dialog: DialogInterface, _: Int ->
                            try {
                                val ids = TimeZone.getAvailableIDs()
                                val tz = TimeZone.getTimeZone(ids[144])
                                val c = Calendar.getInstance(tz)
                                roomDataBase.accessDao().dropCountry()
                                val newCountryUser = CountryUser()
                                newCountryUser.id = 1
                                newCountryUser.fechaActualizacion = c.time
                                adapter.open()
                                val selectedCountry = adapter.getCountryConfiguration(nomCountry!![i])
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
                                changeCountry!!.setSelection(i)
                                dialog.cancel()
                                val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
                                if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)

                                loadGlobalData()
                            } catch (e: Exception) {
                                when (country.abr) {
                                    "SV" -> changeCountry!!.setSelection(0)
                                    "BO" -> changeCountry!!.setSelection(1)
                                    "GT" -> changeCountry!!.setSelection(2)
                                    "HND" -> changeCountry!!.setSelection(3)
                                }
                                dialog.cancel()
                            }
                        }
                        builder.setNegativeButton(getString(R.string.no)) { dialog: DialogInterface, _: Int ->
                            when (country.abr) {
                                "SV" -> changeCountry!!.setSelection(0)
                                "BO" -> changeCountry!!.setSelection(1)
                                "GT" -> changeCountry!!.setSelection(2)
                                "HND" -> changeCountry!!.setSelection(3)
                            }
                            dialog.cancel()
                        }
                        val dialog = builder.create()
                        dialog.setCancelable(false)
                        dialog.show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) { }
        }

        cardImage.setOnClickListener { drawer_layout.openDrawer(GravityCompat.START) }

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
        readUserInfo()
    }

    override fun onBackPressed() {
        // Abel Acosta
        // Call OnBackPressed on fragments
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
        else {
            if (!navController.popBackStack()) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.exit_app))
                builder.setPositiveButton(getString(R.string.yes)) { _: DialogInterface?, _: Int -> this@HomeActivity.finish() }
                builder.setNegativeButton(getString(R.string.no)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
                val dialog = builder.create()
                dialog.show()
            } else super.onBackPressed()
        }
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

    /**
     * App Functions
     */
    private fun readUserInfo() {
        Constants.userProfile?.let {
            Functions.showRoundedImage(it.photoUrl, userImageId)
            Picasso.get()
                .load(it.photoUrl)
                .error(R.drawable.notfound)
                .placeholder(R.drawable.cargando)
                .into(profileImage)
            val fn = it.names?.substringBefore(" ")
            welcomeId.text = "Hola, $fn"
            nameText.text = "${it.names} ${it.lastNames ?: ""}"
//            nameText.text = "${it.names} ${it.lastNames ?: ""}"
            emailText.text = it.email
        }
        navigation_view.bringToFront()
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
        navView = findViewById(R.id.navigation_view)
        navController = findNavController(R.id.nav_host_fragment)

        /*
        navView.menu.findItem(androidx.navigation.ui.R.id.logout).setOnMenuItemClickListener {
            drawerLayoutId.closeDrawers()
            logOut()
            true
        }
         */

        /*navView.menu.findItem(R.id.nav_test).setOnMenuItemClickListener {
            drawer_layout.closeDrawers()
            navController.navigate(R.id.nav_test)
            true
        }

         */

//        // Manage Navigation Rodrigo Osegueda
//        navController.addOnDestinationChangedListener { controller, destination, arguments ->
//            if (destination.id == R.id.nav_profile)
//                bottom_navigation_view.visibility = View.GONE
//            else
//                bottom_navigation_view.visibility = View.VISIBLE
//        }
        // openAction<MainActivity>(1)
        navView.itemIconTintList = null
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // Las opciones que van aqui aparecen con icon burguer, el resto con flecha back
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            ),
            drawer_layout
        )

        NavigationUI.setupWithNavController(bottom_navigation_view, navController)
        // setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

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

    fun getLoyaltyPlans() {
        val request = LoyaltyPlanListRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            recordsNumber = Constants.LIST_PAGE_SIZE,
            pageNumber = 1,
            idUser = Constants.userProfile?.idUser ?: ""
        )
        homeViewModel.loyaltyPlans(request)
    }

    private fun getCategories() {
        val request = CategoryRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            filterType = 1
        )
        homeViewModel.loadCategories(request)
    }

    private fun getItems() {
        val request = with(ArticleRequest()) {
            country = Constants.userProfile?.actualCountry ?: "BO"
            idUser = Constants.userProfile?.idUser ?: ""
            language = 1
            recordsNumber = 10
            sortType = 1
            idCity = Constants.stateFiltered
            this
        }
        homeViewModel.loadItems(request)
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

    private fun getGiftCards() {
        val request = with(GiftCardRequest()) {
            country = Constants.userProfile?.actualCountry ?: "BO"
            language = 1
            recordsNumber = 10
            pageNumber = 1
            sortType = 1
            idCity = Constants.stateFiltered
            idCategory = Constants.categoryFiltered
            this
        }
        homeViewModel.loadGiftCards(request)
    }

    private fun getFeaturedGiftCards() {
        val request = FeaturedGiftCardRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = 1,
            recordsNumber = Constants.LIST_PAGE_SIZE,
            pageNumber = 1,
            sortType = 1,
            idCity = Constants.stateFiltered,
            idCategory = Constants.categoryFiltered
        )
        homeViewModel.loadFeaturedGiftCards(request)
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
        getLoyaltyPlans()
        getCategories()
        getItems()
        getParams()
        getBestDiscounts()
        getBanners()
        getPurchaseProducts()
        getStates()
        // getGiftCards()
        getFeaturedGiftCards()
        getFeaturedCoupons()
        getUserSavings()
        if (Constants.TOKEN.isNotEmpty()) getCounter()
    }

    fun openDrawer() { drawer_layout.openDrawer(GravityCompat.START) }

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
