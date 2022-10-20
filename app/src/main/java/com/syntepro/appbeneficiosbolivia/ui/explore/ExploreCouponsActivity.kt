package com.syntepro.appbeneficiosbolivia.ui.explore

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.entity.firebase.Cupon
import com.syntepro.appbeneficiosbolivia.entity.service.VipCoupon
import com.syntepro.appbeneficiosbolivia.entity.service.VipCouponsRequest
import com.syntepro.appbeneficiosbolivia.service.NetworkService
import com.syntepro.appbeneficiosbolivia.ui.commerce.ui.adapters.CustomSearchAdapter
import com.syntepro.appbeneficiosbolivia.ui.coupon.adapter.VipCouponAdapter
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.activities.CouponDetail2Activity
import com.syntepro.appbeneficiosbolivia.ui.explore.adapter.CardAdapter
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.showError
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.userCountry
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.userSession
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.userUID
import com.syntepro.appbeneficiosbolivia.utils.PreCachingLayoutManagerHelper
import kotlinx.android.synthetic.main.activity_explore_coupons.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.util.*

class ExploreCouponsActivity: BaseActivity() {

    private var dialog: Dialog? = null
    private var cardAdapter: CardAdapter? = null
    private var vipCouponAdapter: VipCouponAdapter? = null
    private var cardList: ArrayList<Cupon> = ArrayList()
    private var dataList: ArrayList<VipCoupon> = ArrayList()
    private var recyclerView: RecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var commerceCoupon: CardView? = null
    private var vipCoupon: CardView? = null
    private var optionId: String? = null
    private var nameId: String? = null
    private val fav = false
    private var searchView: SearchView? = null
    private var mAdapter: CustomSearchAdapter? = null
    private var generalCardText: TextView? = null
    private var vipCardText: TextView? = null
    private var activeVIP = false
    private var count = 0.0000
    private val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_coupons)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        // Views
        swipeRefreshLayout = findViewById(R.id.swipeExplore)
        commerceCoupon = findViewById(R.id.commerceCouponExplore)
        vipCoupon = findViewById(R.id.vipCouponExplore)
        generalCardText = findViewById(R.id.general)
        vipCardText = findViewById(R.id.vip)
        dialog = Dialog(this@ExploreCouponsActivity)
        dialog!!.setContentView(R.layout.custom_loader)
        recyclerView = findViewById(R.id.rvExplore)

        // Parameters
        val extras = intent.extras
        if (extras != null) {
            optionId = extras.getString("opcionid")
            nameId = extras.getString("categoryiname")
        }

        // RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.isNestedScrollingEnabled = true
        recyclerView!!.setItemViewCacheSize(Constants.PAGE_SIZE * 2)
        val rvLiLayoutManager = PreCachingLayoutManagerHelper(this)
        rvLiLayoutManager.orientation = LinearLayoutManager.VERTICAL
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        rvLiLayoutManager.setExtraLayoutSpace(metrics.heightPixels * 3)
        recyclerView!!.layoutManager = rvLiLayoutManager

        // Adapter
        cardAdapter = CardAdapter(cardList) { item -> cardAction(item) }
        vipCouponAdapter = VipCouponAdapter(dataList) { item -> cardVipAction(item) }

        val title = findViewById<TextView>(R.id.toolbar_title)
        title.text = nameId

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(true)
        dialog!!.show()

        dialog!!.setOnCancelListener { dialog: DialogInterface ->
            finish()
            dialog.dismiss()
        }

        swipeRefreshLayout!!.setOnRefreshListener {
            if (activeVIP) {
                showEmptyLayout(false)
                commerceCoupon!!.isEnabled = true
                vipCoupon!!.isEnabled = false
                generalCardText!!.setBackgroundColor(resources.getColor(R.color.loader))
                vipCardText!!.setBackgroundColor(resources.getColor(R.color.colorPrimaryLigth))
                recyclerView!!.adapter = vipCouponAdapter
                cardList.clear()
                dataList.clear()
                when (optionId) {
                    "2" -> {
                        aprovechaYaVIP()
                    }
                    "3" -> {
                        loNuevoVIP()
                    }
                    "4" -> {
                        mejoresDescuentosVIP()
                    }
                }
            } else {
                showEmptyLayout(false)
                commerceCoupon!!.isEnabled = false
                vipCoupon!!.isEnabled = true
                generalCardText!!.setBackgroundColor(resources.getColor(R.color.colorPrimaryLigth))
                vipCardText!!.setBackgroundColor(resources.getColor(R.color.loader))
                recyclerView!!.adapter = cardAdapter
                cardList.clear()
                dataList.clear()
                when (optionId) {
                    "2" -> {
                        aprovechaYa()
                    }
                    "3" -> {
                        loNuevo()
                    }
                    "4" -> {
                        mejoresDescuentos()
                    }
                }
            }
        }

        commerceCoupon!!.setOnClickListener {
            activeVIP = false
            showEmptyLayout(false)
            commerceCoupon!!.isEnabled = false
            vipCoupon!!.isEnabled = true
            generalCardText!!.setBackgroundColor(resources.getColor(R.color.colorPrimaryLigth))
            vipCardText!!.setBackgroundColor(resources.getColor(R.color.loader))
            recyclerView!!.adapter = cardAdapter
            cardList.clear()
            dataList.clear()
            when (optionId) {
                "2" -> {
                    aprovechaYa()
                }
                "3" -> {
                    loNuevo()
                }
                "4" -> {
                    mejoresDescuentos()
                }
            }
        }

        vipCoupon!!.setOnClickListener {
            activeVIP = true
            showEmptyLayout(false)
            commerceCoupon!!.isEnabled = true
            vipCoupon!!.isEnabled = false
            generalCardText!!.setBackgroundColor(resources.getColor(R.color.loader))
            vipCardText!!.setBackgroundColor(resources.getColor(R.color.colorPrimaryLigth))
            recyclerView!!.adapter = vipCouponAdapter
            cardList.clear()
            dataList.clear()
            when (optionId) {
                "2" -> {
                    aprovechaYaVIP()
                }
                "3" -> {
                    loNuevoVIP()
                }
                "4" -> {
                    mejoresDescuentosVIP()
                }
            }
        }

        general.setOnClickListener { commerceCouponExplore.performClick() }
        vip.setOnClickListener { vipCouponExplore.performClick() }

        mAdapter = CustomSearchAdapter(this, null)

        // Show Data
        initData()

        // Temp
//        recyclerView!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
////                timer.cancel()
////                timer.purge()
//                recyclerView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
//            }
//        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        recyclerView!!.adapter = null
        searchView = null
        super.onDestroy()
    }

    private fun initData() {
        activeVIP = false
        showEmptyLayout(false)
        commerceCoupon!!.isEnabled = false
        vipCoupon!!.isEnabled = true
        generalCardText!!.setBackgroundColor(resources.getColor(R.color.colorPrimaryLigth))
        vipCardText!!.setBackgroundColor(resources.getColor(R.color.loader))
        cardList.clear()
        dataList.clear()
        recyclerView!!.adapter = cardAdapter
        when (optionId) {
            "2" -> {
                aprovechaYa()
            }
            "3" -> {
                loNuevo()
            }
            "4" -> {
                mejoresDescuentos()
            }
        }
    }

    private fun aprovechaYa() {
        val aprovecha: Call<ArrayList<Cupon>> = getRetrofit()!!.create(NetworkService::class.java).getAprovecha(userCountry)
        aprovecha.enqueue(object : Callback<ArrayList<Cupon>> {
            override fun onResponse(call: Call<ArrayList<Cupon>>, response: Response<ArrayList<Cupon>>) {
                if (response.code() == 200) {
                    val ap = response.body()
                    if (ap!!.isEmpty()) {
                        showEmptyLayout(true)
                        dialog!!.dismiss()
                        swipeRefreshLayout!!.isRefreshing = false
                    } else {
                        showEmptyLayout(false)
                        val format = DecimalFormat("###,###.00")
                        val m = userSession.moneda
                        for (c in ap) {
                            var realPrice = "null"
                            var descPrice = "null"
                            val descuentoCode: String? = if (c.fbCodeType == null || c.fbCodeType == "") "1"
                                else c.fbCodeType
                            if (descuentoCode == null || descuentoCode == "" || descuentoCode == "1") {
                                val real = c.precioReal.toDouble()
                                realPrice = Html.fromHtml("<strike>" + m + " " + format.format(real) + "</strike>").toString()
                                val descuento = c.precioDesc.toDouble()
                                descPrice = m + " " + format.format(descuento)
                            } else if (descuentoCode == "2") {
                                realPrice = ""
                                val descuento = c.precioDesc.toDouble()
                                descPrice = format.format(descuento) + " %"
                            } else if (descuentoCode == "3") {
                                realPrice = ""
                                val descuento = c.precioDesc.toDouble()
                                descPrice = m + " " + format.format(descuento)
                            }
                            cardList.add(Cupon(c.idCategoria, c.titulo, c.subtitulo, descPrice, realPrice, c.descripcion, c.tyc, c.cantCanje, c.cantCanjeUSER, c.fechaInicio, c.fechaFin, c.imagenCupon, c.imagenComercio, c.nombreComercio, c.idComercio, c.whatsapp, c.instagram, c.facebook, c.pais, descuentoCode, fav))
                        }
                        cardAdapter!!.notifyDataSetChanged()
                        dialog!!.dismiss()
                        swipeRefreshLayout!!.isRefreshing = false
                    }
                } else {
                    showEmptyLayout(true)
                    dialog!!.dismiss()
                    swipeRefreshLayout!!.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<ArrayList<Cupon>>, t: Throwable) {
                showError(this@ExploreCouponsActivity, getString(R.string.error_connection), "")
                showEmptyLayout(true)
                dialog!!.dismiss()
                swipeRefreshLayout!!.isRefreshing = false
            }
        })
    }

    private fun aprovechaYaVIP() {
        val exploreWS = VipCouponsRequest()
        exploreWS.pais = userCountry
        exploreWS.idUsuario = userUID
        exploreWS.texto = ""
        exploreWS.esBusqueda = true
        val call: Call<ArrayList<VipCoupon>> = getRetrofit()!!.create(NetworkService::class.java).getAprovechaVIP(exploreWS)
        call.enqueue(object : Callback<ArrayList<VipCoupon>> {
            override fun onResponse(call: Call<ArrayList<VipCoupon>>, response: Response<ArrayList<VipCoupon>>) {
                if (response.code() == 200) {
                    val ap = response.body()
                    if (ap!!.isEmpty()) {
                        showEmptyLayout(true)
                        dialog!!.dismiss()
                        swipeRefreshLayout!!.isRefreshing = false
                    } else {
                        showEmptyLayout(false)
                        for (vip in ap) {
                            val vipCouponArray = VipCoupon()
                            vipCouponArray.idCampana = vip.idCampana
                            vipCouponArray.titulo = vip.titulo
                            vipCouponArray.subtitulo = vip.subtitulo
                            vipCouponArray.precioReal = vip.precioReal
                            vipCouponArray.precioDescuento = vip.precioDescuento
                            vipCouponArray.idComercio = vip.idComercio
                            vipCouponArray.urlImagenCampana = vip.urlImagenCampana
                            vipCouponArray.urlImagenComercio = vip.urlImagenComercio
                            vipCouponArray.nombrePlan = vip.nombrePlan
                            vipCouponArray.idPlanLealtad = vip.idPlanLealtad
                            vipCouponArray.isUsuarioActivo = vip.isUsuarioActivo
                            vipCouponArray.fbCodeType = vip.fbCodeType
                            dataList.add(vipCouponArray)
                        }
                        vipCouponAdapter!!.notifyDataSetChanged()
                        dialog!!.dismiss()
                        swipeRefreshLayout!!.isRefreshing = false
                    }
                } else {
                    showEmptyLayout(true)
                    dialog!!.dismiss()
                    swipeRefreshLayout!!.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<ArrayList<VipCoupon>>, t: Throwable) {
                showError(this@ExploreCouponsActivity, getString(R.string.error_connection), "")
                showEmptyLayout(true)
                dialog!!.dismiss()
                swipeRefreshLayout!!.isRefreshing = false
            }
        })
    }

    private fun loNuevo() {
        val nuevo: Call<ArrayList<Cupon>> = getRetrofit()!!.create(NetworkService::class.java).getNuevos(userCountry)
        nuevo.enqueue(object : Callback<ArrayList<Cupon>> {
            override fun onResponse(call: Call<ArrayList<Cupon>>, response: Response<ArrayList<Cupon>>) {
                if (response.code() == 200) {
                    val ap = response.body()
                    if (ap!!.isEmpty()) {
                        showEmptyLayout(true)
                        dialog!!.dismiss()
                        swipeRefreshLayout!!.isRefreshing = false
                    } else {
                        showEmptyLayout(false)
                        val format = DecimalFormat("###,###.00")
                        val m = userSession.moneda
                        for (c in ap) {
                            var realPrice = "null"
                            var descPrice = "null"
                            var descuentoCode: String? = if (c.fbCodeType == null || c.fbCodeType == "") {
                                "1"
                            } else {
                                c.fbCodeType
                            }
                            if (descuentoCode == null || descuentoCode == "" || descuentoCode == "1") {
                                val real = c.precioReal.toDouble()
                                realPrice = Html.fromHtml("<strike>" + m + " " + format.format(real) + "</strike>").toString()
                                val descuento = c.precioDesc.toDouble()
                                descPrice = m + " " + format.format(descuento)
                            } else if (descuentoCode == "2") {
                                realPrice = ""
                                val descuento = c.precioDesc.toDouble()
                                descPrice = format.format(descuento) + " %"
                            } else if (descuentoCode == "3") {
                                realPrice = ""
                                val descuento = c.precioDesc.toDouble()
                                descPrice = m + " " + format.format(descuento)
                            }
                            cardList!!.add(Cupon(c.idCategoria, c.titulo, c.subtitulo, descPrice, realPrice, c.descripcion, c.tyc, c.cantCanje, c.cantCanjeUSER, c.fechaInicio, c.fechaFin, c.imagenCupon, c.imagenComercio, c.nombreComercio, c.idComercio, c.whatsapp, c.instagram, c.facebook, c.pais, descuentoCode, fav))
                        }
                        cardAdapter!!.notifyDataSetChanged()
                        dialog!!.dismiss()
                        swipeRefreshLayout!!.isRefreshing = false
                    }
                } else {
                    showEmptyLayout(true)
                    dialog!!.dismiss()
                    swipeRefreshLayout!!.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<ArrayList<Cupon>>, t: Throwable) {
                showError(this@ExploreCouponsActivity, getString(R.string.error_connection), "")
                showEmptyLayout(true)
                dialog!!.dismiss()
                swipeRefreshLayout!!.isRefreshing = false
            }
        })
    }

    private fun loNuevoVIP() {
        val exploreWS = VipCouponsRequest()
        exploreWS.pais = userCountry
        exploreWS.idUsuario = userUID
        exploreWS.texto = ""
        exploreWS.esBusqueda = true
        val call: Call<ArrayList<VipCoupon>> = getRetrofit()!!.create(NetworkService::class.java).getNuevosVIP(exploreWS)
        call.enqueue(object : Callback<ArrayList<VipCoupon>> {
            override fun onResponse(call: Call<ArrayList<VipCoupon>>, response: Response<ArrayList<VipCoupon>>) {
                if (response.code() == 200) {
                    val ap = response.body()
                    if (ap!!.isEmpty()) {
                        showEmptyLayout(true)
                        dialog!!.dismiss()
                        swipeRefreshLayout!!.isRefreshing = false
                    } else {
                        showEmptyLayout(false)
                        for (vip in ap) {
                            val vipCouponArray = VipCoupon()
                            vipCouponArray.idCampana = vip.idCampana
                            vipCouponArray.titulo = vip.titulo
                            vipCouponArray.subtitulo = vip.subtitulo
                            vipCouponArray.precioReal = vip.precioReal
                            vipCouponArray.precioDescuento = vip.precioDescuento
                            vipCouponArray.idComercio = vip.idComercio
                            vipCouponArray.urlImagenCampana = vip.urlImagenCampana
                            vipCouponArray.urlImagenComercio = vip.urlImagenComercio
                            vipCouponArray.nombrePlan = vip.nombrePlan
                            vipCouponArray.idPlanLealtad = vip.idPlanLealtad
                            vipCouponArray.isUsuarioActivo = vip.isUsuarioActivo
                            vipCouponArray.fbCodeType = vip.fbCodeType
                            dataList!!.add(vipCouponArray)
                        }
                        vipCouponAdapter!!.notifyDataSetChanged()
                        dialog!!.dismiss()
                        swipeRefreshLayout!!.isRefreshing = false
                    }
                } else {
                    showEmptyLayout(true)
                    dialog!!.dismiss()
                    swipeRefreshLayout!!.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<ArrayList<VipCoupon>>, t: Throwable) {
                showError(this@ExploreCouponsActivity, getString(R.string.error_connection), "")
                showEmptyLayout(true)
                dialog!!.dismiss()
                swipeRefreshLayout!!.isRefreshing = false
            }
        })
    }

    private fun mejoresDescuentos() {
        val mejor: Call<ArrayList<Cupon>> = getRetrofit()!!.create(NetworkService::class.java).getMejores(userCountry)
        mejor.enqueue(object : Callback<ArrayList<Cupon>> {
            override fun onResponse(call: Call<ArrayList<Cupon>>, response: Response<ArrayList<Cupon>>) {
                if (response.code() == 200) {
                    val task = object : TimerTask() {
                        override fun run() {
                            count += 0.0001
                        }
                    }
                    timer.schedule(task, 0)
                    val ap = response.body()
                    if (ap!!.isEmpty()) {
                        showEmptyLayout(true)
                        dialog!!.dismiss()
                        swipeRefreshLayout!!.isRefreshing = false
                    } else {
                        showEmptyLayout(false)
                        val format = DecimalFormat("###,###.00")
                        val m = userSession.moneda
                        for (c in ap) {
                            var realPrice = "null"
                            var descPrice = "null"
                            var descuentoCode: String? = if (c.fbCodeType == null || c.fbCodeType == "") {
                                "1"
                            } else {
                                c.fbCodeType
                            }
                            if (descuentoCode == null || descuentoCode == "" || descuentoCode == "1") {
                                val real = c.precioReal.toDouble()
                                realPrice = Html.fromHtml("<strike>" + m + " " + format.format(real) + "</strike>").toString()
                                val descuento = c.precioDesc.toDouble()
                                descPrice = m + " " + format.format(descuento)
                            } else if (descuentoCode == "2") {
                                realPrice = ""
                                val descuento = c.precioDesc.toDouble()
                                descPrice = format.format(descuento) + " %"
                            } else if (descuentoCode == "3") {
                                realPrice = ""
                                val descuento = c.precioDesc.toDouble()
                                descPrice = m + " " + format.format(descuento)
                            }
                            cardList!!.add(Cupon(c.idCategoria, c.titulo, c.subtitulo, descPrice, realPrice, c.descripcion, c.tyc, c.cantCanje, c.cantCanjeUSER, c.fechaInicio, c.fechaFin, c.imagenCupon, c.imagenComercio, c.nombreComercio, c.idComercio, c.whatsapp, c.instagram, c.facebook, c.pais, descuentoCode, fav))
                        }
                        cardAdapter!!.notifyDataSetChanged()
                        dialog!!.dismiss()
                        swipeRefreshLayout!!.isRefreshing = false
                    }
                } else {
                    showEmptyLayout(true)
                    dialog!!.dismiss()
                    swipeRefreshLayout!!.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<ArrayList<Cupon>>, t: Throwable) {
                showError(this@ExploreCouponsActivity, getString(R.string.error_connection), "")
                showEmptyLayout(true)
                dialog!!.dismiss()
                swipeRefreshLayout!!.isRefreshing = false
            }
        })
    }

    private fun mejoresDescuentosVIP() {
        val exploreWS = VipCouponsRequest()
        exploreWS.pais = userCountry
        exploreWS.idUsuario = userUID
        exploreWS.texto = ""
        exploreWS.esBusqueda = true
        val call: Call<ArrayList<VipCoupon>> = getRetrofit()!!.create(NetworkService::class.java).getMejoresVIP(exploreWS)
        call.enqueue(object : Callback<ArrayList<VipCoupon>> {
            override fun onResponse(call: Call<ArrayList<VipCoupon>>, response: Response<ArrayList<VipCoupon>>) {
                if (response.code() == 200) {
                    val ap = response.body()
                    if (ap!!.isEmpty()) {
                        showEmptyLayout(true)
                        dialog!!.dismiss()
                        swipeRefreshLayout!!.isRefreshing = false
                    } else {
                        showEmptyLayout(false)
                        for (vip in ap) {
                            val vipCouponArray = VipCoupon()
                            vipCouponArray.idCampana = vip.idCampana
                            vipCouponArray.titulo = vip.titulo
                            vipCouponArray.subtitulo = vip.subtitulo
                            vipCouponArray.precioReal = vip.precioReal
                            vipCouponArray.precioDescuento = vip.precioDescuento
                            vipCouponArray.idComercio = vip.idComercio
                            vipCouponArray.urlImagenCampana = vip.urlImagenCampana
                            vipCouponArray.urlImagenComercio = vip.urlImagenComercio
                            vipCouponArray.nombrePlan = vip.nombrePlan
                            vipCouponArray.idPlanLealtad = vip.idPlanLealtad
                            vipCouponArray.isUsuarioActivo = vip.isUsuarioActivo
                            vipCouponArray.fbCodeType = vip.fbCodeType
                            dataList!!.add(vipCouponArray)
                        }
                        vipCouponAdapter!!.notifyDataSetChanged()
                        dialog!!.dismiss()
                        swipeRefreshLayout!!.isRefreshing = false
                    }
                } else {
                    showEmptyLayout(true)
                    dialog!!.dismiss()
                    swipeRefreshLayout!!.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<ArrayList<VipCoupon>>, t: Throwable) {
                showError(this@ExploreCouponsActivity, getString(R.string.error_connection), "")
                showEmptyLayout(true)
                dialog!!.dismiss()
                swipeRefreshLayout!!.isRefreshing = false
            }
        })
    }

    private fun cardAction(id: String?) {
        val intent = Intent(this, CouponDetail2Activity::class.java)
        intent.putExtra("couponId", id?.toUpperCase() ?: "")
        intent.putExtra("couponType", "0")
        startActivity(intent)
        this.finish()
    }

    private fun cardVipAction(id: String) {
        val intent = Intent(this, CouponDetail2Activity::class.java)
        intent.putExtra("couponId", id.toUpperCase())
        intent.putExtra("couponType", "1")
        startActivity(intent)
        this.finish()
    }

    private fun getRetrofit(): Retrofit? {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        return Retrofit.Builder()
                .baseUrl(NetworkService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
    }

}
