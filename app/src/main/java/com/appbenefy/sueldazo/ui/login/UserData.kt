package com.appbenefy.sueldazo.ui.login

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.appbenefy.sueldazo.database.DataBaseAdapter
import com.appbenefy.sueldazo.entity.app.Pais
import com.appbenefy.sueldazo.entity.firebase.Usuario
import com.appbenefy.sueldazo.entity.service.SaveUserRequest
import com.appbenefy.sueldazo.entity.service.TokenRequest
import com.appbenefy.sueldazo.entity.service.User
import com.appbenefy.sueldazo.service.NetworkService2
import com.appbenefy.sueldazo.service.RetrofitClientInstance
import com.appbenefy.sueldazo.utils.Constants
import com.appbenefy.sueldazo.utils.Functions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Gerson Aquino 7ENE2022
 *
 * @idUserFirebase asignation was change since
 * was taking the incorrect value and caused to
 * crashed the app after a user register
 *
 */
object UserData {
    fun saveFirebaseUser(context: Context, model: Usuario, adapter: DataBaseAdapter, id: String, userStore: Int, completion: (String, Boolean) -> Unit) {
        val request = with(SaveUserRequest()) {
            language = 1
            names = model.nombre
            lastNames = model.apellido
            phone = model.numeroTel.replace("-", "")
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            var date: Date? = Date()
            if (!model.fechaNac.isNullOrEmpty()) try { date = formatter.parse(model.fechaNac) } catch (e: Exception) { e.printStackTrace() }
            val userBirthDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).format(date ?: Date())
            birthDate = userBirthDate
            gender = model.genero
            maritalStatus = model.estadoCivil
            photoUrl = model.imagenPerfil
            email = model.correo
            this
        }
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2).create(NetworkService2::class.java)
        scopeMainThread.launch {
            try {
                val response = apiService.saveUser(request)
                when {
                    response.isSuccessful -> {
                        val ret = response.body()!!
                        if (ret.isSuccess) {
                            ret.data?.let {
                                val userProfile = with(User()) {
                                    idUser = it
                                    idUserFirebase = id
                                    names = model.nombre
                                    lastNames = model.apellido
                                    phone = model.numeroTel.replace("-", "")
                                    birthDate = model.fechaNac
                                    gender = model.genero
                                    maritalStatus = model.estadoCivil
                                    photoUrl = model.imagenPerfil
                                    email = model.correo
                                    adapter.open()
                                    val userCountry: Pais = adapter.getCountryInfoAbr("BO")
                                    adapter.close()
                                    actualCountry = userCountry.abreviacion
                                    countryName = userCountry.nombre
                                    currency = userCountry.moneda
                                    areaCode = userCountry.codigoArea
                                    this
                                }
                                Constants.userProfile = userProfile
                                Functions.savePersistentProfile(context)
                                firebaseToken(it)
                                getToken {  }
                                completion("Ok", true)
                            }
                        } else { completion(ret.description ?: "Ocurrió un error", false) }
                    }
                    else -> { completion(response.message() ?: "Ocurrió un error", false) }
                }
            } catch (e: Exception) {
                Log.e("Exception", e.message ?: e.cause?.message ?: e.cause.toString())
                completion("Ocurrió un error", false)
            }
        }
    }

    fun getToken(completion: (Boolean) -> Unit) {
        val request = with(TokenRequest()) {
            userId = Constants.userProfile?.idUserFirebase
            this
        }
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_SECURITY).create(NetworkService2::class.java)
        scopeMainThread.launch {
            try {
                val response = apiService.getToken(request)
                when {
                    response.isSuccessful -> {
                        val ret = response.body()!!
                        if (ret.isSuccess) {
                            Constants.TOKEN = ret.data ?: ""
                            Log.e("App", "${Constants.TOKEN}")
                            completion(true)
                        } else { completion(false) }
                    }
                    else -> { completion(false) }
                }
            } catch (e: Exception) {
                completion(false)
                Log.e("Exception", e.message ?: e.cause?.message ?: e.cause.toString())
            }
        }
    }

    fun firebaseToken(idUser: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

//            val androidId = Settings.Secure.getString(
//                this.contentResolver,
//                Settings.Secure.ANDROID_ID
//            )

            // Get new FCM registration token
            val token = task.result
            Log.e("Firebase Token", token)
            Functions.updateDeviceToken(
                Constants.userProfile?.idUserFirebase ?: "",
                idUser,
                token,
                Constants.PLAY_STORE
            )
        })



//
//        { instanceResult ->
//            //            val androidId = Settings.Secure.getString(
////                this.contentResolver,
////                Settings.Secure.ANDROID_ID
////            )
//            Log.e("Firebase Token", instanceResult)
//            Functions.updateDeviceToken(
//                Constants.userProfile?.idUserFirebase ?: "",
//                idUser,
//                instanceResult,
//                Constants.PLAY_STORE
//            )
//        }
    }

}
