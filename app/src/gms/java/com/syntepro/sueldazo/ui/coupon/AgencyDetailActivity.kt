package com.syntepro.sueldazo.ui.coupon

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.base.BaseActivity
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.agency.model.AgencyDetailRequest
import com.syntepro.sueldazo.ui.agency.model.AgencyDetailResponse
import com.syntepro.sueldazo.ui.agency.viewModel.AgencyViewModel
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.gms.activity_detail_agency.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AgencyDetailActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var agencyViewModel: AgencyViewModel
    private lateinit var mMap: GoogleMap
    private val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)
    private var branchId: String? = ""
    private var phoneNumber: String? = ""

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

        // Obtain the supportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        mMap.isMyLocationEnabled = true
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
            try {
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
                mMap.addMarker(markerOptions.title(it.agencName).snippet(it.address).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_50)))
                val latLng = LatLng(latitude, longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16F))

                scheduleId.text = "${time(it.openingTime)} - ${time(it.closingTime)}"
                scheduleStatusId.text = Functions.fromHtml(getStatus(time(it.openingTime), time(it.openingTime)))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun openCall(number: String?) {
        if(number.isNullOrEmpty()) return
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+$number"))
        this.startActivity(intent)
    }

    private fun time(time: String?): String {
        if (time.isNullOrEmpty()) return ""
        val partHora = time.split(":")
        return partHora[0] + ":" + partHora[1]
    }

    private fun getStatus(opening: String?, closing: String?): String {
        if (opening.isNullOrEmpty() || closing.isNullOrEmpty()) return ""
        try {
            val time1 = SimpleDateFormat("HH:mm", Locale.US).parse(opening)
            val calendar1 = Calendar.getInstance()
            calendar1.time = time1!!
            calendar1.add(Calendar.DATE, 1)

            val time2 = SimpleDateFormat("HH:mm", Locale.US).parse(closing)
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

            Log.e("Difference", "${hours}:${mins}")

            return if (hours < 1)
                if (mins.toInt() < 0)
                    String.format(getString(R.string.closed_now2))
                else
                    String.format(getString(R.string.closed_soon))
            else
                String.format(getString(R.string.opened_now2))
        } catch (e: ParseException) { e.printStackTrace() }
        return  ""
    }

    //    private fun getAgency() {
//        val call: Call<ArrayList<Sucursal>> = getRetrofit().create(NetworkService::class.java).getSucursal(Functions.userCountry, sucursalId)
//
//        call.enqueue(object : Callback<ArrayList<Sucursal>> {
//            override fun onFailure(call: Call<ArrayList<Sucursal>>, t: Throwable) {
//                Log.e("Error en la consulta", "-> $t")
//            }
//
//            @SuppressLint("SetTextI18n")
//            override fun onResponse(call: Call<ArrayList<Sucursal>>, response: Response<ArrayList<Sucursal>>) {
//                if (response.code() == 200) {
//                    val ap: ArrayList<Sucursal> = response.body()!!
//                    if (ap.isNotEmpty()) {
//                        for (su in ap) {
//                            commerceId.text = su.nombre
//                            agencyId.text = nombre
//                            agencyLocationId.text = "$direccion, $provincia, $departamento"
//
//                            if (telefono.equals("")) agencyPhoneId.text = getString(R.string.tel_no_registrado)
//                            else agencyPhoneId.text = telefono
//
//                            val markerOptions = MarkerOptions()
//                            markerOptions.position(LatLng(latitud, longitud))
//                            mMap.addMarker(markerOptions.title(nombre).snippet(direccion).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_50)))
//                            val latLng = LatLng(latitud, longitud)
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16F))
//                            horarios = su.horarios!!
//                            if (horarios.isNotEmpty()) {
//                                try {
//                                    val calendar = Calendar.getInstance()
//                                    val dayNow = calendar.get(Calendar.DAY_OF_WEEK)
//                                    val day = horarios.find { it.diaSemana == dayNow - 1 }
//                                    if (day != null) {
//                                        scheduleId.text = "${time(day.horaApertura.toString())} - ${time(day.horaCierre.toString())}"
//                                        scheduleStatusId.text = Functions.fromHtml(getStatus(time(day.horaApertura.toString()), time(day.horaCierre.toString())))
//                                    } else {
//                                        scheduleId.text = "No hay horario disponible este día"
//                                        scheduleStatusId.visibility = View.GONE
//                                    }
//                                } catch (e: Exception) {
//                                    scheduleId.text = "No hay horario disponible este día"
//                                    scheduleStatusId.visibility = View.GONE
//                                }
//                            } else {
//                                scheduleId.text = "No hay horarios disponibles"
//                                scheduleStatusId.visibility = View.GONE
//                            }
//                        }
//                    }
//                }
//            }
//        })
//    }

//    private fun getRetrofit(): Retrofit {
//        val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
//            this.level = HttpLoggingInterceptor.Level.BODY
//        }
//        val client : OkHttpClient = OkHttpClient.Builder().apply {
//            this.addInterceptor(interceptor)
//        }.build()
//
//        return Retrofit.Builder()
//                .baseUrl(NetworkService.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
//                .build()
//    }
}
