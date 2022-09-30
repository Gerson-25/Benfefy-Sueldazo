package com.syntepro.appbeneficiosbolivia.ui.explore

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.entity.service.CercaDeTi
import com.syntepro.appbeneficiosbolivia.service.NetworkService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MapsActivity : FragmentActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private var mMap: GoogleMap? = null
    private var loc: Location? = null
    var cardList: ArrayList<CercaDeTi>? = null
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment: SupportMapFragment? = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this@MapsActivity)
        val locManager = getSystemService(LOCATION_SERVICE) as LocationManager
        loc = Objects.requireNonNull(locManager).getLastKnownLocation(LocationManager.GPS_PROVIDER)
        cardList = ArrayList()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
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
        mMap?.isMyLocationEnabled = true

        // Add a marker in my location and move the camera/zoom
        val ubicacion: LatLng
        if (loc != null) {
            ubicacion = LatLng(loc!!.latitude, loc!!.longitude)
            getNear(loc!!.latitude, loc!!.longitude)
        } else {
            val la = intent.extras!!.getString(LATITUDE)?.toDouble()
            val lo = intent.extras!!.getString(LONGITUDE)?.toDouble()
            ubicacion = LatLng(la ?: 0.0, lo ?: 0.0)
            getNear(la ?: 0.0, lo ?: 0.0)
        }
        mMap?.setOnInfoWindowClickListener(this)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 14f))
        mMap?.uiSettings?.isZoomControlsEnabled = true
    }

    private fun getNear(la: Double, lo: Double) {
        val cu = CercaDeTiActivity.roomDataBase.accessDao().country
        cardList!!.clear()
        val request = retrofit.create(NetworkService::class.java).getCerca(cu.abr, la, lo)
        request.enqueue(object : Callback<ArrayList<CercaDeTi?>?> {
            override fun onResponse(call: Call<ArrayList<CercaDeTi?>?>, response: Response<ArrayList<CercaDeTi?>?>) {
                if (response.code() == 200) {
                    val ap = response.body()
                    if (ap!!.isEmpty()) {
                        cardList!!.clear()
                    } else {
                        for (ct in ap) {
                            cardList!!.add(CercaDeTi(ct?.idSucursal, ct?.idComercio, ct?.nombreComercio, ct?.urlImagen, ct?.nombreSucursal, ct?.campanas, ct?.distance, ct?.direccion, ct?.latitude, ct?.longitud))
                            val markerOptions = MarkerOptions()
                            val lat = ct?.latitude?.toDouble()
                            val lon = ct?.longitud?.toDouble()
                            markerOptions.position(LatLng(lat ?: 0.0, lon ?: 0.0))
                            mMap?.addMarker(markerOptions.title(ct?.nombreComercio).snippet(ct?.nombreSucursal).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_50)))
                        }
                    }
                } else
                    cardList!!.clear()
            }

            override fun onFailure(call: Call<ArrayList<CercaDeTi?>?>, t: Throwable) {  }
        })
    }

    override fun onInfoWindowClick(marker: Marker) {
//        startActivity(CuponComercioActivity.getIntent(this, marker.title, marker.title))
    }

    private val retrofit: Retrofit
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
            return Retrofit.Builder()
                    .baseUrl(NetworkService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
        }

    companion object {
        private const val LATITUDE = "latitude"
        private const val LONGITUDE = "longitude"
        @JvmStatic
        fun getIntent(context: Context?, latitude: String?, longitude: String?): Intent {
            return Intent(context, MapsActivity::class.java)
                    .putExtra(LATITUDE, latitude)
                    .putExtra(LONGITUDE, longitude)
        }
    }
}