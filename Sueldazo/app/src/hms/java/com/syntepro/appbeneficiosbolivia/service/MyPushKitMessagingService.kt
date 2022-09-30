package com.syntepro.appbeneficiosbolivia.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.core.AndroidApplication
import com.syntepro.appbeneficiosbolivia.ui.MainActivity
import com.syntepro.appbeneficiosbolivia.ui.benefy.BenefyDetailActivity
import com.syntepro.appbeneficiosbolivia.ui.general.ExchangeInfoDialog
import com.syntepro.appbeneficiosbolivia.ui.general.SuccessGiftActivity
import com.syntepro.appbeneficiosbolivia.ui.home.HomeActivity
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.Loyalty
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities.SuccessPaymentActivity
import com.syntepro.appbeneficiosbolivia.utils.Constants
import java.util.*

class MyPushKitMessagingService: HmsMessageService() {

    private val notificationManager: NotificationManagerCompat by lazy { NotificationManagerCompat.from(this) }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("PushDemoLog", "receive token:$token")
        sendTokenToDisplay(token)
    }

    private fun sendTokenToDisplay(token: String) { Constants.HUAWEI_TOKEN = token }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        remoteMessage?.let {
            Log.e("Notification Service", "" + remoteMessage.data)
            if ((applicationContext as AndroidApplication).isInForeground()) {
                if (remoteMessage.dataOfMap.isNotEmpty()) {
                    when(remoteMessage.dataOfMap["NotificationType"]?.toInt()) {
                        1 -> {
                            val intent = Intent(this, ExchangeInfoDialog::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("qrCode", remoteMessage.dataOfMap["Code"])
                            intent.putExtra("productId", remoteMessage.dataOfMap["IdCoupon"])
                            intent.putExtra("productType", remoteMessage.dataOfMap["IdProductType"])
                            startActivity(intent)
                        }
                        2 -> {
                            val intent = Intent(this, ExchangeInfoDialog::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("qrCode", remoteMessage.dataOfMap["Code"])
                            intent.putExtra("productId", remoteMessage.dataOfMap["IdProduct"])
                            intent.putExtra("productType", remoteMessage.dataOfMap["IdProductType"])
                            startActivity(intent)
                        }
                        3 -> {
                            val intent = Intent(this, BenefyDetailActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            intent.putExtra("productId", remoteMessage.dataOfMap["IdPurchasedProductIndex"]?.toInt())
                            showCouponAssigned(remoteMessage.notification?.title, remoteMessage.notification?.body, intent)
                        }
                        4 -> {
                            val intent = Intent(this, SuccessPaymentActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        5 -> {
                            val intent = Intent(this, SuccessGiftActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("giftId", remoteMessage.dataOfMap["IdProduct"])
                            intent.putExtra("clientName", remoteMessage.dataOfMap["BeneficiaryName"])
                            intent.putExtra("giftCode", remoteMessage.dataOfMap["Code"])
                            startActivity(intent)
                        }
                        6 -> {
                            val intent = Intent(this, HomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("loyaltyPush", true)
                            intent.putExtra("planType", Loyalty.LOYALTY_PLAN_MILES)
                            intent.putExtra("idPlan", remoteMessage.dataOfMap["IdLoyaltyPlan"])
                            startActivity(intent)
                        }
                        7 -> {
                            val intent = Intent(this, HomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("loyaltyPush", true)
                            intent.putExtra("planType", Loyalty.LOYALTY_PLAN_SEALS)
                            intent.putExtra("idPlan", remoteMessage.dataOfMap["IdLoyaltyPlan"])
                            startActivity(intent)
                        }
                    }
                } else
                    generalNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
            } else {
                if (remoteMessage.data.isEmpty()) {
                    generalNotification(
                        remoteMessage.notification?.title,
                        remoteMessage.notification?.body
                    )
                } else {
                    Log.e("Data", "Notification Type ${remoteMessage.dataOfMap["NotificationType"]?.toInt()}")
                    when {
                        remoteMessage.dataOfMap["NotificationType"]?.toInt() == 3 -> {
                            val intent = Intent(this, BenefyDetailActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            intent.putExtra("productId", remoteMessage.dataOfMap["IdPurchasedProductIndex"]?.toInt())
                            showCouponAssigned(remoteMessage.notification?.title, remoteMessage.notification?.body, intent)
                        }
                        remoteMessage.dataOfMap["NotificationType"]?.toInt() == 6 -> {
                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            intent.putExtra("loyaltyPush", true)
                            intent.putExtra("planType", Loyalty.LOYALTY_PLAN_MILES)
                            intent.putExtra("idPlan", remoteMessage.dataOfMap["IdLoyaltyPlan"])
                            showCouponAssigned(remoteMessage.notification?.title, remoteMessage.notification?.body, intent)
                        }
                        remoteMessage.dataOfMap["NotificationType"]?.toInt() == 7 -> {
                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            intent.putExtra("loyaltyPush", true)
                            intent.putExtra("planType", Loyalty.LOYALTY_PLAN_SEALS)
                            intent.putExtra("idPlan", remoteMessage.dataOfMap["IdLoyaltyPlan"])
                            showCouponAssigned(remoteMessage.notification?.title, remoteMessage.notification?.body, intent)
                        }
                        else -> showNotification(remoteMessage.dataOfMap)
                    }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) notificationCallO(title, body, couponId, qrCode, productType?.toInt() ?: 0) else notificationCall(title, body, couponId, qrCode, productType?.toInt() ?: 0)
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
            getPendingIntent(requestID, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT)
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
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
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
        val notification = NotificationCompat.Builder(this, "channel1")
                .setSmallIcon(R.drawable.ic_benefy)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build()
        notificationManager.notify(Random().nextInt(), notification)
    }

    // API 25 or lower
    private fun notificationCall(title: String?, body: String?, couponId: String?, qrCode: String?, type: Int) {
        val requestID = System.currentTimeMillis().toInt()
        val intent = Intent(this, ExchangeInfoDialog::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
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
                .setSmallIcon(R.drawable.ic_benefy)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_benefy))
                .setContentTitle(title)
                .setContentText(body)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        Objects.requireNonNull(notificationManager).notify(Random().nextInt(), builder.build())
    }

}

