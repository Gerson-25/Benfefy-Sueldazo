package com.syntepro.sueldazo.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.database.DataBaseAdapter
import com.syntepro.sueldazo.entity.app.Pais
import com.syntepro.sueldazo.entity.service.User
import com.syntepro.sueldazo.entity.service.UserTokenRequest
import com.syntepro.sueldazo.room.database.RoomDataBase
import com.syntepro.sueldazo.room.entity.CountryUser
import com.syntepro.sueldazo.service.NetworkService2
import com.syntepro.sueldazo.service.RetrofitClientInstance
import com.syntepro.sueldazo.ui.home.HomeActivity
import com.syntepro.sueldazo.ui.login.model.ObtenerTokenRequest
import com.syntepro.sueldazo.ui.login.model.ValidarClienteRequest
import com.syntepro.sueldazo.ui.login.model.ValidarClienteResponse
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Constants.Companion.userCountryProfile
import com.syntepro.sueldazo.utils.Functions
import com.syntepro.sueldazo.utils.Functions.Companion.showError
import com.syntepro.sueldazo.utils.UserType
import kotlinx.android.synthetic.gms.activity_credentials.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

class CredentialsActivity : AppCompatActivity() {

    private val roomDataBase by lazy { RoomDataBase.getRoomDatabase(this) }
    private var mAuth: FirebaseAuth? = null
    private val adapter by lazy { DataBaseAdapter(this.applicationContext) }
    var documentoId = ""
    var isBenefyClient = false
    val STRING_LENGTH = 10;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credentials)

        // Firebase
        mAuth = FirebaseAuth.getInstance()

        login.setOnClickListener {
            if (documentIsTyped() && termsAcepted()){
                documentoId = CINumber.text.toString()
                validarDocumento {
                    isBenefyClient = it?.isClientBenefy ?: false
                    if (it?.isValidClient == true){
                        obtenerToken(it?.idClient)
                    }
                }
            }
            else {
                showError(this@CredentialsActivity,  getString(R.string.document_and_terms), getString(R.string.incomplete_form_title))
            }
        }

        termsLink.setOnClickListener {
            val intent = Intent(this, ConditionsActivity::class.java)
            startActivity(intent)
        }

        visit.setOnClickListener {
            setAnonymousData()
            Constants.TYPE_OF_USER = UserType.VERIFIED_USER
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("isAnonymousUser", true)
            startActivity(intent)
        }

    }

    private fun setAnonymousData() {
        val userProfile = with(User()){
            idDocument = getRandomString()
            idUser = "C885394C-EB7B-464D-7B59-08DA6432E51A"
            this
        }
        Constants.userProfile = userProfile
        Constants.TYPE_OF_USER = UserType.ANONYMOUSE_USER
    }

    private fun getRandomString(): String = List(16) {
        (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
    }.joinToString("")

    private fun obtenerToken(idClient: Int){
        showLoading(true)
        val request = ObtenerTokenRequest (
            country = "BO",
            language = 1,
            idClient = idClient
        )
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICROSUELDAZO).create(NetworkService2::class.java)
        scopeMainThread.launch {
            try {
                val response = apiService.obtenerToken(request)
                when {
                    response.isSuccessful -> {
                        showLoading(false)
                        val ret = response.body()!!
                        if (ret.isSuccess){
                            ret.data?.token?.let {
                                val intent = Intent(applicationContext, OTPValidationActivity::class.java)
                                intent.putExtra("documentId", documentoId)
                                intent.putExtra("token", it)
                                intent.putExtra("isBenefyClient", isBenefyClient)
                                intent.putExtra("cellPhone", ret.data?.cellPhone)
                                intent.putExtra("idClient", idClient)
                                startActivity(intent)
                            }
                        } else {
                            showError(this@CredentialsActivity, ret.description?: getString(R.string.error_inesperado), "Algo salió mal")
                        }

                    }
                    else -> {
                        showError(this@CredentialsActivity, response.message() ?: getString(R.string.error_inesperado), "Algo salió mal")
                        showLoading(false)
//                        completion(null)
                    }
                }
            } catch (e: Exception) {
                showLoading(false)
//                completion(null)
                showError(this@CredentialsActivity, getString(R.string.error_inesperado), "Algo salió mal")
                Log.e("Exception", e.message ?: e.cause?.message ?: e.cause.toString())
            }
        }
    }

    private fun showLoading(show: Boolean){
        loadingScreen.visibility = if (show) View.VISIBLE else View.GONE
        actionsContainer.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun validarDocumento(completion: (ValidarClienteResponse?) -> Unit){
        showLoading(true)
        val documento = CINumber.text.toString()
        val request = ValidarClienteRequest (
            country = "BO",
            language = 1,
            documento = documento.toLong()
            )
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICROSUELDAZO).create(NetworkService2::class.java)
        scopeMainThread.launch {
            try {
                val response = apiService.validarClientePorDoucmento(request)
                when {
                    response.isSuccessful -> {
                        showLoading(false)
                        val ret = response.body()!!
                        if (ret.isSuccess) completion(ret.data)
                        else {
                            showError(this@CredentialsActivity, ret.description, "Usuario Inválido")
                            completion(null)
                        }
                    }
                    else -> {
                        showError(this@CredentialsActivity, getString(R.string.error_inesperado), "Algo salió mal")
                        showLoading(false)
                        completion(null)
                    }
                }
            } catch (e: Exception) {
                showError(this@CredentialsActivity, getString(R.string.error_inesperado), "Algo salió mal")
                showLoading(false)
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

    /**
     * @author Gerson Aquino 18OCT2022
     *
     * This function create a random ID to register anonymous activity.
     *
     * @return variable with and ID to register anonymous user.
     */
    private fun signAsAnonymous():String{
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val randomId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ThreadLocalRandom.current()
                .ints(STRING_LENGTH.toLong(), 0, charPool.size)
                .asSequence()
                .map(charPool::get)
                .joinToString("")
        } else {
           ""
        }
        return  randomId
    }

    private fun termsAcepted(): Boolean{
        return termCheckBox.isChecked
    }

    private fun documentIsTyped(): Boolean{
        return CINumber.text.isNotEmpty()
    }

}