package com.syntepro.appbeneficiosbolivia.ui.login

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
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
import com.syntepro.appbeneficiosbolivia.ui.general.InformationDialog
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Constants.Companion.userCountryProfile
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.showWarning
import kotlinx.android.synthetic.gms.activity_credentials.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class CredentialsActivity : AppCompatActivity() {

    private val roomDataBase by lazy { RoomDataBase.getRoomDatabase(this) }
    private var mAuth: FirebaseAuth? = null
    private val adapter by lazy { DataBaseAdapter(this.applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credentials)

        // Firebase
        mAuth = FirebaseAuth.getInstance()

        login.setOnClickListener {
            if (documentIsTyped() && termsAcepted()){

            }
            else {
                val intent = Intent(this, InformationDialog::class.java)
                intent.putExtra("title", "Formulariio incompleto")
                intent.putExtra("info", "Asegurate de ingresar tu número de Cédula y aceptar los términos y condiciones")
                startActivity(intent)
            }
        }

    }

    /**
     * Firebase Functions
     */

    private fun loginUser(email: String, password: String) {
        mAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(this@CredentialsActivity) { task ->
                if (task.isSuccessful) {
                    mAuth?.currentUser?.reload()
                    val user: FirebaseUser = mAuth?.currentUser!!
                    getUser(user.uid) { dbUser ->
                        dbUser?.let {
                            adapter.open()
                            val userCountry: Pais = adapter.getCountryInfoAbr(it.actualCountry ?: "BO")
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

                            UserData.firebaseToken(it.idUser ?: "")
                            UserData.getToken { success ->
                                if (success) {
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
                                } else {
                                    showWarning(this, getString(R.string.invalid_credentials))
                                    login.visibility = View.VISIBLE
                                    progress_circular.visibility = View.GONE
                                }
                            }
                        } ?: run {
                            Log.e("Error", "Usuario no existe en BD.")
                            validateUser(user.uid)
                        }
                    }
                } else {
                    showWarning(this, getString(R.string.invalid_credentials))
                    login.visibility = View.VISIBLE
                    progress_circular.visibility = View.GONE
                }
            }
    }

    private fun validateUser(id: String) {
        val ref: DocumentReference = FirebaseFirestore.getInstance().collection("Usuarios").document(id)
        ref.get().addOnCompleteListener { taskLogin ->
            if (taskLogin.isSuccessful) {
                val documentSnapshot = taskLogin.result
                if (documentSnapshot.exists()) {
                    val readUser = documentSnapshot.toObject(Usuario::class.java)
                    readUser?.let { usr ->
                        UserData.saveFirebaseUser(this@CredentialsActivity, usr, adapter, id, Constants.PLAY_STORE) { message: String, result: Boolean ->
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
                                FirebaseAuth.getInstance().signOut()
                                showWarning(this, message)
                                login.visibility = View.VISIBLE
                                progress_circular.visibility = View.GONE
                            }
                        }
                    }
                } else {
                    LoginManager.getInstance().logOut()
                    FirebaseAuth.getInstance().signOut()
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

    private fun subscribeTopic(country: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(country)
            .addOnCompleteListener { task ->
                var msg = "Successfully subscribed"
                if (!task.isSuccessful) msg = "Error subscribed"
                Log.d("TOPIC", msg)
            }
    }

    private fun termsAcepted(): Boolean{
        return termCheckBox.isChecked
    }

    private fun documentIsTyped(): Boolean{
        return CINumber.text.isNotEmpty()
    }

}