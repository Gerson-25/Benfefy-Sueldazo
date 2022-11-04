package com.syntepro.sueldazo.ui.shop.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.core.content.FileProvider
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.base.BaseActivity
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.home.model.PurchasedProductsResponse
import com.syntepro.sueldazo.ui.shop.model.GiftDetailRequest
import com.syntepro.sueldazo.ui.shop.model.GiftDetailResponse
import com.syntepro.sueldazo.ui.shop.viewModel.ShopViewModel
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.activity_gift_detail.*
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat

class GiftDetailActivity : BaseActivity() {

    private lateinit var shopViewModel: ShopViewModel
    private val format = DecimalFormat("###,##0.0#")
    private var clientName: String? = ""
    private var giftCode: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_gift_detail)
        this.setFinishOnTouchOutside(false)

        shopViewModel = viewModel(viewModelFactory) {
            observe(giftDetail, ::handleGiftDetail)
            failure(failure, ::handleError)
        }

        val extras = intent.extras
        if (extras != null) {
            val giftId = extras.getString("giftId")
            clientName = extras.getString("clientName")
            giftCode = extras.getString("giftCode")
            loadGiftDetail(giftId)
        }

        closeId.setOnClickListener {
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            this.finish()
        }

        shareGift.setOnClickListener { shareContent("${Constants.userProfile?.names} ${Constants.userProfile?.lastNames} te ha regalado un articulo, ven y disfrutalo en Benefy.", giftCode) }

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

    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        this.finish()
    }

    private fun loadGiftDetail(id: String?) {
        id?.let {
            val request = GiftDetailRequest(
                    country = Constants.userProfile?.actualCountry ?: "BO",
                    language = Functions.getLanguage(),
                    idOrderPresent = it
            )
            shopViewModel.getGiftDetail(request)
        }
    }

    private fun handleGiftDetail(response: BaseResponse<GiftDetailResponse>?) {
        response?.data?.let {
            Functions.showImage(it.urlImage, imageId)
            titleId.text = it.title
            subtitleId.text = it.subTitle
            if (it.idProductType == PurchasedProductsResponse.GIFT_CARD_GIFT) couponPriceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(it.amount)}" else couponPriceId.visibility = View.GONE
            val howExchange = "${getString(R.string.first_gift_detail)} ${clientName?.substringBefore(" ")} ${getString(R.string.second_gift_detail)}"
            howExchangeId.text = howExchange
        }
    }

    @SuppressLint("SetWorldReadable")
    private fun shareContent(content: String, id: String?) {
        val bitmap = getBitmapFromView(imageId)
        try {
            val file = File(this.externalCacheDir, "gift.png")
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
            file.setReadable(true, false)
            val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("https://www.beneficioslatam.com/?couponCodePromotional=$id&country=${Constants.userProfile?.actualCountry ?: "BO"}"))
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
                                    this@GiftDetailActivity,
                                    "com.syntepro.appbeneficiosbolivia.provider",  //(use your app signature + ".provider" )
                                    file)
                            intent.putExtra(Intent.EXTRA_STREAM, imageUri)
                        } else {
                            // Error to get short link
                            intent.putExtra(Intent.EXTRA_TEXT, "$content $dynamicLinkUri")
                            val imageUri = FileProvider.getUriForFile(
                                    this@GiftDetailActivity,
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

}