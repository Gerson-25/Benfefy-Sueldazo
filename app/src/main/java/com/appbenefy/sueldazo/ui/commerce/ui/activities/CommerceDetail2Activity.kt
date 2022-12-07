package com.appbenefy.sueldazo.ui.commerce.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.base.BaseActivity
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.commerce.model.CommerceDetailRequest
import com.appbenefy.sueldazo.ui.commerce.model.CommerceDetailResponse
import com.appbenefy.sueldazo.ui.commerce.viewModel.CommerceViewModel
import com.appbenefy.sueldazo.ui.coupon.AgencyActivity
import com.appbenefy.sueldazo.ui.coupon.ui.activities.CouponListActivity
import com.appbenefy.sueldazo.utils.Constants
import com.appbenefy.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.activity_commerce_detail2.*
import java.io.File
import java.io.FileOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CommerceDetail2Activity : BaseActivity() {

    private lateinit var commerceViewModel: CommerceViewModel
    private val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)
    private var pm: PackageManager? = null
    private var commerceId: String? = ""
    private var commerceName: String? = ""
    private var commerceImage: String? = ""
    private var branchName: String? = ""
    private var mInstagram: String? = ""
    private var mFacebook: String? = ""
    private var mWhatsApp: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_commerce_detail2)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.mainToolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        commerceViewModel = viewModel(viewModelFactory) {
            observe(commerceDetail, ::handleCommerceDetail)
            failure(failure, ::handleError)
        }

        // Extras
        val extras = intent.extras
        if (extras != null) {
            commerceId = extras.getString("commerceId")
            loadCommerceDetail(commerceId)
            val navigateDetail = extras.getBoolean("navigate", false)
            if (navigateDetail) openCoupons()
        }

        instagram.setOnClickListener { mInstagram?.let { ig -> openURL(ig) } }
        facebook.setOnClickListener { mFacebook?.let { fb -> openURL(fb) } }
        whatsapp.setOnClickListener { mWhatsApp?.let { wa -> openWhatsApp(wa) } }
        phone.setOnClickListener { openCall(commercePhoneId.text.toString()) }
        email.setOnClickListener { Functions.openEmail(this@CommerceDetail2Activity, commerceEmailId.text.toString()) }
        website.setOnClickListener { openWebsite(commerceWebId.text.toString()) }
        share.setOnClickListener { shareContent("${commerceNameId.text} - ${commerceDescriptionId.text}", commerceId) }
        commerceCoupons.setOnClickListener { openCoupons() }
        commerceAgency.setOnClickListener { openAgencies() }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadCommerceDetail(id: String?) {
        val request = CommerceDetailRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            idCommerce = id ?: ""
        )
        commerceViewModel.getCommerceDetail(request)
    }

    private fun handleCommerceDetail(response: BaseResponse<CommerceDetailResponse>?) {
        commerceName = response?.data?.commerceName
        commerceImage = response?.data?.commerceImage
        branchName = response?.data?.commerceBranch
        mInstagram = response?.data?.commerceInstagram
        mFacebook = response?.data?.commerceFacebook
        mWhatsApp = response?.data?.commerceFacebook

        supportActionBar!!.title = response?.data?.commerceName
        Functions.showRoundedImage(response?.data?.commerceImage, commerceImageId)
        commerceNameId.text = response?.data?.commerceName
        commerceCategoryId.text = response?.data?.commerceBranch

        commerceDescriptionId.text = response?.data?.commerceDescription

        commercePhoneId.text = if (response?.data?.commercePhone.isNullOrEmpty()) getString(R.string.not_specidied) else response?.data?.commercePhone
        commerceEmailId.text = if (response?.data?.commerceEmail.isNullOrEmpty()) getString(R.string.not_specidied) else response?.data?.commerceEmail
        commerceWebId.text = if (response?.data?.commerceWebSite.isNullOrEmpty()) getString(R.string.not_specidied) else response?.data?.commerceWebSite
        getStatus(response?.data?.commerceOpeningTime, response?.data?.commerceClosingTime)
    }

    private fun getStatus(startTime: String?, endTime: String?) {
        if (startTime.isNullOrEmpty() || endTime.isNullOrEmpty()) return
        try {
            val time1 = SimpleDateFormat("HH:mm", Locale.US).parse(startTime)
            val calendar1 = Calendar.getInstance()
            calendar1.time = time1!!
            calendar1.add(Calendar.DATE, 1)

            val time2 = SimpleDateFormat("HH:mm", Locale.US).parse(endTime)
            val calendar2 = Calendar.getInstance()
            calendar2.time = time2!!
            calendar2.add(Calendar.DATE, 1)

            formatter.timeZone = TimeZone.getTimeZone(Functions.userTimeZone)
            val currentTime = Calendar.getInstance()
            val dateNow = formatter.format(currentTime.time)
            @SuppressLint("SimpleDateFormat") val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
            val actualDate: Date
            actualDate = try {
                simpleDateFormat.parse(dateNow)!!
            } catch (e: Exception) {
                Date()
            }

            val now = SimpleDateFormat("HH:mm", Locale.US).format(actualDate)
            val d = SimpleDateFormat("HH:mm", Locale.US).parse(now)
            val calendar3 = Calendar.getInstance()
            calendar3.time = d!!
            calendar3.add(Calendar.DATE, 1)
            val x = calendar3.time
            if (x.after(calendar1.time) && x.before(calendar2.time)) {
                scheduleStatusId.text = Functions.fromHtml(String.format(getString(R.string.opened_now2)))
                scheduleId.text = "${time(startTime)} - ${time(endTime)}"
            } else {
                scheduleStatusId.text = Functions.fromHtml(String.format(getString(R.string.closed_now2)))
                scheduleId.text = "${time(startTime)} - ${time(endTime)}"
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun time(time: String): String {
        val partHora = time.split(":")
        return partHora[0] + ":" + partHora[1]
    }

    private fun openWhatsApp(whatsApp: String) {
        val url = "https://api.whatsapp.com/send?phone=$whatsApp"
        try {
            pm?.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            this@CommerceDetail2Activity.startActivity(i)
        } catch (e: PackageManager.NameNotFoundException) {
            Functions.showWarning(this@CommerceDetail2Activity, getString(R.string.wa_not_instaled))
        }
    }

    private fun openCall(number: String?) {
        if (number.isNullOrEmpty()) return
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+$number"))
        this.startActivity(intent)
    }

    private fun openWebsite(url: String?) {
        if (url.isNullOrEmpty() || !url.startsWith("http")) return
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun openURL(url: String) {
        if (!url.startsWith("http")) {
            Functions.showWarning(this@CommerceDetail2Activity, getString(R.string.invalid_url))
            return
        }
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

//    private fun shareAddress(title: String, description: String) {
//        val sendIntent = Intent()
//        val builder = StrictMode.VmPolicy.Builder()
//        StrictMode.setVmPolicy(builder.build())
//        val bmpUri = Functions.getLocalBitmapUri(commerceImageId)
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

    private fun openCoupons() {
        callIntent<CouponListActivity> {
            this.putExtra("commerceId", commerceId)
            this.putExtra("commerceName", commerceName)
        }
    }

    private fun openAgencies() {
        val intent = Intent(this@CommerceDetail2Activity, AgencyActivity::class.java)
        intent.putExtra("commerceId", commerceId)
        intent.putExtra("commerceName", commerceName)
        intent.putExtra("commerceImage", commerceImage)
        intent.putExtra("couponId", "")
        intent.putExtra("provenance", 1)
        startActivity(intent)
    }

    @SuppressLint("SetWorldReadable")
    private fun shareContent(content: String, id: String?) {
        if (id.isNullOrEmpty()) return
        val bitmap = getBitmapFromView(commerceImageId)
        try {
            val file = File(this.externalCacheDir, "commerce.png")
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
            file.setReadable(true, false)
            val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.beneficioslatam.com/?commerce=$id&country=${Constants.userProfile?.actualCountry ?: "BO"}"))
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
                            this@CommerceDetail2Activity,
                            "com.syntepro.appbeneficiosbolivia.provider", // (use your app signature + ".provider" )
                            file
                        )
                        intent.putExtra(Intent.EXTRA_STREAM, imageUri)
                    } else {
                        // Error to get short link
                        intent.putExtra(Intent.EXTRA_TEXT, "$content $dynamicLinkUri")
                        val imageUri = FileProvider.getUriForFile(
                            this@CommerceDetail2Activity,
                            "com.syntepro.appbeneficiosbolivia.provider", // (use your app signature + ".provider" )
                            file
                        )
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
