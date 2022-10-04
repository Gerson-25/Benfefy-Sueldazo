package com.syntepro.appbeneficiosbolivia.ui.profile.ui.activities

import android.annotation.SuppressLint
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
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.home.HomeActivity
import com.syntepro.appbeneficiosbolivia.ui.profile.model.TransactionDetailRequest
import com.syntepro.appbeneficiosbolivia.ui.profile.model.TransactionDetailResponse
import com.syntepro.appbeneficiosbolivia.ui.profile.viewModel.ProfileViewModel
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.utils.Helpers
import kotlinx.android.synthetic.main.transactions_info_dialog.*
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat

class TransactionsInfoDialog : BaseActivity() {

    private lateinit var profileViewModel: ProfileViewModel
    private var giftCode = ""
    private var isLoyalty = false
    private var planType = 0
    private var idPlan = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.transactions_info_dialog)
        this.setFinishOnTouchOutside(true)

        profileViewModel = viewModel(viewModelFactory) {
            observe(transactionDetail, ::handleTransactionDetail)
            failure(failure, ::handleError)
        }

        // Extras
        val extras = intent.extras
        if (extras != null) {
            val id = extras.getString("id", "")
            loadTransactionDetail(id)
        }

        shareGift.setOnClickListener {
            if (isLoyalty) {
                val intent = Intent(this, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("loyaltyPush", true)
                intent.putExtra("planType", planType)
                intent.putExtra("idPlan", idPlan)
                startActivity(intent)
            } else
                shareContent("${Constants.userProfile?.names} ${Constants.userProfile?.lastNames} te ha regalado un articulo, ven y disfrutalo en Benefy.", giftCode)
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

    private fun loadTransactionDetail(id: String) {
        val request = TransactionDetailRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                idTransaction = id
        )
        profileViewModel.getTransactionDetail(request)
    }

    private fun handleTransactionDetail(response: BaseResponse<TransactionDetailResponse>?) {
        response?.data?.let {
            dateTransactionId.text = Functions.fromHtml(String.format(getString(R.string.transaction_date_label), Helpers.dateToStr(it.transactionDate, DateFormat.LONG)))
            commerceId.text = Functions.fromHtml(String.format(getString(R.string.gift_detail_commerce_label), it.commerceName))
            when (it.idTransactionType) {
                2 -> {
                    isLoyalty = true
                    shareGift.visibility = View.VISIBLE
                    shareGift.text = "Ver plan"
                    idPlan = it.idReference ?: ""
                    dedicatoryId.text = Functions.fromHtml(String.format(getString(R.string.stamp_description_label), it.description))
                }
                3 -> {
                    isLoyalty = true
                    shareGift.visibility = View.VISIBLE
                    shareGift.text = "Ver plan"
                    idPlan = it.idReference ?: ""
                    dedicatoryId.text = Functions.fromHtml(String.format(getString(R.string.stamp_description_label), it.description))
                }
                9 -> {
                    giftCode = it.giftCode
                    shareGift.visibility = View.VISIBLE
                    article.visibility = View.VISIBLE
                    articleId.text = Functions.fromHtml(String.format(getString(R.string.gift_detail_article_label), it.articleName))
                    from.visibility = View.VISIBLE
                    fromId.text = Functions.fromHtml(String.format(getString(R.string.from_label), it.userGivesName))
                    to.visibility = View.VISIBLE
                    toId.text = Functions.fromHtml(String.format(getString(R.string.to_label), it.userReceivesName))
                    dedicatoryId.text = Functions.fromHtml(String.format(getString(R.string.gift_detail_dedication_label), it.description))
                }
                10 -> {
                    shareGift.visibility = View.GONE
                    article.visibility = View.VISIBLE
                    articleId.text = Functions.fromHtml(String.format(getString(R.string.gift_detail_article_label), it.articleName))
                    from.visibility = View.VISIBLE
                    fromId.text = Functions.fromHtml(String.format(getString(R.string.from_label), it.userGivesName))
                    to.visibility = View.VISIBLE
                    toId.text = Functions.fromHtml(String.format(getString(R.string.to_label), it.userReceivesName))
                    dedicatoryId.text = Functions.fromHtml(String.format(getString(R.string.gift_detail_dedication_label), it.description))
                }
                else -> {
                    shareGift.visibility = View.GONE
                    dedicatoryId.text = Functions.fromHtml(String.format(getString(R.string.stamp_description_label), it.description))
                }
            }
        }
    }

    @SuppressLint("SetWorldReadable")
    private fun shareContent(content: String, id: String?) {
//        val bitmap = getBitmapFromView(imageId)
        try {
            val file = File(this.externalCacheDir, "gift.png")
            val fOut = FileOutputStream(file)
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
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
                            val shortLink = task.result.shortLink
                            intent.putExtra(Intent.EXTRA_TEXT, "$content $shortLink")
                            val imageUri = FileProvider.getUriForFile(
                                    this@TransactionsInfoDialog,
                                    "com.syntepro.appbeneficiosbolivia.provider",  //(use your app signature + ".provider" )
                                    file)
                            intent.putExtra(Intent.EXTRA_STREAM, imageUri)
                        } else {
                            // Error to get short link
                            intent.putExtra(Intent.EXTRA_TEXT, "$content $dynamicLinkUri")
                            val imageUri = FileProvider.getUriForFile(
                                    this@TransactionsInfoDialog,
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