package com.syntepro.appbeneficiosbolivia.ui.notifications.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonParser
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.benefy.BenefyDetailActivity
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.activities.RatingActivity
import com.syntepro.appbeneficiosbolivia.ui.general.SuccessGiftActivity
import com.syntepro.appbeneficiosbolivia.ui.home.HomeActivity
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.Loyalty
import com.syntepro.appbeneficiosbolivia.ui.notifications.model.NotificationRequest
import com.syntepro.appbeneficiosbolivia.ui.notifications.model.NotificationResponse
import com.syntepro.appbeneficiosbolivia.ui.notifications.model.ReadNotificationRequest
import com.syntepro.appbeneficiosbolivia.ui.notifications.ui.adapter.NotificationsAdapter
import com.syntepro.appbeneficiosbolivia.ui.notifications.viewModel.NotificationViewModel
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.activity_notifications.*
import java.util.*
import javax.inject.Inject

class NotificationsActivity : BaseActivity(){

    @Inject
    lateinit var notificationsAdapter: NotificationsAdapter

    private lateinit var notificationViewModel: NotificationViewModel
    private var page: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_notifications)

        // Toolbar
        val myToolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.notificaciones_noti)

        notificationViewModel = viewModel(viewModelFactory) {
            observe(notification, ::handleNotifications)
            observe(readNotification, ::handleReadNotification)
            failure(failure, ::handleError)
        }

        nestedScroll.setOnScrollChangeListener { v: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            val nestedScrollView = checkNotNull(v) {
                return@setOnScrollChangeListener
            }
            val lastChild = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)
            if (lastChild != null) {
                if ((scrollY >= (lastChild.measuredHeight - nestedScrollView.measuredHeight)) && scrollY > oldScrollY) {
                    //get more items
                    page++
                    loadNotifications()
                }
            }
        }

        initList()
        loadNotifications()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun initList() {
        listId.setHasFixedSize(true)
        listId.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        listId.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        listId.adapter = notificationsAdapter
        notificationsAdapter.setActivity(this)
    }

    private fun loadNotifications() {
        showProgress(true)
        val request = NotificationRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                recordsNumber = Constants.LIST_PAGE_SIZE,
                pageNumber = page,
                idUser = Constants.userProfile?.idUser ?: ""
        )
        notificationViewModel.getNotification(request)
    }

    private fun handleNotifications(response: BaseResponse<List<NotificationResponse>>?) {
        showProgress(false)
        response?.data?.let {
            if (page > 1) {
                val temp = notificationsAdapter.collection
                val full = merge(temp, it)
                notificationsAdapter.collection = full
            } else
                notificationsAdapter.collection = it
        }

        if(response?.data.isNullOrEmpty() && page == 1) showEmptyLayout(true) else showEmptyLayout(false)
    }

    private fun handleReadNotification(response: BaseResponse<Boolean>?) {
        Log.e("Updated", "${response?.data}")
        response?.data?.let {
            if (it) {
                page = 1
                loadNotifications()
            }
        }
    }

    private fun <T> merge(first: List<T>, second: List<T>): List<T> {
        val list: MutableList<T> = ArrayList(first)
        list.addAll(second)
        return list
    }

    fun readNotification(notificationId: String) {
        val request = ReadNotificationRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                idNotificationPush = notificationId
        )
        notificationViewModel.readNotification(request)
    }

    fun openNotification(payload: String) {
        if (Functions.isJSONValid(payload)) {
            val json = JsonParser().parse(payload)
            val objectJSON = json.asJsonObject
            when (objectJSON.get("NotificationType").asInt) {
                3 -> {
                    val productIndex = objectJSON.get("IdPurchasedProductIndex")?.asInt
                    callIntent<BenefyDetailActivity> {
                        this.putExtra("productId", productIndex)
                    }
                }
                4 -> {
                    Log.e("Notification", "Read")
                }
                5 -> {
                    val id = objectJSON.get("IdProduct").asString
                    val name = objectJSON.get("BeneficiaryName").asString
                    val code = objectJSON.get("Code").asString
                    callIntent<SuccessGiftActivity> {
                        this.putExtra("giftId", id)
                        this.putExtra("clientName", name)
                        this.putExtra("giftCode", code)
                    }
                }
                6 -> {
                    val idPlan = objectJSON.get("IdLoyaltyPlan").asString
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("loyaltyPush", true)
                    intent.putExtra("planType", Loyalty.LOYALTY_PLAN_MILES)
                    intent.putExtra("idPlan", idPlan)
                    startActivity(intent)
//                    callIntent<HomeActivity> {
//                        this.putExtra("loyaltyPush", true)
//                        this.putExtra("planType", Loyalty.LOYALTY_PLAN_MILES)
//                        this.putExtra("idPlan", idPlan)
//                    }
                }
                7 -> {
                    val idPlan = objectJSON.get("IdLoyaltyPlan").asString
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("loyaltyPush", true)
                    intent.putExtra("planType", Loyalty.LOYALTY_PLAN_MILES)
                    intent.putExtra("idPlan", idPlan)
                    startActivity(intent)
//                    callIntent<HomeActivity> {
//                        this.putExtra("loyaltyPush", true)
//                        this.putExtra("planType", Loyalty.LOYALTY_PLAN_SEALS)
//                        this.putExtra("idPlan", idPlan)
//                    }
                }
                else -> {
                    val code = objectJSON.get("Code").asString
                    val id = if (objectJSON.get("IdCoupon") == null) objectJSON.get("IdProduct").asString else objectJSON.get("IdCoupon").asString
                    val type = objectJSON.get("IdProductType")?.asInt
                    callIntent<RatingActivity>  {
                        this.putExtra("qrCode", code)
                        this.putExtra("couponId", id)
                        this.putExtra("type", type)
                    }
                }
            }
        }
    }

}