package com.appbenefy.sueldazo.ui.profile.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.base.BaseActivity
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.entity.service.Category
import com.appbenefy.sueldazo.ui.home.HomeActivity
import com.appbenefy.sueldazo.ui.home.model.CategoryRequest
import com.appbenefy.sueldazo.ui.profile.model.TransactionDetailRequest
import com.appbenefy.sueldazo.ui.profile.model.TransactionDetailResponse
import com.appbenefy.sueldazo.ui.profile.viewModel.ProfileViewModel
import com.appbenefy.sueldazo.utils.Constants
import com.appbenefy.sueldazo.utils.Functions
import com.appbenefy.sueldazo.utils.Helpers
import kotlinx.android.synthetic.gms.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.transactions_filter_dialog.*
import kotlinx.android.synthetic.main.transactions_info_dialog.*
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.util.*

class TransactionsFilterDialog : BaseActivity() {

    private lateinit var profileViewModel: ProfileViewModel
    private var giftCode = ""
    private val mCalendar = Calendar.getInstance()
    private var isLoyalty = false
    private var planType = 0
    private var idPlan = ""
    private var screen = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.transactions_filter_dialog)
        this.setFinishOnTouchOutside(true)

        profileViewModel = viewModel(viewModelFactory) {
            observe(transactionDetail, ::handleTransactionDetail)
            observe(categories, ::handleCategories)
            failure(failure, ::handleError)
        }

        // Extras
        val extras = intent.extras
        if (extras != null) {
            screen = extras.getInt("screen", 0)
        }

        val color = if (screen == 0) getColor(R.color.colorAccent) else getColor(R.color.white)
        container.setBackgroundColor(color)

        val textColor = if (screen == 0) getColor(R.color.white) else getColor(R.color.black)
        startDate.setTextColor(textColor)
        endDate.setTextColor(textColor)
        startDatelabel.setTextColor(textColor)
        endDateLabel.setTextColor(textColor)
        separator.setBackgroundColor(textColor)

        getCategories()

        startDate.setOnClickListener {
            DatePickerDialog(
                this, { _, year, monthOfYear, dayOfMonth ->
                    mCalendar.set(Calendar.YEAR, year)
                    mCalendar.set(Calendar.MONTH, monthOfYear)
                    mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    startDate.setText(Helpers.dateToStr(mCalendar.time, DateFormat.DATE_FIELD))
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(
                    Calendar.DAY_OF_MONTH)
            ).show()
        }

        endDate.setOnClickListener {
            DatePickerDialog(
                this, { _, year, monthOfYear, dayOfMonth ->
                    mCalendar.set(Calendar.YEAR, year)
                    mCalendar.set(Calendar.MONTH, monthOfYear)
                    mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    endDate.setText(Helpers.dateToStr(mCalendar.time, DateFormat.DATE_FIELD))
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(
                    Calendar.DAY_OF_MONTH)
            ).show()
        }

        filter.setOnClickListener {
            val data = Intent()
            data.putExtra("start", startDate.text)
            data.putExtra("end", endDate.text)
            data.putExtra("category", categorySpinner.selectedItem.toString())
            setResult(Activity.RESULT_OK, data)
            finish()
        }

    }

    private fun getCategories(){
        val request = CategoryRequest(
            country = "BO",
            language = 1,
            filterType = 1
        )
        profileViewModel.loadCategories(request)
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

    private fun handleCategories(categories: BaseResponse<List<Category>>?) {
        categories?.data?.let { categoriesList ->
            val layout = if (screen == 0) R.layout.spinner_category_item else R.layout.spinner_item
            val g = categoriesList.map {
                it.name
            }
            categorySpinner.adapter = ArrayAdapter(this, layout, g)
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
                                    this@TransactionsFilterDialog,
                                    "com.syntepro.appbeneficiosbolivia.provider",  //(use your app signature + ".provider" )
                                    file)
                            intent.putExtra(Intent.EXTRA_STREAM, imageUri)
                        } else {
                            // Error to get short link
                            intent.putExtra(Intent.EXTRA_TEXT, "$content $dynamicLinkUri")
                            val imageUri = FileProvider.getUriForFile(
                                    this@TransactionsFilterDialog,
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