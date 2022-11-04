package com.syntepro.sueldazo.ui

import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.core.AndroidApplication
import com.syntepro.sueldazo.core.di.ApplicationComponent
import com.syntepro.sueldazo.database.DataBaseAdapter
import com.syntepro.sueldazo.entity.app.Pais
import com.syntepro.sueldazo.entity.service.*
import com.syntepro.sueldazo.room.database.RoomDataBase
import com.syntepro.sueldazo.room.entity.Access
import com.syntepro.sueldazo.room.entity.CountryUser
import com.syntepro.sueldazo.service.NetworkService
import com.syntepro.sueldazo.service.NetworkService2
import com.syntepro.sueldazo.service.NotificationService
import com.syntepro.sueldazo.service.RetrofitClientInstance
import com.syntepro.sueldazo.ui.extras.IntroActivity
import com.syntepro.sueldazo.ui.login.*
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Constants.Companion.userCountryProfile
import com.syntepro.sueldazo.utils.Functions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private val roomDataBase by lazy { RoomDataBase.getRoomDatabase(this) }
    private val adapter by lazy { DataBaseAdapter(this.applicationContext) }
    private var mAuth: FirebaseAuth? = null
    private var versionId: TextView? = null

    private var isLoyalty = false
    private var idPlanType = 0
    private var idPlan = ""

    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as AndroidApplication).appComponent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //version name
        versionId = findViewById(R.id.versionId)

        // DataBase
        adapter.createDatabase()

        // Authentication Firebase Instance
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

//        validateVersions()

        Handler().postDelayed({
            val intent = Intent(this@MainActivity, CredentialsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }, 5000)

        val locale = applicationContext.resources.configuration.locale.country

        val tm = this.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val countryCodeValue = Objects.requireNonNull(tm).networkCountryIso

        Log.i("Country New", "Phone: " + locale + " - SIM CARD: " + countryCodeValue + " - LOCALE: " + Locale.getDefault().country + " - " + Locale.getDefault().displayCountry + " - " + Locale.getDefault().isO3Country)
        Log.e("CellPhone Brand New", Build.BRAND + " " + Build.MODEL)

        val extras = intent.extras
        if (extras != null) {
            isLoyalty = extras.getBoolean("loyaltyPush")
            idPlanType = extras.getInt("planType", 0)
            idPlan = extras.getString("idPlan", "")
        }
    }

    /**
     * Web Service Functions
     */
    /**
     * Gerson Aquino 28JUN2021
     *
     * in line 105 we have taken the current app version
     * and show it in the splash screen
     *
     */
    private fun validateVersions() {
        val pInfo: PackageInfo = this@MainActivity.packageManager.getPackageInfo(packageName, 0)
        val currentVersion: String = pInfo.versionName
        val currentUser = mAuth!!.currentUser


        versionId?.text = "v $currentVersion"

        val request = with(VersionsRequest()) {
            country = "BO"
            language = Functions.getLanguage()
            this
        }
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2).create(NetworkService2::class.java)
        scopeMainThread.launch {
            try {
                val response = apiService.getVersions(request)
                when {
                    response.isSuccessful -> {
                        val ret = response.body()!!
                        if (ret.isSuccess) {
                            ret.data?.let {
//                                val contains = it.any { v -> v.versionNumber == currentVersion }
//                                if (contains) updateUI(currentUser)
//                                else showUpdateDialog()
                                updateUI(currentUser)
                            } ?: run {
                                updateUI(currentUser)
                            }
                        } else {
                            updateUI(currentUser)
                            Log.e("Error", "${ret.code}")
                        }
                    }
                    else -> {
                        updateUI(currentUser)
                        Log.e("Error", "${response.message()} - ${response.errorBody()}")
                    }
                }
            } catch (e: Exception) {
                updateUI(currentUser)
                Log.e("Exception", e.message ?: e.cause?.message ?: e.cause.toString())
            }
        }
    }

    private fun validateVersion() {
        val currentUser = mAuth!!.currentUser
        val request = VersionRequest()

        val call: Call<ResponseBody> = getRetrofit()!!.create(NetworkService::class.java).getVersion(request)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>?, response: Response<ResponseBody?>) {
                if (response.code() == 200) {
                    try {
                        val stringResponse: String = response.body()!!.string()
                        val element: JsonElement = JsonParser().parse(stringResponse)
                        val objectJson: JsonObject = element.asJsonObject
                        val newVersion: String = objectJson.get("versionNumber").asString
                        val pInfo: PackageInfo = this@MainActivity.packageManager.getPackageInfo(packageName, 0)
                        val currentVersion: String = pInfo.versionName
                        if (!newVersion.equals(currentVersion, ignoreCase = true)) {
                            roomDataBase.accessDao().dropCountry()
                            val j = Intent(applicationContext, NotificationService::class.java)
                            stopService(j)
                            val acs: List<Access> = roomDataBase.accessDao().access
                            if (acs.isNotEmpty()) roomDataBase.accessDao().dropAccess()
                            mAuth?.signOut()
                            showUpdateDialog()
                        } else updateUI(currentUser)
                    } catch (e: java.lang.Exception) {
                        updateUI(currentUser)
                    }
                } else updateUI(currentUser)
            }

            override fun onFailure(call: Call<ResponseBody?>?, t: Throwable?) {
                updateUI(currentUser)
            }
        })
    }

    private fun getUser(id: String, completion: (User?) -> Unit) {
        val request = with(GetUserRequest()) {
            country = "BO"
            language = 1
            this
        }
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2).create(NetworkService2::class.java)
        scopeMainThread.launch {
            try {
                val response = apiService.getUserByDocument(request)
                when {
                    response.isSuccessful -> {
                        val ret = response.body()!!
                        if (ret.isSuccess) {
                            completion(ret.data)
                        } else {
                            completion(null)
                            Log.e("Error", "${ret.code}")
                        }
                    }
                    else -> {
                        completion(null)
                        Log.e("Error", "${response.message()} - ${response.errorBody()}")
                    }
                }
            } catch (e: Exception) {
                completion(null)
                Log.e("Exception", e.message ?: e.cause?.message ?: e.cause.toString())
            }
        }
    }

    /**
     * App Functions
     */
    private fun updateUI(user: FirebaseUser?) {
        val splashDuration = 2000
        user?.let {
            Handler().postDelayed({
                getUser(it.uid) { dbUser ->
                    dbUser?.let {
                        adapter.open()
                        val userCountry: Pais = adapter.getCountryInfoAbr(it.actualCountry ?: "BO")
                        adapter.close()
                        val country = userCountry.nombre
                        val cod = userCountry.codigoArea
                        val abr = userCountry.abreviacion
                        val coin = userCountry.moneda
                        loadData(country, cod, abr, coin)

                        it.countryName = country
                        it.currency = coin
                        it.areaCode = cod
                        Constants.userProfile = it
                        Functions.savePersistentProfile(this@MainActivity)

                        UserData.firebaseToken(it.idUser ?: "")
                        UserData.getToken { success ->
                            if (success) {
                                if (dbUser.flagTyc) {
                                    val intent = Intent(this@MainActivity, IntroActivity::class.java)
                                    if (idPlanType != 0 && idPlan.isNotEmpty()) {
                                        intent.putExtra("loyaltyPush", isLoyalty)
                                        intent.putExtra("planType", idPlanType)
                                        intent.putExtra("idPlan", idPlan)
                                    }
                                    startActivity(intent)
                                    finish()
                                } else {
                                    val intent = Intent(this@MainActivity, ConditionsActivity::class.java)
                                    intent.putExtra("provenance", 0)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                LoginManager.getInstance().logOut()
                                FirebaseAuth.getInstance().signOut()
                                val intent = Intent(this@MainActivity, CredentialsActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                            }
                        }
                    } ?: run {
                        LoginManager.getInstance().logOut()
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this@MainActivity, OTPValidationActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }
                }
            }, splashDuration.toLong())
        } ?: run {
            Handler().postDelayed({
                val intent = Intent(this@MainActivity, OTPValidationActivity::class.java)
                startActivity(intent)
                finish()
            }, splashDuration.toLong())
        }
    }

    private fun loadData(pais: String, cod: String, abr: String, moneda: String) {
        val country = roomDataBase.accessDao().country
        if (country == null) {
            val ids = TimeZone.getAvailableIDs()
            val tz = TimeZone.getTimeZone(ids[144])
            val c = Calendar.getInstance(tz)
            val countryUser = CountryUser()
            countryUser.id = 1
            countryUser.fechaActualizacion = c.time
            countryUser.pais = pais
            countryUser.codArea = cod
            countryUser.abr = abr
            countryUser.moneda = moneda
            adapter.open()
            val selectedCountry = adapter.getCountryConfiguration(pais)
            countryUser.timeZone = selectedCountry.timeZone
            adapter.close()
            roomDataBase.accessDao().addCountryUser(countryUser)
            userCountryProfile = countryUser
        } else if (country.timeZone == null || country.timeZone == "") {
            roomDataBase.accessDao().dropCountry()
            val ids = TimeZone.getAvailableIDs()
            val tz = TimeZone.getTimeZone(ids[144])
            val c = Calendar.getInstance(tz)
            val countryUser = CountryUser()
            countryUser.id = 1
            countryUser.fechaActualizacion = c.time
            countryUser.pais = pais
            countryUser.codArea = cod
            countryUser.abr = abr
            countryUser.moneda = moneda
            adapter.open()
            val selectedCountry = adapter.getCountryConfiguration(pais)
            countryUser.timeZone = selectedCountry.timeZone
            adapter.close()
            roomDataBase.accessDao().addCountryUser(countryUser)
            userCountryProfile = countryUser
        } else {
            userCountryProfile = country
        }
    }

    private fun showUpdateDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Una nueva version esta disponible!")
        builder.setPositiveButton("Actualizar") { dialog, _ ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.syntepro.appbeneficiosbolivia")))
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.cancel)) { _, _ -> exitProcess(0) }
        builder.setCancelable(false)
        builder.show()
    }

    private fun getRetrofit(): Retrofit? {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
        return Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_MICRO) // PROD
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
    }

}