package com.syntepro.appbeneficiosbolivia.ui.coupon

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.huawei.hms.maps.*
import com.huawei.hms.maps.model.BitmapDescriptorFactory
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MarkerOptions
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.agency.model.AgencyDetailRequest
import com.syntepro.appbeneficiosbolivia.ui.agency.model.AgencyDetailResponse
import com.syntepro.appbeneficiosbolivia.ui.agency.viewModel.AgencyViewModel
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.hms.activity_detail_agency.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AgencyDetailActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var agencyViewModel: AgencyViewModel
    private lateinit var mMap: HuaweiMap
    private val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)
    private var branchId: String? = ""
    private var phoneNumber: String? = ""
//    private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
//    private lateinit var mMapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_detail_agency)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.mainToolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = null

        agencyViewModel = viewModel(viewModelFactory) {
            observe(agencyDetail, ::handleAgencyDetail)
            failure(failure, ::handleError)
        }

        // Parameters
        val extras = intent.extras
        if (extras != null) {
            branchId = extras.getString("branchId")
        }

        requestPermission()

        // Obtain the supportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map)
                as SupportMapFragment
        mapFragment.getMapAsync(this)
//        mMapView = findViewById(R.id.map)
//        var mapViewBundle: Bundle? = null
//        if (savedInstanceState != null) mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
//        MapsInitializer.initialize(this)
//        MapsInitializer.setApiKey("CV5qYDvCLKYFz24wlUmCsjOjrWR8owmgVeHxYovlY7FhzNhtuY7+sgpLSwRUkRwO3rlxTk0mjAMvu4xjoC4IdYdLT/yB")
//        mMapView.onCreate(mapViewBundle)
//        mMapView.getMapAsync(this)

        phone.setOnClickListener { openCall(phoneNumber) }

        // Show Data
        getAgency()
    }

    override fun onDestroy() {
        this.finish()
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onMapReady(huaweiMap: HuaweiMap?) {
        Log.e("Map", "Ready call")
        mMap = huaweiMap!!

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        Log.e("Map", "Actions")
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = false
        mMap.uiSettings.isScrollGesturesEnabled = false
    }

    private fun getAgency() {
        val request = AgencyDetailRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                idAgency = branchId ?: ""
        )
        agencyViewModel.loadAgencyDetail(request)
    }

    private fun handleAgencyDetail(response: BaseResponse<AgencyDetailResponse>?) {
        response?.data?.let {
            phoneNumber = it.phoneNumber
            Functions.showRoundedImage(it.urlImageCommerce, commerceImageId)
            commerceId.text = it.commerceName
            agencyId.text = it.agencName
            agencyLocationId.text = it.address
            if (it.phoneNumber == "") agencyPhoneId.text = getString(R.string.tel_no_registrado)
            else agencyPhoneId.text = it.phoneNumber

            val latitude = it.latitude
            val longitude = it.longitude

            val markerOptions = MarkerOptions()
            markerOptions.position(LatLng(latitude, longitude))
            if (this::mMap.isInitialized) {
                mMap.addMarker(markerOptions.title(it.agencName).snippet(it.address).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_50)))
                val latLng = LatLng(latitude, longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16F))
            } else Log.e("Error", "Map init")

            val schedule = "${time(it.openingTime)} - ${time(it.closingTime)}"
            scheduleId.text = schedule
            scheduleStatusId.text = Functions.fromHtml(getStatus(time(it.openingTime), time(it.openingTime)))
        }
    }

    private fun openCall(number: String?) {
        if(number.isNullOrEmpty()) return
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+$number"))
        this.startActivity(intent)
    }

    private fun getStatus(initSchedule: String, endSchedule: String): String {
        try {
            val time1 = SimpleDateFormat("HH:mm", Locale.US).parse(initSchedule)
            val calendar1 = Calendar.getInstance()
            calendar1.time = time1!!
            calendar1.add(Calendar.DATE, 1)

            val time2 = SimpleDateFormat("HH:mm", Locale.US).parse(endSchedule)
            val calendar2 = Calendar.getInstance()
            calendar2.time = time2!!
            calendar2.add(Calendar.DATE, 1)

            formatter.timeZone = TimeZone.getTimeZone(Functions.userTimeZone)
            val currentTime = Calendar.getInstance()
            val dateNow = formatter.format(currentTime.time)
            @SuppressLint("SimpleDateFormat") val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
            val actualDate:Date
            actualDate = try {
                simpleDateFormat.parse(dateNow)!!
            } catch (e: Exception) { Date() }

            val now = SimpleDateFormat("HH:mm", Locale.US).format(actualDate)
            val d = SimpleDateFormat("HH:mm", Locale.US).parse(now)
            val calendar3 = Calendar.getInstance()
            calendar3.time = d!!
            calendar3.add(Calendar.DATE, 1)
            val x = calendar3.time

            // Calculate difference between two times
            val millis: Long = calendar2.time.time - x.time
            val hours = millis/(1000 * 60 * 60)
            val mins = (millis/(1000 * 60)) % 60

            return if (hours < 1)
                if (mins.toInt() < 0)
                    String.format(getString(R.string.closed_now2))
                else
                    String.format(getString(R.string.closed_soon))
            else
                String.format(getString(R.string.opened_now2)) //, "$mDia $mHorarioInicio - $mHorarioFin"
        } catch (e: ParseException) { e.printStackTrace() }
        return  ""
    }

    private fun time(time: String?): String {
        if (time.isNullOrEmpty()) return ""
        val partHora = time.split(":")
        return partHora[0] + ":" + partHora[1]
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                val strings = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                ActivityCompat.requestPermissions(this, strings, 1)
            }
        }
    }

}
