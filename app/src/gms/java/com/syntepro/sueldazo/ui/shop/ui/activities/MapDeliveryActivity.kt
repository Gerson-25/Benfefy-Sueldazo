package com.syntepro.sueldazo.ui.shop.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.base.BaseActivity
import com.syntepro.sueldazo.ui.shop.model.DeliveryPrices
import com.syntepro.sueldazo.ui.shop.model.DispatchPoints
import com.syntepro.sueldazo.utils.Functions

class MapDeliveryActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var currentLocation: Location? = null
    private var locationDeliverySelected: LatLng? = null
    private var maxDistance: Double = 0.0
    private var closerLocation: DispatchPoints? = null
    private var lastDistance = 0.0f
    private var dispatchPoints: List<DispatchPoints>? = null
    private var deliveryPrices: List<DeliveryPrices>? = null
    private var isValidLocationDelivery: Boolean = false
    private var deliveryTotal: Double? = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_delivery)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            currentLocation = extras.get("currentLocation") as? Location
            dispatchPoints = extras.getParcelableArrayList("dispatchPoints")
            deliveryPrices = extras.getParcelableArrayList("deliveryPrices")
            maxDistance = extras.getDouble("distance")

            val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
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
        mMap.uiSettings.isZoomGesturesEnabled = true

        currentLocation?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            locationDeliverySelected = latLng
            val markerOptions = MarkerOptions().position(latLng)
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
            mMap.addMarker(markerOptions)
            mMap.addCircle(
                    CircleOptions()
                            .center(latLng)
                            .radius(maxDistance)
                            .strokeWidth(2f)
                            .strokeColor(Color.BLUE)
                            .fillColor(Color.argb(50, 65, 105, 225))
            )

            isValidLocationDelivery = getCloserLocation()
            if (!isValidLocationDelivery)
                Functions.showWarning(this@MapDeliveryActivity, getString(R.string.invalid_delivery_zone))
            else {
                val price = deliveryPrices?.find { dp -> lastDistance >= dp.startStripe && lastDistance < dp.endStripe }
                deliveryTotal = price?.charge
            }
        }

        mMap.setOnMapClickListener { point ->
            locationDeliverySelected = point
            mMap.clear()
            mMap.addMarker(MarkerOptions()
                    .position(point)
                    .snippet(""))
            mMap.addCircle(
                    CircleOptions()
                            .center(point)
                            .radius(50.0)
                            .strokeWidth(2f)
                            .strokeColor(Color.BLUE)
                            .fillColor(Color.argb(50, 65, 105, 225))
            )

            isValidLocationDelivery = getCloserLocation()
            if (!isValidLocationDelivery)
                Functions.showWarning(this@MapDeliveryActivity, getString(R.string.invalid_delivery_zone))
            else {
                val price = deliveryPrices?.find { lastDistance >= it.startStripe && lastDistance < it.endStripe }
                deliveryTotal = price?.charge
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("validLocation", isValidLocationDelivery)
        intent.putExtra("locationDeliverySelected", locationDeliverySelected)
        intent.putExtra("dispatchPointId", closerLocation?.idCommerceDispathPoint)
        intent.putExtra("deliveryPrice", deliveryTotal)
        intent.putExtra("distance", lastDistance)
        setResult(Activity.RESULT_OK, intent)
        this.finish()
    }

    private fun getCloserLocation(): Boolean {
        dispatchPoints?.let {
            locationDeliverySelected?.let { ll ->
                val userLocation = Location("")
                userLocation.latitude = ll.latitude
                userLocation.longitude = ll.longitude
                for (dp in it) {
                    val temp = Location("")
                    temp.latitude = dp.latitude
                    temp.longitude = dp.longitude

                    val distance = userLocation.distanceTo(temp) / 1000
                    if (closerLocation == null) {
                        if (distance <= maxDistance) {
                            lastDistance = distance
                            closerLocation = dp
                        }
                    } else {
                        if (distance < lastDistance && distance <= maxDistance) {
                            lastDistance = distance
                            closerLocation = dp
                        }
                    }
                }
            } ?: run {
                return false
            }
            return closerLocation != null
        } ?: run {
            return false
        }
    }

}