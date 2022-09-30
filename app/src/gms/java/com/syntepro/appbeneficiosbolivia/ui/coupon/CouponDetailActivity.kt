package com.syntepro.appbeneficiosbolivia.ui.coupon

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.dynamiclinks.DynamicLink.AndroidParameters
import com.google.firebase.dynamiclinks.DynamicLink.IosParameters
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.database.DataBaseAdapter
import com.syntepro.appbeneficiosbolivia.entity.app.Pais
import com.syntepro.appbeneficiosbolivia.entity.firebase.Cupon
import com.syntepro.appbeneficiosbolivia.entity.service.Sucursales
import com.syntepro.appbeneficiosbolivia.room.database.RoomDataBase
import com.syntepro.appbeneficiosbolivia.room.entity.NavigationCouponUser
import com.syntepro.appbeneficiosbolivia.service.NetworkService
import com.syntepro.appbeneficiosbolivia.ui.explore.ExploreCouponsActivity
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Constants.Companion.provenance
import com.syntepro.appbeneficiosbolivia.utils.Constants.Companion.userCountryProfile
import com.syntepro.appbeneficiosbolivia.utils.DispatchGroup
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.showWarning
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.userCountry
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.userSession
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.userTimeZone
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.userUID
import kotlinx.android.synthetic.gms.activity_coupon_detail.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CouponDetailActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private val roomDataBase by lazy { RoomDataBase.getRoomDatabase(this) }
    private val adapter by lazy { DataBaseAdapter(this.applicationContext) }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val mAuth by lazy { FirebaseAuth.getInstance() }
    private val mapFragment by lazy {
        supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
    }
    private var loc: Location? = null
    private lateinit var mMap: GoogleMap
    private lateinit var readCoupon: Cupon
    private var t: Thread? = null
    private var year: Int = 0
    private var month: Int = 0
    private var actualExchange: Int = 0
    private var commerce: String = ""
    private var validation: Boolean = false
    private var pais: String? = null
    private var paisActual: String? = null
    private var maxCanjeUsr = 0
    private var extrasCouponId: String? = ""
    private var extrasCouponType: String? = ""
    private var country: Pais? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon_detail)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.back_toolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = null

        // Extras
        val extras = intent.extras
        if (extras != null) {
            extrasCouponId = extras.getString("couponId")
            extrasCouponType = extras.getString("couponType")
            Log.e("Data", "Id -> $extrasCouponId  --  Type -> $extrasCouponType")
            addNavigationData()
        }

        mapFragment!!.getMapAsync(this)

        if (!runtimePermissions()) {
            if (isGPSProvider(this)) {
                s2.visibility = View.VISIBLE
                s4.visibility = View.VISIBLE
                mapFragment!!.requireView().visibility = View.VISIBLE
                val locManager = getSystemService(LOCATION_SERVICE) as LocationManager
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            } else {
                mapFragment!!.requireView().visibility = View.GONE
                s2.visibility = View.INVISIBLE
                s4.visibility = View.INVISIBLE
            }
        } else {
            mapFragment!!.requireView().visibility = View.GONE
            s2.visibility = View.INVISIBLE
            s4.visibility = View.INVISIBLE
        }

        val date = Calendar.getInstance()
        year = date[Calendar.YEAR]
        month = date[Calendar.MONTH] + 1

        expandableTextStepsId.text = getString(R.string.pasos)

        favorite(mAuth.currentUser!!, extrasCouponId)

        agenciesItem.setOnClickListener {
            if (commerce.isEmpty()) return@setOnClickListener
            val intent = Intent(this@CouponDetailActivity, AgencyActivity::class.java)
            intent.putExtra("commerceId", commerce)
            intent.putExtra("commerceName", readCoupon.nombreComercio)
            intent.putExtra("commerceImage", readCoupon.imagenComercio)
            intent.putExtra("couponId", extrasCouponId)
            intent.putExtra("provenance", 0)
            startActivity(intent)
//                startActivity(AgencyActivity.getIntent(this@CouponDetailActivity, commerce, extrasCouponId, "0", readCoupon.nombreComercio))
        }

        fbGenerateQR.setOnClickListener {
            progressId.visibility = View.VISIBLE
            fbGenerateQR.setImageResource(0)
            if (extrasCouponType?.toInt() == 1) validateVIP(it)
            else validateGeneral(it)
        }

        whatsAppId.setOnClickListener { openWhatsApp(readCoupon.whatsapp) }

        instagramId.setOnClickListener { openURL(readCoupon.instagram) }

        facebookId.setOnClickListener{ openURL(readCoupon.facebook) }

        val countryUser = roomDataBase.accessDao().country!!
        paisActual = countryUser.abr
    }

    override fun onStart() {
        super.onStart()
        progressId.visibility = View.GONE
        fbGenerateQR.setImageResource(R.drawable.ic_qr)
        fbGenerateQR.isEnabled = true
        extrasCouponType?.let {
            if (it.toInt() == 0) readCouponCustomObject(extrasCouponId ?: "")
            else readCouponVIPCustomObject(extrasCouponId ?: "")
            fbGenerateQR.isEnabled = true
        } ?: run {
            Toast.makeText(this, "Ocurrió un error, intentelo de nuevo.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val provenance = provenance
        val params: Map<String, Any> = Constants.map
        when (provenance) {
            "2", "3", "4" ->
                // Explore
                performIntentExplore(params)
            "5" -> {
                // Category
                val id = Objects.requireNonNull(params["id"]).toString()
                val name = Objects.requireNonNull(params["name"]).toString()
                performIntentCategories(id, name)
            }
            "6" -> {
                val commerceId = Objects.requireNonNull(params["commerceId"]).toString()
                val commerceName = Objects.requireNonNull(params["commerceName"]).toString()
                performIntentCoupons(commerceId, commerceName)
            }
        }
    }

    override fun onDestroy() {
        t?.interrupt()
        t = null
        finish()
        RoomDataBase.destroyInstance()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 50 && resultCode == Activity.RESULT_OK) {
            data?.let {
                val amount = it.getIntExtra("amount", 0)
                if (amount != 0) {
                    val currentUser = mAuth.currentUser
                    generateQR(currentUser, extrasCouponId, amount)
                } else {
                    Toast.makeText(this, "Ocurrió un error, intentelo de nuevo.", Toast.LENGTH_LONG).show()
                }
            }
        } else if (requestCode == 50 && resultCode == Activity.RESULT_CANCELED) {
            progressId.visibility = View.GONE
            fbGenerateQR.setImageResource(R.drawable.ic_qr)
            fbGenerateQR.isEnabled = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.share_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.shareItem) {
            if (this::readCoupon.isInitialized) {
                val content = readCoupon.descripcion
                val couponId = extrasCouponId
                val category = readCoupon.idCategoria
                val country = readCoupon.pais
                shareContent(content, couponId, category, country)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let {
            mMap = it

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }

            mMap.isMyLocationEnabled = true

            loc?.let {
                location -> getAgency(location.latitude, location.longitude)
            } ?: run {
                mapFragment!!.requireView().visibility = View.GONE
                s2.visibility = View.INVISIBLE
                s4.visibility = View.INVISIBLE
            }

            mMap.setOnInfoWindowClickListener(this@CouponDetailActivity)

            mMap.uiSettings.isZoomControlsEnabled = true
            mMap.uiSettings.isZoomGesturesEnabled = false
            mMap.uiSettings.isScrollGesturesEnabled = false
        }
    }

    override fun onInfoWindowClick(marker: Marker?) {
        val uri = "http://maps.google.com/maps?daddr=" + marker!!.position.latitude + "," + marker.position.longitude + " (" + marker.title + ")"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")
        try {
            this.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            try {
                val unrestrictedIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                this.startActivity(unrestrictedIntent)
            } catch (innerEx: ActivityNotFoundException) {
                showWarning(this@CouponDetailActivity, getString(R.string.install_maps))
            }
        }
    }

    fun onClickFavorite(view: View) {
        val animScale = AnimationUtils.loadAnimation(this, R.anim.scale_fav)
        view.startAnimation(animScale)
        addFav(mAuth.currentUser, extrasCouponId!!)
    }

    private fun validateGeneral(v: View) {
        if (pais != null && paisActual != null) {
            if (pais == paisActual) {
                fbGenerateQR.isEnabled = false
                readMaxUser(mAuth.currentUser, extrasCouponId)
                val bar = Snackbar.make(v, getString(R.string.qr), Snackbar.LENGTH_LONG).setAction(getString(R.string.close), null)
                bar.show()
            } else {
                // Show country info
                // Get Countries
                adapter.open()
                val countryCoupon = adapter.getCountryInfoAbr(pais)
                adapter.close()
                showWarning(this, "Este cupón esta disponible únicamente en " + countryCoupon.nombre + ".")
                showError()
            }
        } else {
            fbGenerateQR.isEnabled = false
            readMaxUser(mAuth.currentUser, extrasCouponId)
            val bar = Snackbar.make(v, getString(R.string.qr), Snackbar.LENGTH_LONG).setAction(getString(R.string.close), null)
            bar.show()
        }
    }

    private fun validateVIP(v: View) {
        val dispatchGroup = DispatchGroup()
        dispatchGroup.enter()
        FirebaseFirestore.getInstance().collection("CuponesVIP").document(extrasCouponId!!)
                .get()
                .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                    if (task.isSuccessful) {
                        val documentSnapshot = task.result
                        if (Objects.requireNonNull(documentSnapshot).exists()) {
                            val fbCategory = documentSnapshot["categoriaPlan"]
                            var categoriaPlan = 0
                            if (fbCategory != null) categoriaPlan = fbCategory.toString().toInt()
                            if (categoriaPlan == 4) {
                                var idPlan = ""
                                val planID = documentSnapshot["idPlanLealtad"]
                                if (planID != null) idPlan = Objects.requireNonNull(documentSnapshot["idPlanLealtad"]).toString()
                                FirebaseFirestore.getInstance().collection("Usuarios").document(userUID)
                                        .collection(Constants.MY_LOYALTY_PLAN_COLLECTION)
                                        .whereEqualTo("plan", idPlan)
                                        .whereEqualTo("activo", true)
                                        .get()
                                        .addOnCompleteListener { querySnapshot: Task<QuerySnapshot> ->
                                            if (!Objects.requireNonNull(querySnapshot.result).isEmpty) {
                                                val id = querySnapshot.result.documents[0].id
                                                FirebaseFirestore.getInstance().collection("Usuarios").document(userUID)
                                                        .collection(Constants.MY_LOYALTY_PLAN_COLLECTION).document(id)
                                                        .collection(Constants.STAMP_CARDS_COLLECTION)
                                                        .whereEqualTo("estatus", 2)
                                                        .get().addOnCompleteListener { stamp: Task<QuerySnapshot?> ->
                                                            if (stamp.result!!.isEmpty) {
                                                                validation = false
                                                                dispatchGroup.leave()
                                                            } else {
                                                                validation = true
                                                                dispatchGroup.leave()
                                                            }
                                                        }
                                            } else {
                                                validation = false
                                                dispatchGroup.leave()
                                            }
                                        }.addOnFailureListener { e: java.lang.Exception ->
                                            e.printStackTrace()
                                            validation = false
                                            dispatchGroup.leave()
                                        }
                            } else {
                                validation = true
                                dispatchGroup.leave()
                            }
                        } else {
                            validation = false
                            dispatchGroup.leave()
                        }
                    } else {
                        validation = false
                        dispatchGroup.leave()
                    }
                }.addOnFailureListener { e: java.lang.Exception ->
                    e.printStackTrace()
                    validation = false
                    dispatchGroup.leave()
                }
        dispatchGroup.notify {
            if (validation) {
                if (pais != null && paisActual != null) {
                    if (pais == paisActual) {
                        fbGenerateQR.isEnabled = false
                        readMaxUser(mAuth.currentUser, extrasCouponId)
                        val bar = Snackbar.make(v, getString(R.string.qr), Snackbar.LENGTH_LONG).setAction(getString(R.string.close), null)
                        bar.show()
                    } else {
                        // Show country info
                        // Get Countries
                        adapter.open()
                        val countryCoupon = adapter.getCountryInfoAbr(pais)
                        adapter.close()
                        showWarning(this, "Este cupón esta disponible únicamente en " + countryCoupon.nombre + ".")
                        showError()
                    }
                } else {
                    fbGenerateQR.isEnabled = false
                    readMaxUser(mAuth.currentUser, extrasCouponId)
                    val bar = Snackbar.make(v, getString(R.string.qr), Snackbar.LENGTH_LONG).setAction(getString(R.string.close), null)
                    bar.show()
                }
            } else {
                showWarning(this, "No has completado ninguna tarjeta de lealtad para poder canjear este cupón.")
                showError()
            }
        }
    }

    private fun performIntentExplore(params: Map<String, Any>) {
        val optionId = Objects.requireNonNull(params["optionid"]).toString()
        val categoryName = Objects.requireNonNull(params["categoryiname"]).toString()
        val intent2 = Intent(this, ExploreCouponsActivity::class.java)
        intent2.putExtra("opcionid", optionId)
        intent2.putExtra("categoryiname", categoryName)
        startActivity(intent2)
    }

    private fun performIntentCategories(id: String, name: String) {
//        startActivity(CouponActivity.getIntent(this, id, name))
    }

    private fun performIntentCoupons(commerceId: String, name: String) {
//        val intent = Intent(this, CouponActivity2::class.java)
//        intent.putExtra("commerceId", commerceId)
//        intent.putExtra("commerceName", name)
//        startActivity(intent)
    }

    private fun addNavigationData() {
        val ids = TimeZone.getAvailableIDs()
        val tz = TimeZone.getTimeZone(ids[144])
        val c = Calendar.getInstance(tz)
        val navigationCouponUser = NavigationCouponUser()
        navigationCouponUser.fechaRegistro = c.time
        navigationCouponUser.idCupon = extrasCouponId
        roomDataBase.accessDao().addNavigationCouponUser(navigationCouponUser)
    }

    private fun openWhatsApp(phone: String?) {
        if (phone.isNullOrEmpty()) return
        val url = "https://api.whatsapp.com/send?phone=$phone"
        try {
            val pm = applicationContext.packageManager
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        } catch (e: PackageManager.NameNotFoundException) {
            showWarning(this@CouponDetailActivity, getString(R.string.wa_not_instaled))
        }
    }

    private fun openURL(url: String?) {
        if (url.isNullOrEmpty()) return
        if (!url.startsWith("http")) {
            showWarning(this@CouponDetailActivity, this.getString(R.string.invalid_url))
            return
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    @SuppressLint("SetWorldReadable")
    private fun shareContent(content: String, id: String?, category: String, country: String) {
        if (id.isNullOrEmpty()) return
        val bitmap = getBitmapFromView(couponImageId)
        try {
            val file = File(this.externalCacheDir, "cupon.png")
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
            file.setReadable(true, false)
            val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("https://www.beneficioslatam.com/?id=$id&c=$category&p=$country"))
                    .setDomainUriPrefix("https://beneficioslatam.page.link") // Open links with this app on Android
                    .setAndroidParameters(AndroidParameters.Builder().build()) // Open links with com.example.ios on iOS
                    .setIosParameters(IosParameters.Builder("com.syntepro.boliviaBeneficios").build())
                    .buildDynamicLink()
            val dynamicLinkUri = dynamicLink.uri
            val intent = Intent(Intent.ACTION_SEND)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLongLink(dynamicLinkUri)
                    .buildShortDynamicLink()
                    .addOnCompleteListener(this) { task: Task<ShortDynamicLink?> ->
                        if (task.isSuccessful) {
                            // Short link created
                            val shortLink = task.result?.shortLink
                            intent.putExtra(Intent.EXTRA_TEXT, "$content $shortLink")
                            val imageUri = FileProvider.getUriForFile(
                                    this@CouponDetailActivity,
                                    "com.syntepro.appbeneficiosbolivia.provider",  //(use your app signature + ".provider" )
                                    file)
                            intent.putExtra(Intent.EXTRA_STREAM, imageUri)
                        } else {
                            // Error to get short link
                            intent.putExtra(Intent.EXTRA_TEXT, "$content $dynamicLinkUri")
                            val imageUri = FileProvider.getUriForFile(
                                    this@CouponDetailActivity,
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
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnedBitmap
    }

    private fun generateString(): String? {
        val uuid = UUID.randomUUID().toString()
        return uuid.substring(0, 7).toUpperCase(Locale.getDefault())
    }

    private fun getDifferenceDates(start: Date?, end: Date?): String? {
        if (start == null || end == null) return ""
        var difference = end.time - start.time
        val seconds: Long = 1000
        val minutes = seconds * 60
        val hours = minutes * 60
        val days = hours * 24
        val resultDay = difference / days
        difference %= days
        val resultHour = difference / hours
        difference %= hours
        val resultMinutes = difference / minutes
        difference %= minutes
        val resultSeconds = difference / seconds
        return "$resultDay Días $resultHour: $resultMinutes: ${resultSeconds}s"
    }

    private fun countDown(fin: String) {
        t = object : Thread() {
            @SuppressLint("SimpleDateFormat")
            override fun run() {
                try {
                    while (!isInterrupted) {
                        sleep(1000)
                        runOnUiThread {
                            try {
                                val dateFormat = "dd/MM/yyyy HH:mm:ss"
                                val formatter = SimpleDateFormat(dateFormat)
                                formatter.timeZone = TimeZone.getTimeZone(userTimeZone)
                                val currentTime = Calendar.getInstance()
                                val dateNow = formatter.format(currentTime.time)
                                @SuppressLint("SimpleDateFormat") val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
                                val dateI: Date?
                                val dateF: Date?
                                val now = simpleDateFormat.parse(dateNow)
                                dateF = simpleDateFormat.parse(fin)
                                dateI = now
                                val dif: String? = getDifferenceDates(dateI, dateF)
                                counter.text = dif
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                        }
                    }
                } catch (ie: InterruptedException) {
                    ie.printStackTrace()
                }
            }
        }
        (t as Thread).start()
    }

    private fun getAgency(lat: Double, lon: Double) {
        val userCountry: String = if (userCountryProfile != null) userCountryProfile!!.pais
                                    else userCountry
        val call: Call<ArrayList<Sucursales>> = getRetrofit()!!.create(NetworkService::class.java).getSucursales(userCountry, extrasCouponId, lat, lon, true)
        call.enqueue(object : Callback<ArrayList<Sucursales>> {
            override fun onResponse(call: Call<ArrayList<Sucursales>>, response: Response<ArrayList<Sucursales>>) {
                if (response.code() == 200) {
                    try {
                        val ap = response.body()!!
                        if (ap.isNotEmpty()) {
                            for (su in ap) {
                                val markerOptions = MarkerOptions()
                                markerOptions.position(LatLng(su.latitude, su.longitud))
                                mMap.addMarker(markerOptions.title(su.nombreComercio).snippet(su.nombreSucursal).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_50)))

                                // Add a marker in my location and move the camera/zoom
                                val location = LatLng(su.latitude, su.longitud)
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                            }
                        } else {
                            mapFragment!!.requireView().visibility = View.GONE
                            s2.visibility = View.INVISIBLE
                            s4.visibility = View.INVISIBLE
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        mapFragment!!.requireView().visibility = View.GONE
                        s2.visibility = View.INVISIBLE
                        s4.visibility = View.INVISIBLE
                    }
                } else {
                    mapFragment!!.requireView().visibility = View.GONE
                    s2.visibility = View.INVISIBLE
                    s4.visibility = View.INVISIBLE
                }
            }

            override fun onFailure(call: Call<ArrayList<Sucursales>>, t: Throwable) {
                mapFragment!!.requireView().visibility = View.GONE
            }
        })
    }

    /**
     *  Firebase Functions
     */
    @SuppressLint("RestrictedApi")
    private fun readCouponVIPCustomObject(id: String?) {
        val cupon = db.collection("CuponesVIP").document(id!!)
        cupon.get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
            if (task.isSuccessful) {
                val documentSnapshot = task.result
                if (Objects.requireNonNull(documentSnapshot).exists()) {
                    readCoupon = documentSnapshot.toObject(Cupon::class.java)!!
                    countDown(readCoupon.fechaFin)

                    if (!readCoupon.whatsapp.isNullOrEmpty()) {
                        whatsAppId.setImageResource(R.drawable.ic_wa_selected)
                    }

                    if (!readCoupon.instagram.isNullOrEmpty()) {
                        instagramId.setImageResource(R.drawable.ic_ig_selected)
                    }

                    if (!readCoupon.facebook.isNullOrEmpty()) {
                        facebookId.setImageResource(R.drawable.ic_fb_selected)
                    }

                    val tipoDescuentoCode: String = if (!readCoupon.fbCodeType.isNullOrEmpty()) readCoupon.fbCodeType
                                        else "1"
                    val format = DecimalFormat("###,###.00")
                    titleCouponId.text = Objects.requireNonNull(readCoupon).titulo
                    subtitleCouponId.text = readCoupon.subtitulo

                    // Show country info
                    // Get Countries
                    adapter.open()
                    country = adapter.getCountryInfoAbr(pais)
                    adapter.close()
                    val m: String = if (country?.moneda != null) country!!.moneda
                                    else ""

                    // Validation Coupon Code
                    if (tipoDescuentoCode.isEmpty() || tipoDescuentoCode == "1") {
                        val preal = readCoupon.precioReal.toDouble()
                        realPriceId.text = Html.fromHtml("<strike>" + m + " " + format.format(preal) + "</strike>")
                        val prdesc = readCoupon.precioDesc.toDouble()
                        val desc = m + " " + format.format(prdesc)
                        discountPriceId.text = desc
                    } else if (tipoDescuentoCode == "2") {
                        realPriceId.text = ""
                        val prdesc = readCoupon.precioDesc.toDouble()
                        val desc = format.format(prdesc) + " %"
                        discountPriceId.text = desc
                    } else if (tipoDescuentoCode == "3") {
                        realPriceId.text = ""
                        val prdesc = readCoupon.precioDesc.toDouble()
                        val desc = m + " " + format.format(prdesc)
                        discountPriceId.text = desc
                    }
                    expandableTextDetailId.text = readCoupon.descripcion
                    expandableTextConditionsId.text = readCoupon.tyc + " " + getString(R.string.first_coupon) + " " + readCoupon.cantCanjeUSER + " " + getString(R.string.second_coupon)
                    exchangeQuantityCoupons.text = readCoupon.cantCanje
                    val cantCanjeUsuario = Objects.requireNonNull(readCoupon).cantCanjeUSER
                    maxCanjeUsr = cantCanjeUsuario.toInt()
                    commerceName.text = readCoupon.nombreComercio
                    if (readCoupon.cantCanje == "0") fbGenerateQR.visibility = View.GONE

                    Picasso.get()
                            .load(readCoupon.imagenCupon)
                            .fit()
                            .centerCrop()
                            .error(R.drawable.notfound)
                            .into(couponImageId)
                    Picasso.get()
                            .load(readCoupon.imagenComercio)
                            .fit()
                            .centerInside()
                            .error(R.drawable.notfound)
                            .into(commerceImageId)
                } else {
                    showWarning(this@CouponDetailActivity, "Cupón Inválido")
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun readCouponCustomObject(id: String) {
        val cupon = db.collection("Cupones").document(id)
        cupon.get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
            if (task.isSuccessful) {
                val documentSnapshot = task.result
                if (Objects.requireNonNull(documentSnapshot).exists()) {
                    readCoupon = documentSnapshot.toObject(Cupon::class.java)!!
                    countDown(readCoupon.fechaFin)

                    if (!readCoupon.whatsapp.isNullOrEmpty()) {
                        whatsAppId.setImageResource(R.drawable.ic_wa_selected)
                    }

                    if (!readCoupon.instagram.isNullOrEmpty()) {
                        instagramId.setImageResource(R.drawable.ic_ig_selected)
                    }

                    if (!readCoupon.facebook.isNullOrEmpty()) {
                        facebookId.setImageResource(R.drawable.ic_fb_selected)
                    }

                    val tipoDescuentoCode: String = if (!readCoupon.fbCodeType.isNullOrEmpty()) readCoupon.fbCodeType
                                                    else "1"

                    val format = DecimalFormat("###,###.00")
                    titleCouponId.text = readCoupon.titulo
                    subtitleCouponId.text = readCoupon.subtitulo
                    commerce = readCoupon.idComercio

                    // Show country info
                    // Get Countries
                    adapter.open()
                    country = adapter.getCountryInfoAbr(pais)
                    adapter.close()
                    val m: String = if (country?.moneda != null) country!!.moneda
                                    else ""

                    // Validation Coupon Code
                    if (tipoDescuentoCode.isEmpty() || tipoDescuentoCode == "1") {
                        val preal = readCoupon.precioReal.toDouble()
                        realPriceId.text = Html.fromHtml("<strike>" + m + " " + format.format(preal) + "</strike>")
                        val prdesc = readCoupon.precioDesc.toDouble()
                        val desc = m + " " + format.format(prdesc)
                        discountPriceId.text = desc
                    } else if (tipoDescuentoCode == "2") {
                        realPriceId.text = ""
                        val prdesc = readCoupon.precioDesc.toDouble()
                        val desc = format.format(prdesc) + " %"
                        discountPriceId.text = desc
                    } else if (tipoDescuentoCode == "3") {
                        realPriceId.text = ""
                        val prdesc = readCoupon.precioDesc.toDouble()
                        val desc = m + " " + format.format(prdesc)
                        discountPriceId.text = desc
                    }
                    expandableTextDetailId.text = readCoupon.descripcion
                    expandableTextConditionsId.text = readCoupon.tyc + " " + getString(R.string.first_coupon) + " " + readCoupon.cantCanjeUSER + " " + getString(R.string.second_coupon)
                    exchangeQuantityCoupons.text = readCoupon.cantCanje
                    val cantCanjeUsuario = Objects.requireNonNull(readCoupon).cantCanjeUSER
                    maxCanjeUsr = cantCanjeUsuario.toInt()
                    commerceName.text = readCoupon.nombreComercio

                    if (readCoupon.cantCanje == "0") fbGenerateQR.visibility = View.GONE

                    Picasso.get()
                            .load(readCoupon.imagenCupon)
                            .fit()
                            .centerCrop()
                            .error(R.drawable.notfound)
                            .into(couponImageId)
                    Picasso.get()
                            .load(readCoupon.imagenComercio)
                            .fit()
                            .centerInside()
                            .error(R.drawable.notfound)
                            .into(commerceImageId)
                } else readCouponVIPCustomObject(extrasCouponId)
            }
        }
    }

    private fun readMaxUser(user: FirebaseUser?, id: String?) {
        db.collection("CanjexUsuario")
                .whereEqualTo("idUsuario", user!!.uid)
                .whereEqualTo("idCupon", id)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.isSuccessful) {
                        if (task.result!!.isEmpty) {
                            actualExchange = 0
                            verificationQR(extrasCouponId ?: "")
                        } else {
                            for (document in task.result!!) {
                                val act = Objects.requireNonNull(document["canje"]).toString()
                                actualExchange = act.toInt()
                                verificationQR(extrasCouponId ?: "")
                            }
                        }
                    }
                }
    }

    fun favorite(user: FirebaseUser, id: String?) {
        val favorito = "0"
        db.collection("Favorito")
                .whereEqualTo("idUsuario", user.uid)
                .whereEqualTo("idCupon", id)
                .whereEqualTo("estado", favorito)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.isSuccessful) {
                        if (task.result!!.isEmpty)
                            favorite.setBackgroundResource(R.drawable.ic_favorito)
                        else {
                            val animScale = AnimationUtils.loadAnimation(this@CouponDetailActivity, R.anim.scale_fav)
                            favorite.startAnimation(animScale)
                            favorite.setBackgroundResource(R.drawable.ic_corazon)
                        }
                    }
                }
    }

    private fun addFav(user: FirebaseUser?, id: String) {
        val favoriteState = "0"
        db.collection("Favorito")
                .whereEqualTo("idUsuario", user!!.uid)
                .whereEqualTo("idCupon", id)
                .whereEqualTo("estado", favoriteState)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot> ->
                    if (task.isSuccessful) {
                        if (Objects.requireNonNull(task.result).isEmpty) {
                            favorite.setBackgroundResource(R.drawable.ic_corazon)
                            val userCountry = if (userCountryProfile != null)
                                userCountryProfile!!.pais
                            else
                                userSession.pais
                            val newFav: MutableMap<String, Any> = HashMap()
                            newFav["idUsuario"] = user.uid
                            newFav["idCupon"] = id
                            newFav["fecha"] = Date(System.currentTimeMillis())
                            newFav["estado"] = "0"
                            newFav["tipo"] = "c"
                            newFav["pais"] = userCountry
                            db.collection("Favorito")
                                    .add(newFav)
                                    .addOnFailureListener { favorite.setBackgroundResource(R.drawable.ic_favorito) }
                        } else {
                            favorite.setBackgroundResource(R.drawable.ic_favorito)
                            for (document in task.result) {
                                val ref = db.collection("Favorito").document(document.id)
                                ref.update("estado", "1")
                                        .addOnFailureListener { favorite.setBackgroundResource(R.drawable.ic_corazon) }
                            }
                        }
                    }
                }
    }

    private fun verificationQR(id: String) {
        if (Objects.requireNonNull(extrasCouponType!!).toInt() == 0) {
            val reference = db.collection("Cupones").document(id)
            reference.get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val documentSnapshot = task.result
                    if (Objects.requireNonNull(documentSnapshot).exists()) {
                        val readCategory = documentSnapshot.toObject(Cupon::class.java)
                        if (readCategory != null) {
                            val remaining = Objects.requireNonNull(readCategory).cantCanje.toInt()
                            if (maxCanjeUsr > actualExchange) {
                                if (remaining > 0) {
                                    val currentUser = mAuth.currentUser
                                    generate(currentUser, extrasCouponId)
                                } else {
                                    readCouponCustomObject(extrasCouponId ?: "")
                                    showWarning(this@CouponDetailActivity, getString(R.string.sold_out))
                                    showError()
                                }
                            } else {
                                showWarning(this@CouponDetailActivity, getString(R.string.exceeded))
                                fbGenerateQR.isEnabled = true
                                showError()
                            }
                        }
                    }
                }
            }
        } else if (extrasCouponType!!.toInt() == 1) {
            val reference = db.collection("CuponesVIP").document(id)
            reference.get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val documentSnapshot = task.result
                    if (Objects.requireNonNull(documentSnapshot).exists()) {
                        val readCategory = documentSnapshot.toObject(Cupon::class.java)
                        if (readCategory != null) {
                            val remaining = Objects.requireNonNull(readCategory).cantCanje.toInt()
                            if (maxCanjeUsr > actualExchange) {
                                if (remaining > 0) {
                                    val currentUser = mAuth.currentUser
                                    generate(currentUser, extrasCouponId)
                                } else {
                                    readCouponVIPCustomObject(extrasCouponId)
                                    showWarning(this@CouponDetailActivity, getString(R.string.sold_out))
                                }
                            } else {
                                showWarning(this@CouponDetailActivity, getString(R.string.exceeded))
                                fbGenerateQR.isEnabled = true
                            }
                        }
                    }
                }
            }
        }
    }

    private fun generate(user: FirebaseUser?, id: String?) {
        db.collection("Codigo")
                .whereEqualTo("idUsuario", user!!.uid)
                .whereEqualTo("idCupon", id)
                .whereEqualTo("estado", "0")
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot> ->
                    if (task.isSuccessful) {
                        if (Objects.requireNonNull(task.result).isEmpty) {
//                            val currentUser = mAuth.currentUser
//                            generateQR(currentUser, extrasCouponId)
                            /**
                             * Rodrigo Osegueda 20AGO2020
                             * Added method to confirm coupons by amount before generating QR.
                             * */
                            val remaining = maxCanjeUsr - actualExchange
                            val intent = Intent(this, QRAmountActivity::class.java)
                            intent.putExtra("userQR", "Cupones disponibles por usuario: $remaining")
                            intent.putExtra("remaining", remaining)
                            startActivityForResult(intent, 50)
                        } else {
                            val documentSnapshot = task.result.documents[0]
                            val intent = Intent(this, QRDetailActivity::class.java)
                            intent.putExtra("categoryid", extrasCouponId)
                            intent.putExtra("listenerid", false)
                            intent.putExtra("qrCode", documentSnapshot.id)
                            intent.putExtra("modelCoupon", readCoupon)
                            startActivity(intent)
                        }
                    }
                }
    }

    private fun generateQR(user: FirebaseUser?, id: String?, amount: Int) {
        val userCountry: String = if (userCountryProfile != null)
            userCountryProfile!!.pais
        else
            userCountry
        @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        formatter.timeZone = TimeZone.getTimeZone(userTimeZone)
        val currentTime = Calendar.getInstance()
        val newCodigo: MutableMap<String, Any?> = HashMap()
        newCodigo["idUsuario"] = user!!.uid
        newCodigo["idCupon"] = id
        newCodigo["fechaGeneracion"] = currentTime.time
        newCodigo["cantCanje"] = Objects.requireNonNull(readCoupon).cantCanje
        newCodigo["estado"] = "0"
        newCodigo["pais"] = userCountry
        newCodigo["redeemedAmount"] = 0
        newCodigo["amountGenerated"] = amount
        newCodigo["mobileBrand"] = Build.BRAND
        newCodigo["phoneNumber"] = Constants.userPhone
        val hashId = generateString()
        addQR(newCodigo, hashId!!)
        val newEstadistica: MutableMap<String, Any> = HashMap()
        newEstadistica["idUsuario"] = user.uid

        // Get Calendar info
        val fecha = Calendar.getInstance()
        val anio = fecha[Calendar.YEAR]
        val mes = fecha[Calendar.MONTH] + 1
        val anioActual = anio.toString()
        val mesActual = mes.toString()
        newEstadistica["mes"] = mesActual
        newEstadistica["año"] = anioActual
        newEstadistica["idCategoria"] = readCoupon.idCategoria
        val r = readCoupon.precioReal.toDouble()
        val d = readCoupon.precioDesc.toDouble()
        val dif = r - d
        val ahorro = dif * amount.toDouble()
        newEstadistica["ahorro"] = "0"
        newEstadistica["posibleAhorro"] = ahorro.toString()
        newEstadistica["totalPrecioDesc"] = readCoupon.precioDesc
        newEstadistica["totalPrecioReal"] = readCoupon.precioReal
        newEstadistica["pais"] = userCountry
        addStatistics(user, newEstadistica, readCoupon.idCategoria, userCountry)
    }

    private fun addQR(code: Map<String, Any?>, id: String) {
        db.collection("Codigo").document(id)
                .set(code)
                .addOnCompleteListener {
                    val intent = Intent(this, QRDetailActivity::class.java)
                    intent.putExtra("categoryid", extrasCouponId)
                    intent.putExtra("listenerid", true)
                    intent.putExtra("qrCode", id)
                    intent.putExtra("modelCoupon", readCoupon)
                    startActivity(intent)
                }
    }

    private fun addStatistics(user: FirebaseUser, statistics: Map<String, Any>, id: String, abr: String) {
        val a: String = year.toString()
        val m: String = month.toString()
        db.collection("EstadisticaxUsuario")
                .whereEqualTo("idUsuario", user.uid)
                .whereEqualTo("idCategoria", id)
                .whereEqualTo("año", a)
                .whereEqualTo("mes", m)
                .whereEqualTo("pais", abr)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.isSuccessful) {
                        if (task.result!!.isEmpty) {
                            db.collection("EstadisticaxUsuario").add(statistics)
                        } else {
                            for (document in task.result!!) {
                                val pda = Objects.requireNonNull(document["totalPrecioDesc"]).toString().toDouble()
                                val pdn = Objects.requireNonNull(statistics["totalPrecioDesc"]).toString().toDouble()
                                val act = pda + pdn
                                val nuevoPD = act.toString()
                                val pra = Objects.requireNonNull(document["totalPrecioReal"]).toString().toDouble()
                                val prn = Objects.requireNonNull(statistics["totalPrecioReal"]).toString().toDouble()
                                val actpr = pra + prn
                                val nuevoPR = actpr.toString()
                                val pan = Objects.requireNonNull(document["posibleAhorro"]).toString().toDouble()
                                val paa = Objects.requireNonNull(statistics["posibleAhorro"]).toString().toDouble()
                                val actpa = pan + paa
                                val nuevoPA = actpa.toString()
                                val ref = db.collection("EstadisticaxUsuario").document(document.id)
                                ref.update("totalPrecioDesc", nuevoPD)
                                ref.update("totalPrecioReal", nuevoPR)
                                ref.update("posibleAhorro", nuevoPA)
                            }
                        }
                    }
                }
    }

    private fun runtimePermissions(): Boolean {
        return Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
    }

    private fun isGPSProvider(context: Context): Boolean {
        val lm = context.getSystemService(LOCATION_SERVICE) as LocationManager
        return Objects.requireNonNull(lm).isProviderEnabled(LocationManager.GPS_PROVIDER)
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

    private fun showError() {
        progressId.visibility = View.GONE
        fbGenerateQR.setImageResource(R.drawable.ic_qr)
        fbGenerateQR.isEnabled = true
    }

}