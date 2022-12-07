package com.appbenefy.sueldazo.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.core.AndroidApplication
import com.appbenefy.sueldazo.ui.benefy.BenefyDetailActivity
import com.appbenefy.sueldazo.ui.coupon.ui.activities.RatingActivity
import com.appbenefy.sueldazo.ui.general.ExchangeInfoDialog
import com.appbenefy.sueldazo.ui.general.SuccessGiftActivity
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationManager: NotificationManagerCompat by lazy { NotificationManagerCompat.from(this) }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.e("Notification Service", "" + remoteMessage.data)
        if ((applicationContext as AndroidApplication).isInForeground()) {
            if (remoteMessage.data.isNotEmpty()) {
                when(remoteMessage.data["NotificationType"]?.toInt()) {
                    1 -> {
                        generalNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
                        val intent = Intent(this, RatingActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("qrCode", remoteMessage.data["Code"])
                        intent.putExtra("productId", remoteMessage.data["IdCoupon"])
                        intent.putExtra("productType", remoteMessage.data["IdProductType"])
                        startActivity(intent)
                    }
                    2 -> {
                        generalNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
                        val intent = Intent(this, RatingActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("qrCode", remoteMessage.data["Code"])
                        intent.putExtra("productId", remoteMessage.data["IdProduct"])
                        intent.putExtra("productType", remoteMessage.data["IdProductType"])
                        startActivity(intent)
                    }
                    3 -> {
                        val intent = Intent(this, BenefyDetailActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        intent.putExtra("productId", remoteMessage.data["IdPurchasedProductIndex"]?.toInt())
                        showCouponAssigned(remoteMessage.notification?.title, remoteMessage.notification?.body, intent)
                    }
                    5 -> {
                        val intent = Intent(this, SuccessGiftActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("giftId", remoteMessage.data["IdProduct"])
                        intent.putExtra("clientName", remoteMessage.data["BeneficiaryName"])
                        intent.putExtra("giftCode", remoteMessage.data["Code"])
                        startActivity(intent)
                    }
                }
            } else
                generalNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
        } else {
            if (remoteMessage.data.isEmpty())
                generalNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
            else {
                when {
                    remoteMessage.data["NotificationType"]?.toInt() == 3 -> {
                        val intent = Intent(this, BenefyDetailActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        intent.putExtra("productId", remoteMessage.data["IdPurchasedProductIndex"]?.toInt())
                        showCouponAssigned(remoteMessage.data["Title"], remoteMessage.data["Subtitle"], intent)
                    }
                    else -> showNotification(remoteMessage.data)
                }
            }
        }
    }

    private fun showNotification(data: Map<String, String>) {
        val title = data["title"]
        val body = data["body"]
        val type = data["NotificationType"]
        val couponId = if (type?.toInt() == 1) data["IdCoupon"] else data["IdProduct"]
        val qrCode = data["Code"]
        val productType = data["IdProductType"]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) notificationCallO(title, body, couponId, qrCode, productType?.toInt()
                ?: 0) else notificationCall(title, body, couponId, qrCode, productType?.toInt()
                ?: 0)
    }

    private fun generalNotification(title: String?, body: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = NotificationCompat.Builder(this, "channel1")
                    .setSmallIcon(R.drawable.ic_benefy)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build()
            notificationManager.notify(Random().nextInt(), notification)
        } else {
            val builder = NotificationCompat.Builder(this)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.ic_benefy)
                    .setAutoCancel(true)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_benefy))
                    .setContentTitle(title)
                    .setContentText(body)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(Random().nextInt(), builder.build())
        }
    }

    private fun showCouponAssigned(title: String?, body: String?, resultIntent: Intent) {
        val requestID = System.currentTimeMillis().toInt()
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(requestID, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = NotificationCompat.Builder(this, "channel1")
                    .setContentIntent(resultPendingIntent)
                    .setSmallIcon(R.drawable.ic_benefy)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build()
            notificationManager.notify(Random().nextInt(), notification)
        } else {
            val builder = NotificationCompat.Builder(this)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setContentIntent(resultPendingIntent)
                    .setSmallIcon(R.drawable.ic_benefy)
                    .setAutoCancel(true)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_benefy))
                    .setContentTitle(title)
                    .setContentText(body)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(Random().nextInt(), builder.build())
        }
    }

    // API 26 or higher
    private fun notificationCallO(title: String?, body: String?, couponId: String?, qrCode: String?, type: Int) {
        val requestID = System.currentTimeMillis().toInt()
        val intent = Intent(this, ExchangeInfoDialog::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("qrCode", qrCode)
        intent.putExtra("productId", couponId)
        intent.putExtra("productType", type)
        /*
        * Rodrigo Osegueda 12ENE2020 - Pending Intent modification
        */
        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(intent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(requestID, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val notification = NotificationCompat.Builder(this, "channel1")
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.ic_benefy)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build()
        notificationManager.notify(Random().nextInt(), notification)
    }

    // API 25 or lower
    private fun notificationCall(title: String?, body: String?, couponId: String?, qrCode: String?, type: Int) {
        val requestID = System.currentTimeMillis().toInt()
        val intent = Intent(this, ExchangeInfoDialog::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("qrCode", qrCode)
        intent.putExtra("productId", couponId)
        intent.putExtra("productType", type)
        /*
        * Rodrigo Osegueda 12ENE2020 - Pending Intent modification
        */
        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(intent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(requestID, PendingIntent.FLAG_ONE_SHOT)
        }
        val builder = NotificationCompat.Builder(this)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.ic_benefy)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_benefy))
                .setContentTitle(title)
                .setContentText(body)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random().nextInt(), builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

}