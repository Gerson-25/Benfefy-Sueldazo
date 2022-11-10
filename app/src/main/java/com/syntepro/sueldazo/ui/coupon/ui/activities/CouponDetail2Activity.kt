package com.syntepro.sueldazo.ui.coupon.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.huawei.agconnect.applinking.AppLinking
import com.huawei.agconnect.applinking.ShortAppLinking
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.base.BaseActivity
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.entity.app.QRCode
import com.syntepro.sueldazo.ui.agency.model.AgencyResponse
import com.syntepro.sueldazo.ui.coupon.AgencyActivity
import com.syntepro.sueldazo.ui.coupon.model.CouponAgencyRequest
import com.syntepro.sueldazo.ui.coupon.model.CouponDetailRequest
import com.syntepro.sueldazo.ui.coupon.model.CouponDetailResponse
import com.syntepro.sueldazo.ui.coupon.model.UpdateCouponQuantityRequest
import com.syntepro.sueldazo.ui.coupon.viewModel.CouponViewModel
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.activity_coupon_detail2.*
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.util.*

class CouponDetail2Activity: BaseActivity() {

    private lateinit var couponViewModel: CouponViewModel
    private val format = DecimalFormat("###,##0.0#")
    private var couponId: String? = ""
    private var commerceId: String? = ""
    private var commerceName: String? = ""
    private var commerceImage: String? = ""
    private var couponFacebook: String? = ""
    private var couponWhatsApp: String? = ""
    private var couponInstagram: String? = ""
    private var userExchangeLimit = 0
    private var couponExchangeLimit = 0
    private var realPrice: Double? = 0.0
    private var discountPrice: Double? = 0.0
    private var categoryId: String? = ""
    private var qrCodeGenerated: String? = ""
    private var year: Int = 0
    private var month: Int = 0
    private var mName: String? = ""
    private var mLatitude: Double? = 0.0
    private var mLongitude: Double? = 0.0
    private var loyaltyType: Int = 0
    private var quantityUserSelected: Int = 1
    private var shortLinkText: String = ""
    private var longLinkText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_coupon_detail2)
        this.setFinishOnTouchOutside(true)

        couponViewModel = viewModel(viewModelFactory) {
            observe(couponDetail, ::handleCouponDetail)
            observe(couponAgency, ::handleCouponAgency)
            observe(updateCouponQuantity, ::handleUpdateCouponQuantity)
            failure(failure, ::handleError)
        }

        // Extras
        val extras = intent.extras
        if (extras != null) {
            couponId = extras.getString("couponId")
            loyaltyType = extras.getInt("loyaltyType")
            loadCouponDetail(couponId)
            loadCouponAgency(couponId)
        }

        val pm = applicationContext.packageManager

        val date = Calendar.getInstance()
        year = date[Calendar.YEAR]
        month = date[Calendar.MONTH] + 1

        share.setOnClickListener {
            shareContent("${titleId.text?.toString()} ${couponInfoId.text}", couponId, Constants.userProfile?.actualCountry ?: "BO")
        }
        whatsapp.setOnClickListener { Functions.openWhatsApp(couponWhatsApp, pm, this@CouponDetail2Activity) }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun loadCouponDetail(id: String?) {
        val request = CouponDetailRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                idUser = Constants.userProfile?.idUser ?: "",
                idCoupon = id ?: ""
        )
        couponViewModel.getCouponDetail(request)
    }

    private fun loadCouponAgency(id: String?) {
        val request = CouponAgencyRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                idCoupon = id ?: "",
                latitude = 0.0,
                longitude = 0.0,
                justOne = true
        )
        couponViewModel.loadCouponAgency(request)
    }

    private fun loadUpdateQuantity(selectedQuantity: Int) {
        val request = UpdateCouponQuantityRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                code = qrCodeGenerated ?: "",
                quantity = selectedQuantity
        )
        couponViewModel.updateUserCouponQuantity(request)
    }

    private fun handleCouponDetail(response: BaseResponse<CouponDetailResponse>?) {
        commerceId = response?.data?.idCommerce
        commerceName = response?.data?.commerceName
        commerceImage = response?.data?.urlCommerceImage
        couponFacebook = response?.data?.facebook
        couponWhatsApp = response?.data?.whatsapp
        couponInstagram = response?.data?.instagram
        couponExchangeLimit = response?.data?.cantExchage ?: 0
        userExchangeLimit = response?.data?.cantExchangeUserLimit ?: 0
        realPrice = response?.data?.realPrice ?: 0.0
        discountPrice = response?.data?.discountPrice ?: 0.0
        categoryId = response?.data?.idCouponCategory ?: ""
        qrCodeGenerated = response?.data?.qrCode ?: ""

        createAppLinking(commerceId, categoryId ?: "", Constants.userProfile?.actualCountry ?: "BO")

        val data = QRCode(response?.data?.generatedCoupons ?: 1, response?.data?.qrCode ?: "")
        val jsonObject = Gson().toJson(data)

        generateQRImage(jsonObject)

        Functions.showImage(response?.data?.urlCouponImage, commerceImageId)
        Functions.showRoundedImage(response?.data?.urlCommerceImage, commerceImageId)
        titleId.text = response?.data?.title


        val arr = IntArray(response?.data?.cantExchangeUserLimit ?: 1) { it + 1 }
        userQuantityId.text = "${getString(R.string.coupons_available_user)} ${response?.data?.cantExchangeUserLimit}"
        if (response?.data?.description != null) couponInfoId.text = response.data.description else couponInfoId.visibility = View.GONE
        conditionsId.text = response?.data?.conditions

        if (response?.data?.cantExchangeUserLimit == 0 && response.data.cantExchage == 0) {
            qrImageId.setImageBitmap(null)
            qrImageId.setBackgroundResource(R.drawable.ic_sin_cupones)
            qrCodeId.text = getString(R.string.sold_out)
            qrCodeId.textSize = 14f
        } else if (response?.data?.cantExchangeUserLimit == 0) {
            qrImageId.setImageBitmap(null)
            qrImageId.setBackgroundResource(R.drawable.ic_sin_cupones)
            qrCodeId.text = getString(R.string.exceeded)
            qrCodeId.textSize = 14f
        } else if (response?.data?.cantExchage == 0) {
            qrImageId.setImageBitmap(null)
            qrImageId.setBackgroundResource(R.drawable.ic_sin_cupones)
            qrCodeId.text = getString(R.string.sold_out)
            qrCodeId.textSize = 14f
        }
    }

    private fun handleCouponAgency(response: BaseResponse<List<AgencyResponse>>?) {
        response?.data?.let {
            if (it.isNotEmpty()) {
                mName = if (it[0].agencyName.isNullOrEmpty()) it[0].sucursalName
                else it[0].agencyName
                mLatitude = it[0].latitude
                mLongitude = it[0].longitude
            }
        }
    }

    private fun handleUpdateCouponQuantity(response: BaseResponse<Boolean>?) {
        Log.e("Update", "${response?.data}")
        response?.data?.let {
            val data = QRCode(quantityUserSelected, qrCodeGenerated ?: "")
            val jsonObject = Gson().toJson(data)

            generateQRImage(jsonObject)
        }
    }

    private fun generateQRImage(qr: String?) {
        qr?.let {
            val multiFormatWriter = MultiFormatWriter()
            try {
                val bitMatrix = multiFormatWriter.encode(it, BarcodeFormat.QR_CODE, 300, 300)
                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.createBitmap(bitMatrix)
                qrImageId.setImageBitmap(bitmap)
                qrCodeId.text = qrCodeGenerated
            } catch (e: WriterException) { e.printStackTrace() }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun showAddressMenu(v: View) {
        val wrapper = ContextThemeWrapper(this@CouponDetail2Activity, R.style.popupMenuStyle)
        val inflater = MenuInflater(this@CouponDetail2Activity) //popup.getMenuInflater();
        val menuBuilder = MenuBuilder(this@CouponDetail2Activity)
        inflater.inflate(R.menu.menu_address, menuBuilder)

        val optionsMenu = MenuPopupHelper(wrapper, menuBuilder, v)
        optionsMenu.setForceShowIcon(true)

        menuBuilder.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.viewInMapid -> {
                        showGoogleMaps()
                        true
                    }
                    R.id.openWithWazeId -> {
                        showWaze()
                        true
                    }
                    else -> false
                }
            }

            override fun onMenuModeChange(menu: MenuBuilder) {}
        })

        optionsMenu.setOnDismissListener {}
        optionsMenu.show()
    }

    private fun showGoogleMaps() {
        if(!hasLocation()) return
        val marker =  Uri.encode(mLatitude.toString()  + "," +
                mLongitude.toString()+ "(" + mName +")")
        val gmmIntentUri = Uri.parse("geo:" + mLatitude.toString()  + "," +
                mLongitude.toString() + "?q=" + marker )
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(this.packageManager) != null) {
            startActivity(mapIntent)
        }
    }

    @SuppressLint("ShowToast")
    private fun showWaze() {
        if(!hasLocation()) return
        try {
            val uri = "waze://?ll=" + mLatitude.toString() + "," + mLongitude.toString() + "&navigate=yes"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
        } catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(this, "No tienes la aplicación instalada.", Toast.LENGTH_LONG)
        }
    }

    private fun hasLocation(): Boolean {
        if(mLatitude == null || mLongitude == null){
            val toast = Toast.makeText(this, "Problemas con la ubicación, intentalo de nuevo.", Toast.LENGTH_LONG)
            toast.show()
            return false
        }
        return true
    }

    private fun getDifferenceDates(start: Date?, end: Date?): String {
        try {
            if (start == null || end == null) return ""
            var difference = end.time - start.time
            val seconds: Long = 1000
            val minutes = seconds * 60
            val hours = minutes * 60
            val days = hours * 24
            val resultDay = difference / days
            difference %= days
            return if (resultDay <= 0) getString(R.string.expire_today) else "$resultDay ${getString(R.string.days)}"
        } catch (e: Exception) {
            return ""
        }
    }

//    private fun shareCoupon(title: String, description: String) {
//        val sendIntent = Intent()
//        val builder = StrictMode.VmPolicy.Builder()
//        StrictMode.setVmPolicy(builder.build())
//        val bmpUri = Functions.getLocalBitmapUri(couponImageId)
//        sendIntent.action = Intent.ACTION_SEND
//        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
//        sendIntent.putExtra(
//                Intent.EXTRA_TEXT, title + "\n" + description + "\n\n<<"
//                + getString(R.string.shared_from)
//                + " " + getString(R.string.app_name) + "\n" + getString(R.string.app_url) + ">>"
//        )
//
//        if (bmpUri != null) {
//            sendIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
//            sendIntent.type = "image/*"
//            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        }
//        else sendIntent.type = "text/plain"
//
//        startActivity(Intent.createChooser(sendIntent, getString(R.string.sendto)))
//    }

    @SuppressLint("SetWorldReadable")
    private fun shareContent(content: String, id: String?, country: String) {
        if (id.isNullOrEmpty()) return
        val bitmap = getBitmapFromView(commerceImageId)
        try {
            val file = File(this.externalCacheDir, "coupon.png")
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
            file.setReadable(true, false)
            val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("https://www.beneficioslatam.com/?idCoupon=$id&country=$country"))
                    .setDomainUriPrefix("https://beneficioslatam.page.link") // Open links with this app on Android
                    .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build()) // Open links with com.example.ios on iOS
                    .setIosParameters(DynamicLink.IosParameters.Builder("com.syntepro.boliviaBeneficios").build())
                    .buildDynamicLink()
            val dynamicLinkUri = dynamicLink.uri
            val intent = Intent(Intent.ACTION_SEND)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLongLink(dynamicLinkUri)
                    .buildShortDynamicLink()
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Short link created
                            val shortLink = task.result?.shortLink
                            intent.putExtra(Intent.EXTRA_TEXT, "$content $shortLink")
                            val imageUri = FileProvider.getUriForFile(
                                    this@CouponDetail2Activity,
                                    "com.syntepro.appbeneficiosbolivia.provider",  //(use your app signature + ".provider" )
                                    file)
                            intent.putExtra(Intent.EXTRA_STREAM, imageUri)
                        } else {
                            // Error to get short link
                            intent.putExtra(Intent.EXTRA_TEXT, "$content $dynamicLinkUri")
                            val imageUri = FileProvider.getUriForFile(
                                    this@CouponDetail2Activity,
                                    "com.syntepro.appbeneficiosbolivia.provider",  //(use your app signature + ".provider" )
                                    file)
                            intent.putExtra(Intent.EXTRA_STREAM, imageUri)
                        }
                        intent.type = "image/png"
                        startActivity(Intent.createChooser(intent, "Share image via"))
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas)
        else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }

    /*
    * Huawei App link Method
    */
    private fun createAppLinking(id: String?, category: String, country: String) {
        val builder = AppLinking.Builder()
                .setUriPrefix("https://beneficioslatam.page.link")
                .setDeepLink(Uri.parse("https://www.beneficioslatam.com/?id=$id&c=$category&p=$country&t=2"))
                .setAndroidLinkInfo(AppLinking.AndroidLinkInfo.Builder().build())
                .setCampaignInfo(
                        AppLinking.CampaignInfo.Builder()
                                .setName("HDC")
                                .setSource("Huawei")
                                .setMedium("App")
                                .build())
        builder.buildShortAppLinking(ShortAppLinking.LENGTH.SHORT)
                .addOnSuccessListener { shortAppLinking: ShortAppLinking -> shortLinkText = shortAppLinking.shortUrl.toString() }
                .addOnFailureListener { e: Exception -> Log.e("Error", "${e.message}") }
        longLinkText = builder.buildAppLinking().uri.toString()
    }

    private fun shareLink(appLinking: String?) {
        if (appLinking != null) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, appLinking)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val view = window.decorView
        val lp = view.layoutParams as WindowManager.LayoutParams
        lp.gravity = Gravity.START or Gravity.CENTER
        lp.x = 0
        lp.y = 0
        lp.horizontalMargin = 0f
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        windowManager.updateViewLayout(view, lp)
    }

    override fun onDestroy() {
        super.onDestroy()
        setResult(Activity.RESULT_CANCELED)
    }

}
