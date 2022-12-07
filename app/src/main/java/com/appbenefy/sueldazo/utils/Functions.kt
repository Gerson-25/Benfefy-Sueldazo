package com.appbenefy.sueldazo.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.core.AndroidApplication
import com.appbenefy.sueldazo.entity.service.User
import com.appbenefy.sueldazo.entity.service.UserTokenRequest
import com.appbenefy.sueldazo.room.database.RoomDataBase
import com.appbenefy.sueldazo.room.entity.CountryUser
import com.appbenefy.sueldazo.service.NetworkService2
import com.appbenefy.sueldazo.service.RetrofitClientInstance
import com.appbenefy.sueldazo.ui.general.ErrorInfoDialog
import com.appbenefy.sueldazo.ui.general.InformationDialog
import com.appbenefy.sueldazo.ui.general.UserQRInfoDialog
import com.appbenefy.sueldazo.ui.general.WarningInfoDialog
import com.appbenefy.sueldazo.utils.Constants.Companion.PREFS_NAME
import com.appbenefy.sueldazo.utils.Constants.Companion.PREF_USER_PROFILE
import com.appbenefy.sueldazo.utils.Constants.Companion.userCountryProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class Functions {
    companion object {
        val roomDataBase: RoomDataBase = RoomDataBase.getRoomDatabase(AndroidApplication.applicationContext())
        private const val IMAGE_SIZE = 400

        private const val IMAGE_WIDTH = 400
        private const val IMAGE_HEIGHT = 400

        /**
         * Rodrigo Osegueda 06NOV2020
         *
         * Method to show user information.
         *
         * @property userImageId the image container.
         * @property welcomeId the name container.
         */
        fun readUserInfo(userImageId: ImageView, welcomeId: TextView, counterId: TextView?) {
            Constants.userProfile?.let {
                showRoundedImage(it.photoUrl, userImageId)
                val fn = it.names?.substringBefore(" ")
                welcomeId.text = "Hola, $fn"
            }
            if (Constants.NOTIFICATION_COUNTER != 0) {
                counterId?.visibility = View.VISIBLE
                if (Constants.NOTIFICATION_COUNTER > 99) counterId?.text = "99+"
                else counterId?.text = "${Constants.NOTIFICATION_COUNTER}"
            }
        }

        /**
         * Rodrigo Osegueda 06NOV2020
         *
         * Method to show URL image into ImageView.
         *
         * @property image the url image.
         * @property imageView the image container.
         */
        fun showImage(image: String?, imageView: ImageView) {
            if(image.isNullOrEmpty()) return
            Picasso.get()
                    .load(image)
//                    .resize(IMAGE_SIZE, IMAGE_SIZE)
//                    .onlyScaleDown()
//                .centerInside()
//                    .centerInside()
                    .noPlaceholder()
                    .into(imageView)
        }

        /**
         * Rodrigo Osegueda 06NOV2020
         *
         * This function formatted HTML from Spanned Android text.
         *
         * @property html HTML formatted string.
         * @return spanned android text.
         */
        @SuppressWarnings("deprecation")
        fun fromHtml(html: String): Spanned {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(html)
            }
        }

        /**
         * Rodrigo Osegueda 06NOV2020
         *
         * This function show URL image into Circle ImageView Transform.
         *
         * @property image the url image.
         * @property imageView the image container.
         */
        fun showRoundedImage(image: String?, imageView: ImageView) {
            if(image.isNullOrEmpty()) return
            Picasso.get()
                    .load(image)
                    .fit()
                    .centerInside()
                    .transform(CircleTransform())
                    .into(imageView)
        }

        /**
         * Rodrigo Osegueda 06NOV2020
         *
         * This function show PopUp Error.
         *
         * @property ctx context of activity.
         * @property message error message to show.
         */
        fun showError(ctx: Context?, message: String?, title: String?) {
            val intent = Intent(ctx, ErrorInfoDialog::class.java)
            intent.putExtra("error", message)
            intent.putExtra("title", title)
            ctx!!.startActivity(intent)
        }

        /**
         * Rodrigo Osegueda 06NOV2020
         *
         * This function show PopUp Warning.
         *
         * @property ctx context of activity.
         * @property message warning message to show.
         */
        fun showWarning(ctx: Context, message: String?) {
            val intent = Intent(ctx, WarningInfoDialog::class.java)
            intent.putExtra("warning", message)
            ctx.startActivity(intent)
        }

        /**
         * Rodrigo Osegueda 06NOV2020
         *
         * This function show general information to user.
         *
         * @property ctx context of activity.
         * @property title information title.
         * @property message description for user.
         */
        fun showInformation(ctx: Context, title: String?, message: String?) {
            val intent = Intent(ctx, InformationDialog::class.java)
            intent.putExtra("title", title)
            intent.putExtra("info", message)
            ctx.startActivity(intent)
        }

        /**
         * Rodrigo Osegueda 06NOV2020
         *
         * This function show user QR Image.
         *
         * @property ctx context of activity.
         */
        fun showUserQR(ctx: Context?) {
            val intent = Intent(ctx, UserQRInfoDialog::class.java)
            ctx!!.startActivity(intent)
        }

        fun setBackgroundDrawable(context: Context, layout: View, drawable: Int) {
            val sdk = Build.VERSION.SDK_INT
            if(sdk < Build.VERSION_CODES.JELLY_BEAN)
                layout.setBackgroundDrawable(ContextCompat.getDrawable(context, drawable))
            else
                layout.background = ContextCompat.getDrawable(context, drawable)
        }

        /**
         * Rodrigo Osegueda 06NOV2020
         *
         * This function get URI from ImageView.
         *
         * @property imageView image container.
         * @return Uri from image
         */
        fun getLocalBitmapUri(imageView: ImageView): Uri? {
            val drawable = imageView.drawable
            val bmp: Bitmap?
            if (drawable is BitmapDrawable) {
                bmp = (imageView.drawable as BitmapDrawable).bitmap
            } else {
                return null
            }
            // Store image to default external storage directory
            var bmpUri: Uri? = null
            try {
                val file = File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS
                        ), "share_image_" + System.currentTimeMillis() + ".png"
                )
                file.parentFile.mkdirs()
                val out = FileOutputStream(file)
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
                out.close()
                bmpUri = Uri.fromFile(file)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return bmpUri
        }

        // Room
        val userCountry: String
            get() {
                val cu = roomDataBase.accessDao().country
                return cu.abr
            }

        val userTimeZone:String
            get() {
                val cu = roomDataBase.accessDao().country
                return if (cu.timeZone.isNullOrEmpty()) {
                    "GMT"
                } else cu.timeZone
            }

        // Firebase
        val userUID:String
        get() {
            return Constants.userProfile?.idUserFirebase ?: ""
        }

        val userEmail:String
        get(){
            return Constants.userProfile?.email ?: ""
        }

        // User Configuration
        val userSession: CountryUser
        get() {
            return roomDataBase.accessDao().country
        }

        /**
         * @author Rodrigo Osegueda 22JUN2020
         *
         * This function get user information on the device.
         *
         * @property ctx context of the application.
         * @return variable with the user information.
         */
        fun getPersistentProfile(ctx: Context): User? {
            try {
                val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val gson = Gson()
                val uProfile = prefs.getString(PREF_USER_PROFILE, "")
                if (!uProfile.isNullOrEmpty())
                    return gson.fromJson(uProfile, User::class.java)
            } catch (e: Exception) { e.printStackTrace() }
            return null
        }

        /**
         * @author Rodrigo Osegueda 22JUN2020
         *
         * This function stores user information on the device.
         *
         * @property ctx context of the application.
         */
        fun savePersistentProfile(ctx: Context) {
            try {
                val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val gson = Gson()
                // User Profile
                val json: String
                if (userCountryProfile != null) {
                    json = gson.toJson(userCountryProfile)
                    prefs.edit().putString(PREF_USER_PROFILE, json).apply()
                } else
                    prefs.edit().remove(PREF_USER_PROFILE).apply()
            } catch (e: Exception) { e.printStackTrace() }
        }

        /**
         * @author Rodrigo Osegueda 16SEP2020
         *
         * This function updates the user's token for push notifications.
         *
         * @property deviceToken user and device token.
         *
         */
        fun updateDeviceToken(firebaseID: String, userID: String, deviceToken: String, userStore: Int) {
            val request = with(UserTokenRequest()) {
                country = Constants.userProfile?.country ?: "BO"
                language = 1
                idUser = userID
                idUserFirebase = firebaseID
                token = deviceToken
                device = "Android"
                store = userStore
                this
            }
            val job = Job()
            val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
            val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2).create(NetworkService2::class.java)
            scopeMainThread.launch {
                try {
                    val response = apiService.addUserToken(request)
                    when {
                        response.isSuccessful -> {
                            val ret = response.body()!!
                            if (ret.isSuccess)
                                Log.e("Synchronized Token", "${ret.data}")
                            else
                                Log.e("Error", "${ret.code}")
                        }
                        else -> { Log.e("Error", "${response.message()} - ${response.errorBody()}") }
                    }
                } catch (e: Exception) {
                    Log.e("Exception", e.message ?: e.cause?.message ?: e.cause.toString())
                }
            }
        }


        /**
         * @author Rodrigo Osegueda 25SEP2020
         *
         * This function requests permissions from the user.
         *
         * @property ctx context of the application.
         * @property permission permission to request.
         * @property id identifier for request.
         */
        @RequiresApi(api = Build.VERSION_CODES.M)
        fun requestPermission(ctx: Activity, permission: String, id: Int) {
            if (hasPermission(ctx, permission))
                return
            else {
                val permissionsToRequest = ArrayList<String>()
                permissionsToRequest.add(permission)
                ctx.requestPermissions(permissionsToRequest.toTypedArray(), id)
            }
        }

        /**
         * @author Rodrigo Osegueda 25SEP2020
         *
         * This function checks if the user has the requested permission.
         *
         * @property context context of the application.
         * @property permission permission to request.
         *
         * @return boolean value if user has permissions.
         */
        private fun hasPermission(
                context: Context,
                permission: String?
        ): Boolean {
            return context.checkCallingOrSelfPermission(permission!!) ==
                    PackageManager.PERMISSION_GRANTED
        }

        /**
         * @author Rodrigo Osegueda 10OCT2020
         *
         * This function checks the language of the device.
         *
         * @return int value with the type of language.
         */
        fun getLanguage(): Int {
            return when(Locale.getDefault().language) {
                "es" -> 1
                "en" -> 2
                else -> 1
            }
        }

        /**
         * @author Rodrigo Osegueda 10OCT2020
         *
         * This function open a URL on device browser.
         *
         * @property url string url to open on browser.
         * @property ctx context of activity.
         */
        fun openURL(url: String?, ctx: Context?) {
            url?.let {
                if(!url.startsWith("http")) {
                    showWarning(ctx!!, ctx.getString(R.string.invalid_url))
                    return
                }
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                ctx?.startActivity(i)
            } ?: run {
                showWarning(ctx!!, ctx.getString(R.string.invalid_url))
            }
        }

        /**
         * @author Rodrigo Osegueda 10OCT2020
         *
         * This function open a WhatsApp number chat.
         *
         * @property whatsApp number to send message.
         * @property pm package manager of application.
         * @property ctx context of activity.
         */
        fun openWhatsApp(whatsApp: String?, pm: PackageManager?, ctx: Context?) {
            whatsApp?.let {
                val url = "https://api.whatsapp.com/send?phone=$whatsApp"
                try {
                    pm?.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    ctx?.startActivity(i)
                } catch (e: PackageManager.NameNotFoundException) {
                    showWarning(ctx!!, ctx.getString(R.string.wa_not_instaled))
                }
            } ?: run {
                showWarning(ctx!!, "Error con el número ingresado")
            }
        }

        /**
         * Rodrigo Osegueda 01JUN2020
         *
         * This function open device mail app.
         *
         * @property email address to send mail.
         * @property context context of activity.
         */
        fun openEmail(context: Context, email: String?) {
            email.let {
                val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", it, null))
                context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
            }
        }

        /**
         * Rodrigo Osegueda 01JUN2020
         *
         * This function validate JSON format.
         *
         * @property json JSON string.
         *
         * @return Boolean if JSON is valid.
         */
        fun isJSONValid(json: String): Boolean {
            try {
                JSONObject(json)
            } catch (ex: JSONException) {
                try {
                    JSONArray(json)
                } catch (ex1: JSONException) { return false }
            }
            return true
        }

        /**
         * Rodrigo Osegueda 01JUN2020
         *
         * This function get the theme of device.
         *
         * @property activity calling activity.
         *
         * @return Boolean if device is in DarkMode.
         */
        fun isDarkTheme(activity: Activity): Boolean {
            return activity.resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }

        /**
         * Rodrigo Osegueda 01JUN2020
         *
         * This function get the theme of device.
         *
         * @property context context of the activity.
         *
         * @return Boolean if device GPS is enabled.
         */
        fun isLocationEnabled(context: Context): Boolean {
            var locationMode = 0
            val locationProviders: String
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                locationMode = try {
                    Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
                } catch (e: SettingNotFoundException) {
                    e.printStackTrace()
                    return false
                }
                locationMode != Settings.Secure.LOCATION_MODE_OFF
            } else {
                locationProviders = Settings.Secure.getString(context.contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
                !TextUtils.isEmpty(locationProviders)
            }
        }

        /**
         * Rodrigo Osegueda 01JUN2020
         *
         * This function detect credit card type.
         *
         * @property CreditCardNumber string credit card number.
         *
         * @return String with the credit card type.
         */
        fun getCreditCardType(CreditCardNumber: String): String {
            val regVisa = Regex("^4[0-9]{12}(?:[0-9]{3})?$")
            val regMaster = Regex("^5[1-5][0-9]{14}$")
            val regExpress = Regex("^3[47][0-9]{13}$")
            val regDiners = Regex("^3(?:0[0-5]|[68][0-9])[0-9]{11}$")
            val regDiscover = Regex("^6(?:011|5[0-9]{2})[0-9]{12}$")
            val regJCB = Regex("^(?:2131|1800|35\\d{3})\\d{11}$")
            return if (regVisa.matches(CreditCardNumber)) "VISA" else if (regMaster.matches(CreditCardNumber)) "MASTER" else if (regExpress.matches(CreditCardNumber)) "AEXPRESS" else if (regDiners.matches(CreditCardNumber)) "DINERS" else if (regDiscover.matches(CreditCardNumber)) "DISCOVERS" else if (regJCB.matches(CreditCardNumber)) "JCB" else "VISA CARD"
        }

        /**
         * Rodrigo Osegueda 01JUN2020
         *
         * This function detect credit card type.
         *
         * @property countryName string country name.
         *
         * @return String with the country abbreviation.
         */
        fun getCountryAbbreviation(countryName: String): String {
            return when (countryName) {
                "El Salvador" -> "SV"
                "Bolivia" -> "BO"
                "Guatemala" -> "GT"
                else -> ""
            }
        }

    }
}