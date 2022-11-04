package com.syntepro.sueldazo.ui.coupon

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.base.BaseActivity
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.database.DataBaseAdapter
import com.syntepro.sueldazo.entity.app.Departamento
import com.syntepro.sueldazo.room.database.RoomDataBase
import com.syntepro.sueldazo.room.entity.CountryUser
import com.syntepro.sueldazo.ui.agency.model.AgencyResponse
import com.syntepro.sueldazo.ui.agency.ui.adapters.AgencyAdapter
import com.syntepro.sueldazo.ui.agency.viewModel.AgencyViewModel
import com.syntepro.sueldazo.ui.commerce.model.CommerceAgencyRequest
import com.syntepro.sueldazo.ui.coupon.model.CouponAgencyRequest
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.gms.activity_agency.*
import java.util.*
import javax.inject.Inject

class AgencyActivity: BaseActivity() {

    @Inject
    lateinit var agencyAdapter: AgencyAdapter

    private lateinit var agencyViewModel: AgencyViewModel
    private val adapter by lazy { DataBaseAdapter(this.applicationContext) }
    private val roomDataBase by lazy { RoomDataBase.getRoomDatabase(this) }
    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private var commerceId: String? = ""
    private var commerceName: String? = ""
    private var commerceImage: String? = ""
    private var couponId: String? = ""
    private var provenanceId: Int = 0
    private var cu: CountryUser? = null
    private var lat = 0.0
    private var lon = 0.0
    private var agencyOriginalList: List<AgencyResponse> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_agency)

        // Toolbar
        val myToolbar = findViewById<Toolbar>(R.id.back_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.sucursales)

        agencyViewModel = viewModel(viewModelFactory) {
            observe(commerceAgency, ::handleCommerceAgencies)
            observe(couponAgency, ::handleCouponAgencies)
            failure(failure, ::handleError)
        }

        // Extras
        val extras = intent.extras
        if (extras != null) {
            commerceId = extras.getString("commerceId")
            commerceName = extras.getString("commerceName")
            commerceImage = extras.getString("commerceImage")
            couponId = extras.getString("couponId")
            provenanceId = extras.getInt("provenance")
            commerceNameId.text = commerceName
            Functions.showRoundedImage(commerceImage, pictureId)
        }

        // SQLite
        adapter.createDatabase()

        // Department List
        adapter.open()
        cu = roomDataBase.accessDao().country
        val country = adapter.getCountryConfiguration(cu?.pais)
        val dpts = adapter.getInfoDepto(country.idPais) as List<Departamento>
        getAllDepartments(dpts)
        adapter.close()

        initList()
        fetchLocation()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        RoomDataBase.destroyInstance()
        cu = null
        listId.adapter = null
        finish()
        super.onDestroy()
    }

    private fun getAllDepartments(d: List<Departamento?>) {
        val nomDept = ArrayList<String>()
        nomDept.add("Ver Todas")
        for (i in d.indices) nomDept.add(d[i]!!.nombre)

        filterSpinner.adapter = ArrayAdapter(this@AgencyActivity, R.layout.spinner_item, nomDept)

        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (position != 0) {
                    val filterTemp = agencyOriginalList.filter { s -> s.state == filterSpinner.adapter.getItem(position).toString() }
                    agencyAdapter.collection = filterTemp
                } else agencyAdapter.collection = agencyOriginalList
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // empty
            }
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            isDistance = false
            if (provenanceId == 0) loadCouponAgency(lat, lon)
            else if (provenanceId == 1) loadCommerceAgency(lat, lon)
            return
        }
        fusedLocationClient!!.lastLocation
                .addOnSuccessListener(this) { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        isDistance = true
                        lat = location.latitude
                        lon = location.longitude
                    } else {
                        isDistance = false
                        lat = 0.0
                        lon = 0.0
                    }
                    if (provenanceId == 0) loadCouponAgency(lat, lon)
                    else if (provenanceId == 1) loadCommerceAgency(lat, lon)
                }
    }

    private fun initList() {
        listId.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        listId.layoutManager = linearLayoutManager
        listId.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        listId.adapter = agencyAdapter
        agencyAdapter.setActivity(this)
    }

    private fun loadCommerceAgency(lat: Double, lon: Double) {
        val request = CommerceAgencyRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                recordsNumber = Constants.LIST_PAGE_SIZE,
                pageNumber = 1,
                idCommerce = commerceId ?: "",
                longitude = lon,
                latitude = lat
        )
        agencyViewModel.loadCommerceAgency(request)
    }

    private fun loadCouponAgency(lat: Double, lon: Double) {
        val request = CouponAgencyRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                idCoupon = couponId ?: "",
                latitude = lat,
                longitude = lon,
                justOne = false
        )
        agencyViewModel.loadCouponAgency(request)
    }

    private fun handleCommerceAgencies(response: BaseResponse<List<AgencyResponse>>?) {
        agencyAdapter.collection = response?.data.orEmpty()
        agencyOriginalList = response?.data.orEmpty()
        if(response?.data.isNullOrEmpty())
            Log.e("Ok", "EmptyList")
    }

    private fun handleCouponAgencies(response: BaseResponse<List<AgencyResponse>>?) {
        agencyAdapter.collection = response?.data.orEmpty()
        agencyOriginalList = response?.data.orEmpty()
        if(response?.data.isNullOrEmpty())
            Log.e("Ok", "EmptyList")
    }

    fun openAgency(model: AgencyResponse) {
        val id = if (model.idAgency.isNullOrEmpty()) model.sucursalID else model.idAgency
        val intent = Intent(this@AgencyActivity, AgencyDetailActivity::class.java)
        intent.putExtra("branchId", id)
        startActivity(intent)
    }

    companion object {
        var isDistance = false
    }
}