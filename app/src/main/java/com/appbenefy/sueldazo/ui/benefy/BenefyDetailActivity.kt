package com.appbenefy.sueldazo.ui.benefy

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.text.Html
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.base.BaseActivity
import com.appbenefy.sueldazo.entity.app.QRCode
import com.appbenefy.sueldazo.entity.service.Agency
import com.appbenefy.sueldazo.entity.service.PresentDetail
import com.appbenefy.sueldazo.service.NetworkService2
import com.appbenefy.sueldazo.service.RetrofitClientInstance
import com.appbenefy.sueldazo.ui.agency.ui.activities.AgencyActivity2
import com.appbenefy.sueldazo.ui.benefy.model.PurchasedProductDetail
import com.appbenefy.sueldazo.ui.benefy.model.PurchasedProductDetailRequest
import com.appbenefy.sueldazo.ui.home.model.PurchasedProductsResponse
import com.appbenefy.sueldazo.utils.Constants
import com.appbenefy.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.activity_benefy_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.DecimalFormat


class BenefyDetailActivity : BaseActivity() {

    private var productId: Int? = 0
    private var mName: String? = ""
    private var mLatitude: Double? = null
    private var mLongitude: Double? = null
    private var code: String? = ""
    private var commerceImage: String? = ""
    private var commerceName: String? = ""
    private var commerceFacebook: String? = ""
    private var commerceInstagram: String? = ""
    private var commerceWhatsapp: String? = ""
    private var modelArray: MutableList<Agency>? = null
    private var presentDetail: PresentDetail? = null
    private val format = DecimalFormat("###,##0.0#")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_benefy_detail)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.back_toolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = null

        // Extras
        val extras = intent.extras
        if (extras != null) {
            productId = extras.getInt("productId")
            getData()
        }

        val pm = applicationContext.packageManager

        availableSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                parent?.let {
                    val value = it.selectedItem as Int
                    val data = QRCode(value, code ?: "")
                    val jsonObject = Gson().toJson(data)
                    generateQR(jsonObject)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {  }
        }

        if (!Functions.isDarkTheme(this@BenefyDetailActivity) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            agenciesId.setCardBackgroundColor(getColor(R.color.gray_card_benefit))

        locationImageId.setOnClickListener { showAddressMenu(locationImageId) }

        showAgencies.setOnClickListener {
            val intent = Intent(this@BenefyDetailActivity, AgencyActivity2::class.java)
            intent.putExtra("image", commerceImage)
            intent.putExtra("name", commerceName)
            intent.putParcelableArrayListExtra("model", ArrayList(modelArray ?: arrayListOf()))
            startActivity(intent)
        }

        facebook.setOnClickListener { Functions.openURL(commerceFacebook, this@BenefyDetailActivity) }
        instagram.setOnClickListener { Functions.openURL(commerceInstagram, this@BenefyDetailActivity) }
        whatsapp.setOnClickListener { Functions.openWhatsApp(commerceWhatsapp, pm, this@BenefyDetailActivity) }
        sendRegard.setOnClickListener { shareGratitude() }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getData() {
        val request = PurchasedProductDetailRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            idProductIndex = productId ?: 0
        )
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2).create(NetworkService2::class.java)
        scopeMainThread.launch {
            try {
                val response = apiService.getPurchasedProductDetail(request)
                when {
                    response.isSuccessful -> {
                        val ret = response.body()!!
                        if (ret.isSuccess) {
                            ret.data?.let { showData(it) }
                        } else {
                            Functions.showWarning(this@BenefyDetailActivity, ret.description
                                    ?: "Ocurrió un error")
                            Log.e("Error", "${ret.code}")
                        }
                    }
                    else -> {
                        Functions.showWarning(this@BenefyDetailActivity, response.message()
                                ?: "Ocurrió un error")
                        Log.e("Error", response.message() + "-" + response.errorBody()?.toString())
                    }
                }
            } catch (e: Exception) {
                showError()
                Log.e("Exception", e.message ?: e.cause?.message ?: e.cause.toString())
            }
        }
    }

    private fun showData(model: PurchasedProductDetail) {
        modelArray = model.agencies?.toMutableList()
        code = model.qrCode
        if (model.agencies!!.isNotEmpty()) {
            mName = model.agencies?.get(0)?.nameProduct
            mLatitude = model.agencies?.get(0)?.latitude
            mLongitude = model.agencies?.get(0)?.longitude
            agencyNameId.text = model.agencies?.get(0)?.nameProduct
            agencyAddressId.text = model.agencies?.get(0)?.addressProduct
        }
        commerceImage = model.urlCommerce
        commerceName = model.commerceName
        commerceFacebook = model.commerceFacebook
        commerceInstagram = model.commerceInstagram
        commerceWhatsapp = model.commerceWhatsapp
        Functions.showRoundedImage(model.urlCommerce, commerceImageId)
        titleId.text = model.title
        couponPriceId.text = ""
        couponOriginalPriceId.text = Html.fromHtml("<del>${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.regularPrice)}</del>")
        val data = QRCode(1, model.qrCode ?: "")
        val jsonObject = Gson().toJson(data)
        generateQR(jsonObject)
        qrCodeId.text = model.qrCode
        val arr = IntArray(model.productsAvailable) { it + 1 }
        availableSpinner.adapter = ArrayAdapter(this@BenefyDetailActivity, R.layout.spinner_item, arr.toMutableList())
        val userAvailable = "${getString(R.string.coupons_available_user)} ${model.productsAvailable}"
        userQuantityId.text = userAvailable
        conditionsId.text = model.termsConditions
        presentDetail = model.presentDetail

        when (model.idProductType) {
            PurchasedProductsResponse.PURCHASED_COUPON -> {
                Functions.showImage(model.urlArt, couponImageId)
                subtitleId.text = model.subtitle
                if (model.percentPrice.equals(model.regularPrice)) couponOriginalPriceId.text = ""
                val originalPrice = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.percentPrice)}"
                couponPriceId.text = originalPrice
            }
            PurchasedProductsResponse.PURCHASED_GIFT_CARD -> {
                Functions.showImage(model.urlCommerce, couponImageId)
                subtitleId.text = "Giftcard"
                available.visibility = View.GONE
                price.orientation = LinearLayout.HORIZONTAL
                val originalPriceView = couponOriginalPriceId
                price.removeViewAt(1)
                price.addView(originalPriceView, 0)
                val paramsOriginalPrice = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                val paramsPrice = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                paramsOriginalPrice.marginEnd = 24
                couponOriginalPriceId.layoutParams = paramsOriginalPrice
                couponPriceId.layoutParams = paramsPrice
                couponOriginalPriceId.visibility = View.INVISIBLE
                val couponPrice = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.percentPrice)}"
                couponPriceId.text = couponPrice
            }
            PurchasedProductsResponse.CORPORATE_VOUCHER -> {
                Functions.showImage(model.urlArt, couponImageId)
                subtitleId.text = model.subtitle
                available.visibility = View.GONE
                price.orientation = LinearLayout.HORIZONTAL
                val originalPriceView = couponOriginalPriceId
                price.removeViewAt(1)
                price.addView(originalPriceView, 0)
                val paramsOriginalPrice = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                val paramsPrice = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                paramsOriginalPrice.marginEnd = 24
                couponOriginalPriceId.layoutParams = paramsOriginalPrice
                couponPriceId.layoutParams = paramsPrice
                couponOriginalPriceId.visibility = View.INVISIBLE
                val couponPrice = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.percentPrice)}"
                couponPriceId.text = couponPrice
            }
            PurchasedProductsResponse.ARTICLE_GIFT -> {
                giftRegards.visibility = View.VISIBLE
                Functions.showImage(model.urlArt, couponImageId)
                subtitleId.text = model.subtitle
                available.visibility = View.GONE
                price.orientation = LinearLayout.HORIZONTAL
                val originalPriceView = couponOriginalPriceId
                price.removeViewAt(1)
                price.addView(originalPriceView, 0)
                val paramsOriginalPrice = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                val paramsPrice = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                paramsOriginalPrice.marginEnd = 24
                couponOriginalPriceId.layoutParams = paramsOriginalPrice
                couponPriceId.layoutParams = paramsPrice
                couponOriginalPriceId.visibility = View.INVISIBLE
                couponPriceId.text = ""
            }
            PurchasedProductsResponse.GIFT_CARD_GIFT -> {
                giftRegards.visibility = View.VISIBLE
                Functions.showImage(model.urlCommerce, couponImageId)
                subtitleId.text = "Giftcard"
                available.visibility = View.GONE
                price.orientation = LinearLayout.HORIZONTAL
                val originalPriceView = couponOriginalPriceId
                price.removeViewAt(1)
                price.addView(originalPriceView, 0)
                val paramsOriginalPrice = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                val paramsPrice = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                paramsOriginalPrice.marginEnd = 24
                couponOriginalPriceId.layoutParams = paramsOriginalPrice
                couponPriceId.layoutParams = paramsPrice
                couponOriginalPriceId.visibility = View.INVISIBLE
                val couponPrice = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.percentPrice)}"
                couponPriceId.text = couponPrice
            }
            PurchasedProductsResponse.INSURANCE_GIFT -> {
                giftRegards.visibility = View.VISIBLE
                Functions.showImage(model.urlArt, couponImageId)
                subtitleId.text = model.subtitle
                available.visibility = View.GONE
                price.orientation = LinearLayout.HORIZONTAL
                val originalPriceView = couponOriginalPriceId
                price.removeViewAt(1)
                price.addView(originalPriceView, 0)
                val paramsOriginalPrice = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                val paramsPrice = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                paramsOriginalPrice.marginEnd = 24
                couponOriginalPriceId.layoutParams = paramsOriginalPrice
                couponPriceId.layoutParams = paramsPrice
                couponOriginalPriceId.visibility = View.INVISIBLE
                couponPriceId.text = ""
            }
            PurchasedProductsResponse.WELCOME_COUPON -> {
                Functions.showImage(model.urlArt, couponImageId)
                subtitleId.text = model.subtitle
                available.visibility = View.GONE
                price.orientation = LinearLayout.HORIZONTAL
                val originalPriceView = couponOriginalPriceId
                price.removeViewAt(1)
                price.addView(originalPriceView, 0)
                val paramsOriginalPrice = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                val paramsPrice = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                paramsOriginalPrice.marginEnd = 24
                couponOriginalPriceId.layoutParams = paramsOriginalPrice
                couponPriceId.layoutParams = paramsPrice
                couponOriginalPriceId.visibility = View.INVISIBLE
                val couponPrice = "${format.format(model.percentPrice)} %"
                couponPriceId.text = couponPrice
            }
            else -> {
                Functions.showImage(model.urlArt, couponImageId)
                subtitleId.text = model.subtitle
                couponPriceId.text =  if (model.percentPrice != 0.0) "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.percentPrice)}"
                    else "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.regularPrice)}"
            }
        }
    }

    private fun generateQR(qr: String?) {
        qr.let {
            val multiFormatWriter = MultiFormatWriter()
            try {
                val bitMatrix = multiFormatWriter.encode(qr, BarcodeFormat.QR_CODE, 300, 300)
                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.createBitmap(bitMatrix)
                qrImageId.setImageBitmap(bitmap)
            } catch (e: WriterException) { e.printStackTrace() }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun showAddressMenu(v: View) {
        val wrapper = ContextThemeWrapper(this@BenefyDetailActivity, R.style.popupMenuStyle)
        val inflater = MenuInflater(this@BenefyDetailActivity) //popup.getMenuInflater();
        val menuBuilder = MenuBuilder(this@BenefyDetailActivity)
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
        val marker =  Uri.encode(mLatitude.toString() + "," +
                mLongitude.toString() + "(" + mName + ")")
        val gmmIntentUri = Uri.parse("geo:" + mLatitude.toString() + "," +
                mLongitude.toString() + "?q=" + marker)
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

    private fun shareGratitude() {
        val sendIntent = Intent()
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val bmpUri = Functions.getLocalBitmapUri(commerceImageId)
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        sendIntent.putExtra(
                Intent.EXTRA_TEXT, "${getString(R.string.grateful)}\n\n<<"
                + getString(R.string.shared_from)
                + " " + getString(R.string.app_name) + "\n" + getString(R.string.app_url) + ">>"
        )

        if (bmpUri != null) {
            sendIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
            sendIntent.type = "image/*"
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        else sendIntent.type = "text/plain"

        startActivity(Intent.createChooser(sendIntent, getString(R.string.sendto)))
    }

    private fun showError() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.app_name))
        builder.setMessage(getString(R.string.loading_coupon_error))

        builder.setPositiveButton(getString(R.string.undertood)) { _, _ -> this.finish() }

        builder.show()
    }

}