package com.syntepro.appbeneficiosbolivia.utils

import com.syntepro.appbeneficiosbolivia.core.AndroidApplication
import com.syntepro.appbeneficiosbolivia.entity.service.User
import com.syntepro.appbeneficiosbolivia.room.entity.CountryUser
import com.syntepro.appbeneficiosbolivia.ui.home.model.StatesResponse
import com.syntepro.appbeneficiosbolivia.ui.home.model.SudamericanaData
import com.syntepro.appbeneficiosbolivia.ui.home.model.TransactionData
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.getPersistentProfile

class
Constants {
    companion object {
        // Application
        const val PREFS_NAME = "BenefyPrefsFile"
        const val PREF_USER_PROFILE = "userProfile"
        const val PROFILE_IMAGE = "https://firebasestorage.googleapis.com/v0/b/app-seguros-comedica.appspot.com/o/not-found.png?alt=media&token=5fc951a6-365c-45fd-9fb9-00560018f67a"

        // Firebase
        const val TRADE_COLLECTION = "Comercios"
        const val TRADE_CATEGORY_COLLECTION = "Rubros"
        const val MY_TRADES_COLLECTION = "Comercios"
        const val USERS_COLLECTION = "Usuarios"
        const val LOYALTY_PLAN_COLLECTION = "PlanesLealtad"
        const val MY_LOYALTY_PLAN_COLLECTION = "MisPlanesLealtad"
        const val STAMP_CARDS_COLLECTION = "TarjetaSellos"
        const val USER_COLLECTION = "Usuarios"

        // Firebase Storage
        const val PROFILE_IMAGES_PATH = "profileImages"
        const val PROFILE_THUMBS_IMAGE_PATH = "profileThumbImages"

        // Google
        const val RC_SIGN_IN = 1000

        // Configuration
        const val LIST_PAGE_SIZE = 20
        const val LIST_PREFETCH_DISTANCE = 10
        const val PAGE_SIZE = 20
        const val INITIAL_LOAD_SIZE_HINT = 10
        const val IMAGE_REQUEST = 200
        const val MAX_THUMB_WIDTH = 70
        const val MAX_THUMB_HEIGHT = 70
        const val NEW_IMAGE_SIZE = 50
        const val IMAGE_PROFILE_SIZE = 300
        const val SUCCESS_TRANSACTION = "https://beneficioslatam.com/trx/exitoso.html?key=7F04EF01-024A-416C-8352-C6E074B81E3B"
        const val FAILED_TRANSACTION = "https://beneficioslatam.com/trx/err.html?key=7F04EF01-024A-416C-8352-C6E074B81E3B"
        const val HUAWEI_IMAGE = "https://firebasestorage.googleapis.com/v0/b/beneficios-1b534.appspot.com/o/Comercios%2facadb8fb-0acc-4ba1-08e4-08d860d26773?alt=media&token=acadb8fb-0acc-4ba1-08e4-08d860d26773"
        const val MOBILE_BRAND = "HUAWEI"
        const val APP_GALLERY = 1
        const val APP_GALLERY_VERSION = "VERSION_HUAWEI"
        const val PLAY_STORE = 3
        const val PLAY_STORE_VERSION = "VERSION_ANDROID"
        var TOKEN: String = ""
        var HUAWEI_TOKEN = ""
        var NOTIFICATION_COUNTER = 0
        var sudamericanaParameters: List<SudamericanaData>? = null
        var transactionParameters: List<TransactionData>? = null
        var countryStates: MutableList<StatesResponse>? = null

        // Filter Constants
        var stateFiltered: String? = null
        var categoryFiltered: String? = null
        var stateNameFiltered: String? = null
        var cityNameFiltered: String? = null
        var branchFilteredList: MutableList<String>? = null

        // Loyalty
        var isSelectedPlan: String? = ""
        var idPlanType: Int? = 0
        var idPushPlan: String? = ""

        // URL DEV
//        var BASE_URL_MICRO: String = "http://172.30.126.112:8090/api/"
//        var BASE_URL_MICRO2: String = "http://172.30.126.112:8092/"
//        var BASE_URL_TRACKING: String = "http://172.30.20.108:9080/"
//        var BASE_URL_SECURITY: String = "http://172.30.126.112:8094/"
//        var BASE_URL_PAYMENT_GATEWAY = "http://172.30.126.112:8093/"
//        const val  API_KEY: String = "75APPMA1-B4DF-475E-A331-F737424F013C"

        // URL DEV
//        var BASE_URL_MICRO: String = "http://172.26.126.112:8090/api/"
//        var BASE_URL_MICRO2: String = "http://172.26.126.112:8092/"
//        var BASE_URL_TRACKING: String = "http://172.26.20.108:9080/"
//        var BASE_URL_SECURITY: String = "http://172.26.126.112:8094/"
//        var BASE_URL_PAYMENT_GATEWAY = "http://172.26.126.112:8093/"
//        const val  API_KEY: String = "75APPMA1-B4DF-475E-A331-F737424F013C"

//        // URL UAT
//        var BASE_URL_MICRO: String = "https://backoffice.uat.beneficioslatam.com/api/"
//        var BASE_URL_MICRO2: String = "https://backofficeV2.uat.beneficioslatam.com/"
//        var BASE_URL_TRACKING: String = "http://192.168.1.68:84/"
//        var BASE_URL_SECURITY: String = "https://security.uat.beneficioslatam.com/"
//        var BASE_URL_PAYMENT_GATEWAY = "https://paymentGateway.uat.beneficioslatam.com/"
//        const val API_KEY: String = "75APPMA1-B4DF-475E-A331-F737424F013C"

        // URL UAT
//        var BASE_URL_MICRO: String = "https://uat.bck.backofficev1.appbenefy.com/api/"
//        var BASE_URL_MICRO2: String = "https://uat.bck.backofficev2.appbenefy.com/"
//        var BASE_URL_TRACKING: String = "http://192.168.1.68:84/"
//        var BASE_URL_SECURITY: String = "https://uat.bck.security.appbenefy.com/"
//        var BASE_URL_PAYMENT_GATEWAY = "https://uat.bck.paymentgateway.appbenefy.com/"
//        const val API_KEY: String = "75APPMA1-B4DF-475E-A331-F737424F013C"

        // URL PRD
//        var BASE_URL_MICRO: String = "https://backoffice.beneficioslatam.com:85/api/"
//        var BASE_URL_MICRO2: String = "https://backofficeV2.beneficioslatam.com/"
//        var BASE_URL_TRACKING: String = "https://backoffice.beneficioslatam.com:86/api/"
//        var BASE_URL_SECURITY: String = "https://security.beneficioslatam.com/"
//        var BASE_URL_PAYMENT_GATEWAY = "https://paymentGateway.beneficioslatam.com/"
//        const val  API_KEY: String = "75APPMA1-B4DF-475E-A331-F737424F013CPR"

        // URL PRD
//        var BASE_URL_MICRO: String = "https://bck.backofficev1.appbenefy.com/api/"
//        var BASE_URL_MICRO2: String = "https://bck.backofficev2.appbenefy.com/"
//        var BASE_URL_TRACKING: String = "https://bck.backofficev1.appbenefy.com:86/api/"
//        var BASE_URL_SECURITY: String = "https://bck.security.appbenefy.com/"
//        var BASE_URL_PAYMENT_GATEWAY = "https://bck.paymentgateway.appbenefy.com/"
//        const val  API_KEY: String = "75APPMA1-B4DF-475E-A331-F737424F013CPR"

        // URL DEV
        var BASE_URL_MICRO: String = "http://172.30.126.112:8090/api/"
        var BASE_URL_MICRO2: String = "http://172.30.126.112:8092/"
        var BASE_URL_TRACKING: String = "http://172.30.20.108:9080/"
        var BASE_URL_SECURITY: String = "http://172.30.126.112:8094/"
        var BASE_URL_PAYMENT_GATEWAY = "http://172.30.126.112:8093/"
        var BASE_URL_MICROSUELDAZO: String = "http://192.168.194.175:9000/"
        const val  API_KEY: String = "75APPMA1-B4DF-475E-A331-F737424F013C"

        // Variables
        private var _userProfile: User? = null
        var userProfile: User?
            get() {
                if (_userProfile == null) { getPersistentProfile(AndroidApplication.applicationContext()) }
                return _userProfile
            }
            set (value) { _userProfile = value }

        private var _anonymousUserProfile: User? = null
        var anonymousUserProfile: User?
            get() {
                if (_anonymousUserProfile == null) { getPersistentProfile(AndroidApplication.applicationContext()) }
                return _anonymousUserProfile
            }
            set (value) { _anonymousUserProfile = value }

        // Search Adapter
        const val SUGGESTION_COUPON = 1
        const val SUGGESTION_COMMERCE = 2
        const val SUGGESTION_BRANCH_OFFICE = 3
        const val SUGGESTION_TOP_TRENDS = 4
        const val SUGGESTION_VIP_COUPON = 5

        // User
        var userCountryProfile: CountryUser? = null

        //Variable Explore type tapped
        //2,3,4 Explore
        //5 CouponActivity
        //6 CouponActivity2
        var provenance = ""
        val map = mutableMapOf<String, Any>()

        var userPhone: String? = null
    }
}