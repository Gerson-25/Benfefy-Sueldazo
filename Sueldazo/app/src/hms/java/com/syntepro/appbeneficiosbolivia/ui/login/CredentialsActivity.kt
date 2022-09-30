package com.syntepro.appbeneficiosbolivia.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.anotherdev.firebase.auth.FirebaseAuth
import com.anotherdev.firebase.auth.FirebaseAuthRest
import com.anotherdev.firebase.auth.FirebaseUser
import com.facebook.login.LoginManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.huawei.hms.push.HmsMessaging
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.database.DataBaseAdapter
import com.syntepro.appbeneficiosbolivia.entity.app.Pais
import com.syntepro.appbeneficiosbolivia.entity.firebase.Usuario
import com.syntepro.appbeneficiosbolivia.entity.service.GetUserRequest
import com.syntepro.appbeneficiosbolivia.entity.service.User
import com.syntepro.appbeneficiosbolivia.room.database.RoomDataBase
import com.syntepro.appbeneficiosbolivia.room.entity.CountryUser
import com.syntepro.appbeneficiosbolivia.service.NetworkService2
import com.syntepro.appbeneficiosbolivia.service.RetrofitClientInstance
import com.syntepro.appbeneficiosbolivia.ui.extras.IntroActivity
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Constants.Companion.userCountryProfile
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.showWarning
import kotlinx.android.synthetic.hms.activity_credentials.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class CredentialsActivity : AppCompatActivity() {

    private val roomDataBase by lazy { RoomDataBase.getRoomDatabase(this) }
    private val adapter by lazy { DataBaseAdapter(this.applicationContext) }
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credentials)

        // Firebase
        val app = FirebaseApp.getInstance()
        mAuth = FirebaseAuthRest.getInstance(app)

        resetPassword.setOnClickListener {
            val intent = Intent(this@CredentialsActivity, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        login.setOnClickListener {
            if (validate()) {
                login.visibility = View.INVISIBLE
                progress_circular.visibility = View.VISIBLE
                loginUser(userField.text.toString(), passwordField.text.toString())
            }
        }
    }

    /**
     * Firebase Functions
     */
    private fun loginUser(email: String, password: String) {
        try {
            mAuth?.signInWithEmailAndPassword(email, password)
                    ?.subscribe(
                            { complete ->
                                Log.e("Complete", "Ok -> ${complete.idToken}")
                                mAuth?.currentUser?.reload()
                                val user: FirebaseUser = mAuth?.currentUser!!
                                getUser(user.uid!!) { dbUser ->
                                    dbUser?.let {
                                        adapter.open()
                                        val userCountry: Pais = adapter.getCountryInfoAbr(it.actualCountry
                                                ?: "BO")
                                        adapter.close()
                                        val country = userCountry.nombre
                                        val cod = userCountry.codigoArea
                                        val abr = userCountry.abreviacion
                                        val coin = userCountry.moneda
                                        loadData(country, cod, abr, coin)
                                        subscribeTopic(abr)

                                        it.countryName = country
                                        it.currency = coin
                                        it.areaCode = cod
                                        Constants.userProfile = it
                                        Functions.savePersistentProfile(this@CredentialsActivity)

                                        UserData.getToken { success ->
                                            if (success) {
                                                if (Constants.HUAWEI_TOKEN.isNotEmpty()) {
                                                    Functions.updateDeviceToken(
                                                            it.idUserFirebase ?: "",
                                                            it.idUser ?: "",
                                                            Constants.HUAWEI_TOKEN,
                                                            Constants.APP_GALLERY
                                                    )
                                                }

                                                if (dbUser.flagTyc) {
                                                    val intent = Intent(this@CredentialsActivity, IntroActivity::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                } else {
                                                    val intent = Intent(this@CredentialsActivity, ConditionsActivity::class.java)
                                                    intent.putExtra("provenance", 0)
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            } else
                                                this@CredentialsActivity.runOnUiThread { showError() }
                                        }
                                    } ?: run {
                                        Log.e("Error", "Usuario no existe en BD.")
                                        validateUser(user.uid!!)
                                    }
                                }
                            },
                            { throwable ->
                                this@CredentialsActivity.runOnUiThread { showError() }
                                Log.e("Error", "Ocurrió un error, intentelo de nuevo. ${throwable.localizedMessage}")
                            }
                    )
        } catch (e: Exception) {
            showWarning(this, getString(R.string.error_connection))
            login.visibility = View.VISIBLE
            progress_circular.visibility = View.GONE
        }
    }

    private fun validateUser(id: String) {
        FirebaseFirestore.getInstance().collection("Usuarios").document(id)
                .get()
                .addOnCompleteListener { taskLogin ->
                    if (taskLogin.isSuccessful) {
                        val documentSnapshot = taskLogin.result
                        if (documentSnapshot.exists()) {
                            val readUser = documentSnapshot.toObject(Usuario::class.java)
                            readUser?.let { usr ->
                                UserData.saveFirebaseUser(this@CredentialsActivity, usr, adapter, id, Constants.APP_GALLERY) { message: String, result: Boolean ->
                                    if (result) {
                                        val country = usr.pais
                                        val cod = usr.codigoArea
                                        val abr = usr.abreviacion
                                        val coin = usr.moneda
                                        loadData(country, cod, abr, coin)
                                        if (usr.tyc != null && usr.tyc == "1") {
                                            val intent = Intent(this@CredentialsActivity, IntroActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        } else if (usr.tyc != null && usr.tyc == "0") {
                                            val intent = Intent(this@CredentialsActivity, ConditionsActivity::class.java)
                                            intent.putExtra("provenance", 0)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            val intentLog = Intent(this@CredentialsActivity, IntroActivity::class.java)
                                            startActivity(intentLog)
                                            finish()
                                        }
                                    } else {
                                        LoginManager.getInstance().logOut()
                                        mAuth?.signOut()
                                        showWarning(this, message)
                                        login.visibility = View.VISIBLE
                                        progress_circular.visibility = View.GONE
                                    }
                                }
                            }
                        } else {
                            LoginManager.getInstance().logOut()
                            mAuth?.signOut()
                            val intent = Intent(this@CredentialsActivity, WelcomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                        }
                    } else {
                        showWarning(this, getString(R.string.invalid_credentials))
                        login.visibility = View.VISIBLE
                        progress_circular.visibility = View.GONE
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
            idUserFirebase = id
            this
        }
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2).create(NetworkService2::class.java)
        scopeMainThread.launch {
            try {
                val response = apiService.getUser(request)
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
    private fun validate(): Boolean {
        val email: String = userField.text.toString().trim()
        val passwordInput: String = passwordField.text.toString().trim()
        return if (email.isEmpty() || passwordInput.isEmpty()) {
            showWarning(this, getString(R.string.c_required))
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showWarning(this, getString(R.string.e_invalid))
            false
        } else if (passwordInput.length < 6) {
            showWarning(this, getString(R.string.min_characters))
            false
        } else true
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
        } else
            userCountryProfile = country
    }

    private fun showError() {
        showWarning(this@CredentialsActivity, getString(R.string.error_connection))
        login.visibility = View.VISIBLE
        progress_circular.visibility = View.GONE
    }

    private fun subscribeTopic(country: String) {
        try {
            HmsMessaging.getInstance(this@CredentialsActivity)
                    .subscribe(country)
                    .addOnCompleteListener { task ->
                        // Obtain the topic subscription result.
                        if (task.isSuccessful) Log.i("TOPIC", "subscribe topic successfully")
                        else Log.e("TOPIC", "subscribe topic failed, the return value is " + task.exception.message)
                    }
        } catch (e: Exception) {
            Log.e("TOPIC", "subscribe failed, catch exception : $e")
        }
    }

}