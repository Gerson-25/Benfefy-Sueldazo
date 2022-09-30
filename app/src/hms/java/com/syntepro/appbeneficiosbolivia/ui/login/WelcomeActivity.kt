package com.syntepro.appbeneficiosbolivia.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anotherdev.firebase.auth.FirebaseAuth
import com.anotherdev.firebase.auth.FirebaseAuthRest
import com.anotherdev.firebase.auth.FirebaseUser
import com.anotherdev.firebase.auth.provider.FacebookAuthProvider
import com.anotherdev.firebase.auth.provider.GoogleAuthProvider
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.huawei.hms.push.HmsMessaging
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.database.DataBaseAdapter
import com.syntepro.appbeneficiosbolivia.entity.app.Pais
import com.syntepro.appbeneficiosbolivia.entity.app.SocialUser
import com.syntepro.appbeneficiosbolivia.entity.firebase.Usuario
import com.syntepro.appbeneficiosbolivia.entity.service.GetUserRequest
import com.syntepro.appbeneficiosbolivia.entity.service.User
import com.syntepro.appbeneficiosbolivia.room.database.RoomDataBase
import com.syntepro.appbeneficiosbolivia.room.entity.CountryUser
import com.syntepro.appbeneficiosbolivia.service.NetworkService2
import com.syntepro.appbeneficiosbolivia.service.RetrofitClientInstance
import com.syntepro.appbeneficiosbolivia.ui.extras.IntroActivity
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Constants.Companion.RC_SIGN_IN
import com.syntepro.appbeneficiosbolivia.utils.Constants.Companion.userCountryProfile
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.hms.activity_welcome.*
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
        val app = FirebaseApp.getInstance()
        mAuth = FirebaseAuthRest.getInstance(app)

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

        google.setOnClickListener {
            val signInIntent = googleSignInClient?.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        facebook.setOnClickListener {
            buttonFacebookLogin.callOnClick()
            facebookLogin()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            task.addOnSuccessListener { firebaseAuthWithGoogle(it) }
            task.addOnFailureListener { Log.e("Error", "Exception - ${it.localizedMessage}") }
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                val account = task.getResult(ApiException::class.java)
//                firebaseAuthWithGoogle(account)
//            } catch (e: ApiException) { e.printStackTrace() }
        } else
            callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Firebase Functions
     */
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        account?.let {
            val socialData = SocialUser()
            socialData.name = account.displayName
            socialData.email = account.email
            socialData.image = account.photoUrl?.toString() + "?height=500"

            val credential = GoogleAuthProvider.getCredential(it.idToken)
            mAuth?.signInWithCredential(credential)
                ?.subscribe(
                    {
                        mAuth!!.currentUser!!.reload()
                        getUser(mAuth?.currentUser!!.uid!!) { dbUser ->
                            dbUser?.let { u ->
                                adapter.open()
                                val userCountry: Pais = adapter.getCountryInfoAbr(u.actualCountry ?: "BO")
                                adapter.close()
                                val country = userCountry.nombre
                                val cod = userCountry.codigoArea
                                val abr = userCountry.abreviacion
                                val coin = userCountry.moneda
                                loadData(country, cod, abr, coin)

                                u.countryName = country
                                u.currency = coin
                                u.areaCode = cod
                                Constants.userProfile = u
                                Functions.savePersistentProfile(this@WelcomeActivity)
                                subscribeTopic(abr)

                                UserData.getToken { success ->
                                    if (success) {
                                        if (Constants.HUAWEI_TOKEN.isNotEmpty()) {
                                            Functions.updateDeviceToken(
                                                u.idUserFirebase ?: "",
                                                u.idUser ?: "",
                                                Constants.HUAWEI_TOKEN,
                                                Constants.APP_GALLERY
                                            )
                                        }

                                        if (u.flagTyc) {
                                            val intent = Intent(this@WelcomeActivity, IntroActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            val intent = Intent(this@WelcomeActivity, ConditionsActivity::class.java)
                                            intent.putExtra("provenance", 0)
                                            startActivity(intent)
                                            finish()
                                        }
                                    } else
                                        this@WelcomeActivity.runOnUiThread {
                                            Toast.makeText(this@WelcomeActivity, "Fallo la autenticaciñon.",
                                                Toast.LENGTH_LONG).show()
                                        }
                                }
                            } ?: run {
                                Log.e("Error", "Usuario no existe en BD")
                                validateProfile(mAuth?.currentUser?.uid, socialData)
                            }
                        }
                    },
                    { throwable ->
                        // If sign in fails, display a message to the user.
                        Log.e("Google HMS", "signInWithEmail:failure -> ${throwable.localizedMessage}")
                        Toast.makeText(this@WelcomeActivity, "Credenciales Invalidas",
                            Toast.LENGTH_SHORT).show()
                    }
                )

//            mAuth?.signInWithCredential(credential)?.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    mAuth!!.currentUser!!.reload()
//                    getUser(mAuth?.currentUser!!.uid) { dbUser ->
//                        dbUser?.let { u ->
//                            adapter.open()
//                            val userCountry: Pais = adapter.getCountryInfoAbr(u.actualCountry ?: "BO")
//                            adapter.close()
//                            val country = userCountry.nombre
//                            val cod = userCountry.codigoArea
//                            val abr = userCountry.abreviacion
//                            val coin = userCountry.moneda
//                            loadData(country, cod, abr, coin)
//
//                            u.countryName = country
//                            u.currency = coin
//                            u.areaCode = cod
//                            Constants.userProfile = u
//                            Functions.savePersistentProfile(this@WelcomeActivity)
//
//                            UserData.getToken()
//                            UserData.firebaseToken(u.idUser ?: "")
//
//                            if (u.flagTyc) {
//                                val intent = Intent(this@WelcomeActivity, IntroActivity::class.java)
//                                startActivity(intent)
//                                finish()
//                            } else {
//                                val intent = Intent(this@WelcomeActivity, CredentialsActivity::class.java)
//                                intent.putExtra("provenance", 0)
//                                startActivity(intent)
//                                finish()
//                            }
//                        } ?: run {
//                            Log.e("Error", "Usuario no existe en BD")
//                            validateProfile(mAuth?.currentUser, socialData)
//                        }
//                    }
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w("TAG", "signInWithEmail:failure", task.exception)
//                    Toast.makeText(this@WelcomeActivity, "Credenciales Invalidas",
//                            Toast.LENGTH_SHORT).show()
//                }
//            }
        }
    }

    /**
     * Gerson Aquino 16OCT2021
     *
     * Facebook login dependencies was updated
     */
    private fun facebookLogin() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().logInWithReadPermissions(this@WelcomeActivity, mutableListOf("public_profile", "email", "user_birthday", "user_friends"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.e("Facebook_Login", "Success")
                val socialData = SocialUser()
                val request = GraphRequest.newMeRequest(result.accessToken) { facebookObject, _ ->
                    try {
                        // Here is the data that you want
                        Log.e("FB_LOGIN_JSON_RES", facebookObject.toString())
                        socialData.name = facebookObject?.get("name") as? String
                        socialData.email = facebookObject?.get("email") as? String
                        socialData.image = facebookObject?.getJSONObject("picture")?.getJSONObject("data")?.get("url") as? String
                        //socialData.image = facebookObject.getJSONObject("picture").get("url") as? String
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                val parameters = Bundle()
                parameters.putString("fields", "name,email,id,picture.type(large)")
                request.parameters = parameters
                request.executeAsync()
                firebaseAuthWithFacebook(result.accessToken, socialData)
            }

            override fun onCancel() {
                Log.e("Facebook_Login", "Cancel")
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(this@WelcomeActivity, error.message.toString(), Toast.LENGTH_LONG).show()
                Log.e("Facebook_Login", error.toString())
            }

        })
    }

    private fun firebaseAuthWithFacebook(token: AccessToken, socialData: SocialUser?) {
        val credential = FacebookAuthProvider.getCredential(token.token!!)
        try {
            mAuth?.signInWithCredential(credential)
                    ?.subscribe(
                            {
                                mAuth!!.currentUser!!.reload()
                                getUser(mAuth?.currentUser!!.uid!!) { dbUser ->
                                    dbUser?.let { u ->
                                        adapter.open()
                                        val userCountry: Pais = adapter.getCountryInfoAbr(u.actualCountry ?: "BO")
                                        adapter.close()
                                        val country = userCountry.nombre
                                        val cod = userCountry.codigoArea
                                        val abr = userCountry.abreviacion
                                        val coin = userCountry.moneda
                                        loadData(country, cod, abr, coin)

                                        u.countryName = country
                                        u.currency = coin
                                        u.areaCode = cod
                                        Constants.userProfile = u
                                        Functions.savePersistentProfile(this@WelcomeActivity)
                                        subscribeTopic(abr)

                                        UserData.getToken { success ->
                                            if (success) {
                                                if (Constants.HUAWEI_TOKEN.isNotEmpty()) {
                                                    Functions.updateDeviceToken(
                                                            u.idUserFirebase ?: "",
                                                            u.idUser ?: "",
                                                            Constants.HUAWEI_TOKEN,
                                                            Constants.APP_GALLERY
                                                    )
                                                }

                                                if (u.flagTyc) {
                                                    val intent = Intent(this@WelcomeActivity, IntroActivity::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                } else {
                                                    val intent = Intent(this@WelcomeActivity, ConditionsActivity::class.java)
                                                    intent.putExtra("provenance", 0)
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            } else
                                                this@WelcomeActivity.runOnUiThread {
                                                    Toast.makeText(this@WelcomeActivity, "Fallo la autenticaciñon.",
                                                            Toast.LENGTH_LONG).show()
                                                }
                                        }
                                    } ?: run {
                                        Log.e("Error", "Usuario no existe en BD")
                                        validateProfile(mAuth?.currentUser?.uid, socialData)
                                    }
                                }
                            },
                            { throwable ->
                                LoginManager.getInstance().logOut()
                                if (throwable.message.equals("An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.")) {
                                    this@WelcomeActivity.runOnUiThread {
                                        Toast.makeText(this@WelcomeActivity, "Ya existe una cuenta con el mismo correo.",
                                                Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.e("TAG", "signInWithEmail:failure -> ${throwable.localizedMessage}")
                                    this@WelcomeActivity.runOnUiThread {
                                        Toast.makeText(this@WelcomeActivity, "Fallo la autenticaciñon.",
                                                Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                    )
        } catch (e: Exception) {
            Toast.makeText(this@WelcomeActivity, "Fallo la autenticaciñon.",
                    Toast.LENGTH_LONG).show()
        }
    }

    private fun validateProfile(isUser: String?, data: SocialUser?) {
        isUser?.let {
            val ref =  FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION).document(it)
            ref.get()
                    .addOnSuccessListener { fbUser ->
                        if (fbUser.exists()) {
                            val firebaseUser = fbUser.toObject(Usuario::class.java)
                            firebaseUser?.let { usr ->
                                UserData.saveFirebaseUser(this@WelcomeActivity, usr, adapter, it, Constants.APP_GALLERY) { message: String, result: Boolean ->
                                    if (result) {
                                        loadData(usr.pais, usr.codigoArea, usr.abreviacion, usr.moneda)
                                        val intentLog = Intent(this@WelcomeActivity, IntroActivity::class.java)
                                        startActivity(intentLog)
                                        finish()
                                    } else {
                                        LoginManager.getInstance().logOut()
                                        mAuth?.signOut()
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
            val intent = Intent(this@WelcomeActivity, CountrySelectionActivity::class.java)
            intent.putExtra("userId", it.uid)
            intent.putExtra("model", data)
            intent.putExtra("registerStore", Constants.APP_GALLERY)
            startActivity(intent)
            this.finish()
        }
    }

    private fun subscribeTopic(country: String) {
        try {
            HmsMessaging.getInstance(this@WelcomeActivity)
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