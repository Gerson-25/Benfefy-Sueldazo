package com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.entity.service.*
import com.syntepro.appbeneficiosbolivia.service.NetworkService2
import com.syntepro.appbeneficiosbolivia.service.RetrofitClientInstance
import com.syntepro.appbeneficiosbolivia.ui.agency.ui.activities.AgencyActivity2
import com.syntepro.appbeneficiosbolivia.ui.menu.EditProfileActivity2
import com.syntepro.appbeneficiosbolivia.ui.shop.model.*
import com.syntepro.appbeneficiosbolivia.ui.sudamericana.model.SurveyRequest
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.activity_commerce_detail2.*
import kotlinx.android.synthetic.main.activity_shop_detail.*
import kotlinx.android.synthetic.main.activity_shop_detail.commerceImageId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat

class ShopDetailActivity : AppCompatActivity() {

    private var shopId: String? = ""
    private var type: Int = 0
    private var total: Int = 0
    private var productAmount: Double = 0.0
    private var regularPrice: Double = 0.0
    private var percentage: Double = 0.0
    private var amountGift: Double = 0.0
    private var mName: String? = ""
    private var mLatitude: Double? = null
    private var mLongitude: Double? = null
    private var commerceImage: String? = ""
    private var commerceName: String? = ""
    private var productType: Int = 0
    private var modelArray: MutableList<Agency>? = null
    private var userQuestionnaire: SurveyRequest? = null
    private var userArticleGift: UserArticleGift? = null
    private var userDeliveryDetail: DeliveryDetail? = null
    private var allowsDelivery: Boolean = false
    private var maxDistanceDelivery: Double = 0.0
    private var dispatchPoints: List<DispatchPoints>? = null
    private var deliveryPrices: List<DeliveryPrices>? = null
    private var articleTotalPrice: Double? = 0.0
    private val format = DecimalFormat("###,##0.0#")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_detail)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.back_toolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = null

        if (!Functions.isDarkTheme(this@ShopDetailActivity) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            agenciesId.setCardBackgroundColor(getColor(R.color.gray_card_benefit))

        locationImageId.setOnClickListener { showAddressMenu(locationImageId) }

        minus.setOnClickListener {
            if (type == 1) {
                if (total > 1) {
                    total -= 1
                    counterId.text = "$total"
                    calculatePrice()
                }
            } else {
                if (total > 0) {
                    total -= 5
                    counterId.text = "$total"
                    calculateGift()
                }
            }
        }

        plus.setOnClickListener {
            if (type == 1) {
                total += 1
                counterId.text = "$total"
                calculatePrice()
            } else {
                total += 5
                counterId.text = "$total"
                calculateGift()
            }
        }

        showAgencies.setOnClickListener {
            val intent = Intent(this@ShopDetailActivity, AgencyActivity2::class.java)
            intent.putExtra("image", commerceImage)
            intent.putExtra("name", commerceName)
            intent.putParcelableArrayListExtra("model", ArrayList(modelArray))
            startActivity(intent)
        }

        buy.setOnClickListener {
            buy.text = ""
            progress_bar_accept.visibility = View.VISIBLE
            val intent = Intent(this@ShopDetailActivity, EditProfileActivity2::class.java)
            if (productType == ArticleResponse.PERSONAL_ACCIDENT_INSURANCE || productType == ArticleResponse.PET_INSURANCE || productType == ArticleResponse.COVID_INSURANCE) {
                intent.putExtra("provenance", 3)
                intent.putExtra("insuranceType", productType)
            } else intent.putExtra("provenance", 2)
            intent.putExtra("id", shopId)
            intent.putExtra("articlePrice", articleTotalPrice)
            intent.putExtra("allowDelivery", allowsDelivery)
            intent.putExtra("distance", maxDistanceDelivery)
            intent.putParcelableArrayListExtra("dispatchPoints", ArrayList(dispatchPoints ?: arrayListOf()))
            intent.putParcelableArrayListExtra("deliveryPrices", ArrayList(deliveryPrices ?: arrayListOf()))
            startActivityForResult(intent, 750)
        }

        shareItemId.setOnClickListener {
            val longLink = buildLongDynamicLink(shopId!!, type)
            val bitmap = getBitmapFromView(couponImageId)
            val file: File
            try {
                file = File(this.externalCacheDir, "item.png")
                val fOut = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                fOut.flush()
                fOut.close()
                val imageUri = FileProvider.getUriForFile(this, "com.syntepro.appbeneficiosbolivia.provider", file)
                buildShortDynamicLink(longLink) { succes, shortLink ->
                    if (succes) {
                        shareItem("", imageUri, shortLink!!)
                    } else {
                        shareItem("", imageUri, longLink)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 750 && resultCode == Activity.RESULT_OK) {
            userQuestionnaire = data?.getSerializableExtra("model") as? SurveyRequest
            userArticleGift = data?.getSerializableExtra("giftModel") as? UserArticleGift
            userDeliveryDetail = data?.getSerializableExtra("deliveryModel") as? DeliveryDetail
            generatePayOrder()
        } else if (requestCode == 550 && resultCode == Activity.RESULT_CANCELED) {
            data?.let {
                val error = it.getBooleanExtra("error", false)
                if (error) Functions.showError(this, getString(R.string.payment_error), "")
            }
        } else {
            buy.text = getString(R.string.buy)
            progress_bar_accept.visibility = View.GONE
        }
    }

    private fun generatePayOrder() {
        val request = with(PaymentOrderRequest()) {
            country = Constants.userProfile?.actualCountry ?: "BO"
            language = Functions.getLanguage()
            idElement = shopId
            quantity = if (type == 1) total else 1
            elementType = productType
            amount = productAmount
            userInfo?.idUser = Constants.userProfile?.idUser
            userInfo?.identityCard = "1212121212"
            userInfo?.phoneModel = "Android"
            userInfo?.businessName = ""
            userInfo?.nit = ""
            questionnaire = userQuestionnaire
            presentDetail = userArticleGift
            deliveryDetail = userDeliveryDetail
            this
        }
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2).create(NetworkService2::class.java)
        scopeMainThread.launch {
            try {
                val response = apiService.createPaymentOrder(request)
                when {
                    response.isSuccessful -> {
                        val ret = response.body()!!
                        if (ret.isSuccess) {
                            ret.data?.let {
                                when (it.payType) {
                                    1 -> {
                                        buy.text = getString(R.string.buy)
                                        progress_bar_accept.visibility = View.GONE
                                        val intent = Intent(this@ShopDetailActivity, PaymentOrderActivity::class.java)
                                        intent.putExtra("url", it.urlPayment)
                                        startActivityForResult(intent, 550)
                                    }

                                    2 -> {
                                        buy.text = getString(R.string.buy)
                                        progress_bar_accept.visibility = View.GONE
                                        val intent = Intent(this@ShopDetailActivity, CardInformationActivity::class.java)
                                        intent.putExtra("orderId", it.idOrder)
                                        startActivityForResult(intent, 550)
                                    }
                                }
                            }
                        } else {
                            buy.text = getString(R.string.buy)
                            progress_bar_accept.visibility = View.GONE
                            Functions.showWarning(this@ShopDetailActivity, ret.description ?: "Ocurrió un error")
                            Log.e("Error", "${ret.code}")
                        }
                    }
                    else -> {
                        buy.text = getString(R.string.buy)
                        progress_bar_accept.visibility = View.GONE
                        Functions.showWarning(this@ShopDetailActivity, response.message() ?: "Ocurrió un error")
                        Log.e("Error", response.message() + "-" + response.errorBody()?.string())
                    }
                }
            } catch (e: Exception) {
                buy.text = getString(R.string.buy)
                progress_bar_accept.visibility = View.GONE
                showOrderError()
                Log.e("Exception", e.message ?: e.cause?.message ?: e.cause.toString())
            }
        }
    }

    private fun showCouponData(model: ArticleResponse) {
        productType = model.idProductType
        modelArray = model.agencies?.toMutableList()
        allowsDelivery = model.allowsDelivery
        maxDistanceDelivery = model.maxDistanceDelivery
        dispatchPoints = model.dispatchPoints
        deliveryPrices = model.deliveryPrices
        if (model.agencies!!.isNotEmpty()) {
            mName = model.agencies?.get(0)?.nameProduct
            mLatitude = model.agencies?.get(0)?.latitude
            mLongitude = model.agencies?.get(0)?.longitude
            agencyNameId.text = model.agencies?.get(0)?.nameProduct
            agencyAddressId.text = model.agencies?.get(0)?.addressProduct
        }
        commerceImage = model.commerceImageUrl
        commerceName = model.commerceName
        regularPrice = model.regularPrice
        percentage = model.percentage
        Functions.showImage(model.imageUrl, couponImageId)
        Functions.showRoundedImage(model.commerceImageUrl, commerceImageId)
        titleId.text = model.title
        subtitleId.text = model.subtitle
        counterId.text = "$total"
        val discount = (model.regularPrice) * (model.percentage / 100.0f)
        val newPrice = model.regularPrice - discount
        priceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(newPrice)}"
        model.description?.let { articleDescriptionId.text = it } ?: run { descriptionContent.visibility = View.GONE }
        conditionsId.text = model.conditions
        articleTotalPrice = newPrice
    }

    private fun showGiftCardData(model: GiftCard) {
        productType = 2
        modelArray = model.agencies?.toMutableList()
        amountGift = model.minimumAmount
        productAmount = model.minimumAmount
        if (model.agencies!!.isNotEmpty()) {
            mLatitude = model.agencies?.get(0)?.latitude
            mLongitude = model.agencies?.get(0)?.longitude
            mName = model.agencies?.get(0)?.nameProduct
            agencyNameId.text = model.agencies?.get(0)?.nameProduct
            agencyAddressId.text = model.agencies?.get(0)?.addressProduct
        }
        commerceImage = model.urlCommerce
        commerceName = model.commerceName
        Functions.showImage(model.urlCommerce, couponImageId)
        Functions.showRoundedImage(model.urlCommerce, commerceImageId)
        titleId.text = model.giftcardName
        subtitleId.visibility = View.GONE
        counterId.text = "${1}"
        priceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.minimumAmount)}"
        descriptionContent.visibility = View.GONE
        conditionsId.text = model.conditions
        articleTotalPrice = model.minimumAmount
    }

    private fun calculatePrice() {
        if (regularPrice == 0.0) return
        val discount = (regularPrice * total) * (percentage / 100.0f)
        val newPrice = (regularPrice * total) - discount
        priceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(newPrice)}"
        articleTotalPrice = newPrice
        productAmount = discount
    }

    private fun calculateGift() {
        if (amountGift == 0.0) return
        priceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(amountGift + total)}"
        productAmount = amountGift + total
        articleTotalPrice = amountGift + total
    }

    @SuppressLint("RestrictedApi")
    private fun showAddressMenu(v: View) {
        val wrapper = ContextThemeWrapper(this@ShopDetailActivity, R.style.popupMenuStyle)
        val inflater = MenuInflater(this@ShopDetailActivity)
        val menuBuilder = MenuBuilder(this@ShopDetailActivity)
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

            override fun onMenuModeChange(menu: MenuBuilder) { }
        })

        optionsMenu.setOnDismissListener { }
        optionsMenu.show()
    }

    private fun showGoogleMaps() {
        if (!hasLocation()) return
        val marker = Uri.encode(
            mLatitude.toString() + "," +
                mLongitude.toString() + "(" + mName + ")"
        )
        val gmmIntentUri = Uri.parse(
            "geo:" + mLatitude.toString() + "," +
                mLongitude.toString() + "?q=" + marker
        )
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(this.packageManager) != null) startActivity(mapIntent)
    }

    @SuppressLint("ShowToast")
    private fun showWaze() {
        if (!hasLocation()) return
        try {
            val uri = "waze://?ll=" + mLatitude.toString() + "," + mLongitude.toString() + "&navigate=yes"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "No tienes la aplicación instalada.", Toast.LENGTH_LONG)
        }
    }

    private fun hasLocation(): Boolean {
        if (mLatitude == null || mLongitude == null) {
            val toast = Toast.makeText(this, "Problemas con la ubicación, intentalo de nuevo.", Toast.LENGTH_LONG)
            toast.show()
            return false
        }
        return true
    }

    private fun showError() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.app_name))
        builder.setMessage(getString(R.string.loading_coupon_error))

        builder.setPositiveButton(getString(R.string.undertood)) { _, _ -> this.finish() }

        builder.show()
    }

    private fun showOrderError() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.app_name))
        builder.setMessage(getString(R.string.loading_coupon_error))

        builder.setPositiveButton(getString(R.string.undertood)) { _, _ -> }

        builder.show()
    }

    private fun buildLongDynamicLink(idProduct: String, productType: Int): Uri {
        val dynamicLinkBuilder = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://www.beneficioslatam.com/?idProduct=$idProduct&productType=$productType&country=${Constants.userProfile?.actualCountry ?: "BO"}"))
            .setDomainUriPrefix("https://beneficioslatam.page.link")
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
            .setIosParameters(DynamicLink.IosParameters.Builder("com.syntepro.boliviaBeneficios").build())
            .buildDynamicLink()

        return dynamicLinkBuilder.uri
    }

    private fun buildShortDynamicLink(longLink: Uri, onComplete: (succes: Boolean, shortLink: Uri?) -> Unit) {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLongLink(longLink)
            .buildShortDynamicLink()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful)
                    onComplete(true, task.result.shortLink)
                else
                    onComplete(false, null)
            }
    }

    private fun shareItem(content: String, imageUri: Uri, dynamicLink: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Intent.EXTRA_TEXT, "$content $dynamicLink")
        intent.putExtra(Intent.EXTRA_STREAM, imageUri)
        intent.type = "image/png"
        startActivity(Intent.createChooser(intent, "Share item via"))
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
}
