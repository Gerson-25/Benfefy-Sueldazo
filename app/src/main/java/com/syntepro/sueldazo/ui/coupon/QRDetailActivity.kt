package com.syntepro.sueldazo.ui.coupon

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.squareup.picasso.Picasso
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.entity.firebase.Cupon
import com.syntepro.sueldazo.ui.coupon.ui.activities.RatingActivity
import com.syntepro.sueldazo.ui.general.ExchangeInfoDialog
import com.syntepro.sueldazo.utils.NotificationCenter
import kotlinx.android.synthetic.main.activity_q_r_detail.*
import java.io.ByteArrayOutputStream
import java.util.*

class QRDetailActivity : AppCompatActivity() {

    private lateinit var coupon: Cupon
    private var active = false
    private var notificationManager: NotificationManagerCompat? = null
    private var runnable: Runnable? = null
    private var shareUri: Uri? = null
    private var extrasCouponId: String? = ""
    private var extrasCode: String? = ""
    var documentID: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_q_r_detail)

        // Toolbar
        val myToolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            coupon = extras.getSerializable("modelCoupon") as Cupon
            extrasCouponId = extras.getString("categoryid")
            extrasCode = extras.getString("qrCode")
            supportActionBar!!.title = coupon.nombreComercio
            // Show Data
            readCouponCustomObject(coupon)
            getQR()
        }

        // Instance
        notificationManager = NotificationManagerCompat.from(this)

        runnable = Runnable {
//            val dialog = Dialog(this@QRDetailActivity)
//            dialog.setContentView(R.layout.custom_popup_exito)
//            val closePopup = dialog.findViewById<ImageView>(R.id.close)
//            val accept = dialog.findViewById<Button>(R.id.btn_aceptar)
//
//            closePopup.setOnClickListener {
//                dialog.dismiss()
//                val intent = Intent(this@QRDetailActivity, RatingActivity::class.java)
//                intent.putExtra("qrCode", documentID)
//                intent.putExtra("couponId", extrasCouponId)
//                startActivity(intent)
//                finish()
//            }
//
//            accept.setOnClickListener {
//                dialog.dismiss()
//                NotificationCenter.defaultCenter().removeFunctionForNotification("showPopupCanje", runnable)
//                val intent = Intent(this@QRDetailActivity, RatingActivity::class.java)
//                intent.putExtra("qrCode", documentID)
//                intent.putExtra("couponId", extrasCouponId)
//                startActivity(intent)
//                finish()
//            }

            // Modification Popup Activity Active
            when {
                active -> {
//                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                    dialog.setCancelable(false)
//                    dialog.show()
                    val intent = Intent(this@QRDetailActivity, ExchangeInfoDialog::class.java)
                    intent.putExtra("qrCode", documentID)
                    intent.putExtra("productId", extrasCouponId)
                    startActivityForResult(intent, 928)
//                    CustomDialog.display(supportFragmentManager, documentID, extrasCouponId)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> notificationCallO()
                else -> notificationCall()
            }
        }

        shareWA.setOnClickListener {
            if (this::coupon.isInitialized) shareQR(coupon)
            else Log.e("Error", "Intentelo de nuevo.")
        }

        // Validate
        NotificationCenter.defaultCenter()?.verifyNotificationExists("showPopupCanje")

        // Notification
        NotificationCenter.defaultCenter()?.addFunctionForNotification("showPopupCanje", runnable!!)
    }

    override fun onStart() {
        super.onStart()
        active = true
    }

    override fun onStop() {
        super.onStop()
        active = false
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        finish()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 928 && resultCode == Activity.RESULT_OK) {
            finish()
        }
    }

    private fun readCouponCustomObject(coupon: Cupon) {
        titleId.text = coupon.titulo
        subtitleId.text = coupon.subtitulo
        termsId.text = coupon.tyc
        Picasso.get()
                .load(coupon.imagenComercio)
                .fit()
                .centerInside()
                .error(R.drawable.notfound)
                .into(commerceImage)
    }

    private fun getQR() {
        documentID = extrasCode
        qrCodeId.text = extrasCode
        generateQR("https://www.beneficioslatam.com/canje/$extrasCode")
        addRealtimeUpdate(extrasCode)
    }

    private fun generateQR(qr: String) {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(qr, BarcodeFormat.QR_CODE, 300, 300)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            img_qr.setImageBitmap(bitmap)
            shareUri = getImageUri(this, bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun addRealtimeUpdate(hash: String?) {
        val docRef = FirebaseFirestore.getInstance().collection("Codigo").document(hash!!)
        docRef.addSnapshotListener { snapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
            if (e != null) {
                Log.w("Listener", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                if (Objects.requireNonNull(snapshot["estado"]) == "1") successMessage()
            }
        }
    }

    private fun successMessage() {
        NotificationCenter.defaultCenter()?.postNotification("showPopupCanje")
    }

    private fun shareQR(model: Cupon) {
        shareUri?.let {
            val whatsAppIntent = Intent(Intent.ACTION_SEND)
            whatsAppIntent.type = "text/plain"
            whatsAppIntent.setPackage("com.whatsapp")
            whatsAppIntent.putExtra(Intent.EXTRA_TEXT, "Disfruta de este descuento con tu código $extrasCode")
            whatsAppIntent.putExtra(Intent.EXTRA_STREAM, shareUri)
            whatsAppIntent.type = "image/jpeg"
            whatsAppIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                startActivity(whatsAppIntent)
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(this, "No tienes WhatsApp instalado.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        return try {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "IMG_QR", null)
            Uri.parse(path)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Notifications fro API 26 or higher
    private fun notificationCallO() {
        val pendingIntent = PendingIntent.getActivity(this, 1, ratingRedirect(), PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(this, "channel1")
                .setSmallIcon(R.drawable.ic_benefy)
                .setContentTitle(getString(R.string.canje))
                .setContentText(getString(R.string.app_beneficios_notification))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build()
        notificationManager!!.notify(3, notification)
    }

    // Notifications for API 25 or lower
    private fun notificationCall() {
        val pendingIntent = PendingIntent.getActivity(this, 1, ratingRedirect(), PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_benefy)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_benefy))
                .setContentTitle(getString(R.string.canje))
                .setContentText(getString(R.string.app_beneficios_notification))
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        Objects.requireNonNull(notificationManager).notify(3, builder.build())
    }

    private fun ratingRedirect(): Intent {
        val intent = Intent(this@QRDetailActivity, RatingActivity::class.java)
        intent.putExtra("qrCode", documentID)
        intent.putExtra("couponId", extrasCouponId)
        return intent
    }

}
