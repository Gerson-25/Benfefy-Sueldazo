package com.syntepro.sueldazo.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.database.DataBaseAdapter
import com.syntepro.sueldazo.entity.app.Pais
import com.syntepro.sueldazo.entity.app.SocialUser
import com.syntepro.sueldazo.entity.firebase.Usuario
import com.syntepro.sueldazo.entity.service.GetUserRequest
import com.syntepro.sueldazo.entity.service.User
import com.syntepro.sueldazo.room.database.RoomDataBase
import com.syntepro.sueldazo.room.entity.CountryUser
import com.syntepro.sueldazo.service.NetworkService2
import com.syntepro.sueldazo.service.RetrofitClientInstance
import com.syntepro.sueldazo.ui.extras.IntroActivity
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Constants.Companion.userCountryProfile
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.gms.activity_welcome.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class WelcomeActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private lateinit var callbackManager: CallbackManager
    private val roomDataBase by lazy { RoomDataBase.getRoomDatabase(this) }
    private val adapter by lazy { DataBaseAdapter(this.applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Firebase
        mAuth = FirebaseAuth.getInstance()

        // Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestScopes(Scope(Scopes.PROFILE))
                .requestScopes(Scope(Scopes.PLUS_ME))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this@WelcomeActivity, gso)

        val sourceString = "<b>" + getString(R.string.start_saving) + "</b> " + getString(R.string.favorite_commerces)
        titleId.text = Html.fromHtml(sourceString)

        createAccount.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        login.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, CredentialsActivity::class.java)
            startActivity(intent)
        }

    }

    private fun validateProfile(user: FirebaseUser?, data: SocialUser?) {
        user?.let {
            val ref =  FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION).document(it.uid)
            ref.get()
                    .addOnSuccessListener { fbUser ->
                        if (fbUser.exists()) {
                            val firebaseUser = fbUser.toObject(Usuario::class.java)
                            firebaseUser?.let { usr ->
                                UserData.saveFirebaseUser(this@WelcomeActivity, usr, adapter, it.uid, Constants.PLAY_STORE) { message: String, result: Boolean ->
                                    if (result) {
//                                        loadData(usr.pais, usr.codigoArea, usr.abreviacion, usr.moneda)
                                        val intentLog = Intent(this@WelcomeActivity, IntroActivity::class.java)
                                        startActivity(intentLog)
                                        finish()
                                    } else {
                                        LoginManager.getInstance().logOut()
                                        FirebaseAuth.getInstance().signOut()
                                        Functions.showWarning(this, message)
                                    }
                                }
                            }
                        } else { updateUI(mAuth?.currentUser, data) }
                    }
        }
    }

    /**
     * Web Service Functions
     */
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
                        if (ret.isSuccess) completion(ret.data)
                        else completion(null)
                    }
                    else -> { completion(null) }
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
            val selectedCountry: Pais = adapter.getCountryConfiguration(pais)
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
            val selectedCountry: Pais = adapter.getCountryConfiguration(pais)
            countryUser.timeZone = selectedCountry.timeZone
            adapter.close()
            roomDataBase.accessDao().addCountryUser(countryUser)
            userCountryProfile = countryUser
        } else userCountryProfile = country
    }

    private fun updateUI(user: FirebaseUser?, data: SocialUser?) {
        user?.let {
            val intent = Intent(this@WelcomeActivity, CitySelectionActivity::class.java)
            intent.putExtra("userId", it.uid)
            intent.putExtra("model", data)
            intent.putExtra("registerStore", Constants.PLAY_STORE)
            startActivity(intent)
            this.finish()
        }
    }

    private fun subscribeTopic(country: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(country)
                .addOnCompleteListener { task ->
                    var msg = "Successfully subscribed"
                    if (!task.isSuccessful) msg = "Error subscribed"
                    Log.d("TOPIC", msg)
                }
    }

}