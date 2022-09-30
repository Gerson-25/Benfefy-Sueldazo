package com.syntepro.appbeneficiosbolivia.ui.menu

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.database.DataBaseAdapter
import com.syntepro.appbeneficiosbolivia.entity.app.Departamento
import com.syntepro.appbeneficiosbolivia.entity.app.Pais
import com.syntepro.appbeneficiosbolivia.entity.app.Provincia
import com.syntepro.appbeneficiosbolivia.entity.service.*
import com.syntepro.appbeneficiosbolivia.room.database.RoomDataBase
import com.syntepro.appbeneficiosbolivia.service.NetworkService2
import com.syntepro.appbeneficiosbolivia.service.RetrofitClientInstance
import com.syntepro.appbeneficiosbolivia.ui.home.adapter.CustomAdapter
import com.syntepro.appbeneficiosbolivia.ui.home.model.StatesResponse
import com.syntepro.appbeneficiosbolivia.ui.shop.model.DeliveryDetail
import com.syntepro.appbeneficiosbolivia.ui.shop.model.DeliveryPrices
import com.syntepro.appbeneficiosbolivia.ui.shop.model.DispatchPoints
import com.syntepro.appbeneficiosbolivia.ui.shop.model.UserArticleGift
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities.DeliveryInformationActivity
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities.GiftCouponInformationActivity
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities.InvoiceActivity
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities.MapDeliveryActivity
import com.syntepro.appbeneficiosbolivia.ui.sudamericana.model.SurveyRequest
import com.syntepro.appbeneficiosbolivia.ui.sudamericana.ui.activities.SurveyActivity
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.getCountryAbbreviation
import com.syntepro.appbeneficiosbolivia.utils.Helpers
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.gms.activity_edit_profile2.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditProfileActivity2 : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val roomDataBase by lazy { RoomDataBase.getRoomDatabase(this) }
    private val permissionCode = 101
    private val adapter by lazy { DataBaseAdapter(this.applicationContext) }
    private var nomCountry: ArrayList<String> = arrayListOf()
    private val flags = intArrayOf(R.drawable.sv, R.drawable.bo, R.drawable.gt)
    private var mStorage: StorageReference? = null
    private val mCalendar = Calendar.getInstance()
    private val mDeliveryCalendar = Calendar.getInstance()
    private var userQuestionnaire: SurveyRequest? = null
    private var mImageUri: Uri? = null
    private var mDateSelected: Date? = Date()
    private var deliveryDateSelected: Date? = Date()
    private var currentImage: String? = null
    private var provenance: Int = 0
    private var insuredType: Int = 0
    private var id: String? = ""
    private var selectedCountry: String? = null
    private var departmentSelected: String? = ""
    private var provinceSelected: String? = ""
    private var dispatchPoints: List<DispatchPoints>? = null
    private var deliveryPrices: List<DeliveryPrices>? = null
    private var locationDeliverySelected: LatLng? = null
    private var maxDistance: Double = 0.0
    private var closerLocation: DispatchPoints? = null
    private var dispatchPointSelected: String? = ""
    private var lastDistance = 0.0f
    private var state: String? = null
    private var city: String? = null
    private var includeDelivery: Boolean? = false
    private var deliveryTotal: Double? = 0.0
    private var articleOriginalPrice: Double? = 0.0
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile2)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.back_toolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.editar_perfil)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            provenance = extras.getInt("provenance")
            insuredType = extras.getInt("insuranceType")
            id = extras.getString("id")
            articleOriginalPrice = extras.getDouble("articlePrice")
            dispatchPoints = extras.getParcelableArrayList("dispatchPoints")
            deliveryPrices = extras.getParcelableArrayList("deliveryPrices")
            maxDistance = extras.getDouble("distance")
            val deliveryAllows = extras.getBoolean("allowDelivery", false)
            if (!deliveryAllows) deliveryMaterialCard.visibility = View.GONE
            supportActionBar!!.title = resources.getString(R.string.complete_your_data)
            when (provenance) {
                1 -> {
                    giftMaterialCard.visibility = View.GONE
                    deliveryMaterialCard.visibility = View.GONE
                }
                2 -> {
                    imageContainer.visibility = View.GONE
                    countrySpinner.visibility = View.GONE
                    locationInfo.visibility = View.GONE
                    dataConfirm.visibility = View.VISIBLE
                    personalData.visibility = View.VISIBLE
                    Constants.countryStates?.let { getDeliveryStates(it) }
                }
                3 -> {
                    imageContainer.visibility = View.GONE
                    countrySpinner.visibility = View.GONE
                    locationInfo.visibility = View.VISIBLE
                    dataConfirm.visibility = View.VISIBLE
                    personalData.visibility = View.VISIBLE
                    giftMaterialCard.visibility = View.GONE
                    adapter.open()
                    val country = adapter.getCountryInfoAbr(
                        Constants.userProfile?.actualCountry
                            ?: "BO"
                    )
                    val prfBO = "+" + country.codigoArea
                    prefixId.text = prfBO
                    department.text = country.depto
                    province.text = country.muni
                    deliveryPrefixId.text = prfBO
                    deliveryDepartment.text = country.depto
                    deliveryProvince.text = country.muni
                    val departments = adapter.getInfoDepto(country.idPais) as List<Departamento?>
                    getAllDepartments(departments)
                    adapter.close()
                }
                else -> getPermissions()
            }
        }

        // Storage
        mStorage = FirebaseStorage.getInstance().reference

        // Get Countries
        adapter.open()
        val country = adapter.infoCountry as List<Pais?>
        for (i in country.indices) {
            nomCountry.add(country[i]!!.nombre)
        }

        // Country Spinner
        countrySpinner.adapter = CustomAdapter(this, flags, nomCountry)

        adapter.close()

        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCountry = Constants.userProfile?.country
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                try {
                    parent?.let {
                        adapter.open()
                        val abbreviation = getCountryAbbreviation(nomCountry[it.selectedItemPosition])
                        val c = adapter.getCountryInfoAbr(abbreviation)
                        val prfBO = "+" + c.codigoArea
                        prefixId.text = prfBO
                        department.text = c.depto
                        province.text = c.muni
                        selectedCountry = c.abreviacion

                        val departments = adapter.getInfoDepto(c.idPais) as List<Departamento?>
                        getAllDepartments(departments)
                        adapter.close()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // Gender Spinner
        val gender = arrayOf(getString(R.string.male), getString(R.string.female))
        genderSpinner.adapter = ArrayAdapter(this, R.layout.spinner_item, gender)

        // Marital Status Spinner
        val marital = arrayOf(getString(R.string.soltero), getString(R.string.comprometido), getString(R.string.casado), getString(R.string.separado), getString(R.string.divorciado), getString(R.string.viudo))
        maritalStatusSpinner.adapter = ArrayAdapter(this, R.layout.spinner_item, marital)

        if (!Functions.isDarkTheme(this@EditProfileActivity2) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            giftMaterialCard.setCardBackgroundColor(getColor(R.color.gray_card_benefit))
            deliveryMaterialCard.setCardBackgroundColor(getColor(R.color.gray_card_benefit))
        }

        // Show Data
        showData()

        dateField.setOnClickListener {
            DatePickerDialog(
                this, { _, year, monthOfYear, dayOfMonth ->
                mCalendar.set(Calendar.YEAR, year)
                mCalendar.set(Calendar.MONTH, monthOfYear)
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel()
            }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        deliveryDateField.setOnClickListener {
            DatePickerDialog(
                this, { _, year, monthOfYear, dayOfMonth ->
                mDeliveryCalendar.set(Calendar.YEAR, year)
                mDeliveryCalendar.set(Calendar.MONTH, monthOfYear)
                mDeliveryCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                if (Date().before(mDeliveryCalendar.time)) updateDeliveryLabel() else Functions.showWarning(this@EditProfileActivity2, "Fecha invalida de delivery.")
            }, mDeliveryCalendar.get(Calendar.YEAR), mDeliveryCalendar.get(Calendar.MONTH), mDeliveryCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        profileImage!!.setOnClickListener {
            startActivityForResult(Helpers.getPickImageChooserIntent(packageManager, externalCacheDir!!), 200)
        }

        editPhoto.setOnClickListener { profileImage.callOnClick() }

        giftedItem.setOnClickListener {
            if (giftedItem.isChecked) giftData.visibility = View.VISIBLE
            else giftData.visibility = View.GONE
        }

        deliveryItem.setOnClickListener {
            if (deliveryItem.isChecked) {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@EditProfileActivity2)
                if (Functions.isLocationEnabled(this@EditProfileActivity2)) fetchLocation()
                deliveryData.visibility = View.VISIBLE
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.cancel_delivery))
                builder.setPositiveButton(getString(R.string.accept)) { _: DialogInterface?, _: Int ->
                    deliveryItem.isChecked = false
                    deliveryData.visibility = View.GONE
                    save.isEnabled = true
                    clearDelivery()
                }
                builder.setNegativeButton(getString(R.string.cancel)) { dialog: DialogInterface, _: Int ->
                    deliveryItem.isChecked = true
                    save.isEnabled = false
                    dialog.cancel()
                }
                val dialog = builder.create()
                dialog.show()
            }
        }

        giftInformation.setOnClickListener { callIntent<GiftCouponInformationActivity> { } }

        deliveryInformation.setOnClickListener { callIntent<DeliveryInformationActivity> { } }

        editDeliveryLocation.setOnClickListener {
            callIntent<MapDeliveryActivity>(922) {
                this.putExtra("currentLocation", if (this@EditProfileActivity2::currentLocation.isInitialized) currentLocation else null)
                this.putParcelableArrayListExtra(
                    "dispatchPoints",
                    ArrayList(
                        dispatchPoints
                            ?: arrayListOf()
                    )
                )
                this.putParcelableArrayListExtra(
                    "deliveryPrices",
                    ArrayList(
                        deliveryPrices
                            ?: arrayListOf()
                    )
                )
                this.putExtra("distance", maxDistance)
            }
        }

        save.setOnClickListener {
            save.text = ""
            progress_bar_accept.visibility = View.VISIBLE
            when (provenance) {
                1 -> saveData()
                2 -> {
                    when {
                        giftedItem.isChecked && deliveryItem.isChecked -> validateDeliveryGift()

                        giftedItem.isChecked -> validateArticleGiftData()

                        deliveryItem.isChecked -> validateDelivery()

                        else -> validateArticleData()
                    }
                }
                3 -> validateInsuredData()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val getImage = externalCacheDir
            val imageUri = Helpers.getPickImageResultUri(data, getImage!!)

            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            var requirePermissions = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                Helpers.isUriRequiresPermissions(imageUri!!, contentResolver)
            ) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            }

            if (!requirePermissions) {
                CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAutoZoomEnabled(true)
                    .setRequestedSize(Constants.IMAGE_PROFILE_SIZE, Constants.IMAGE_PROFILE_SIZE)
                    .setAspectRatio(1, 1)
                    .start(this)

                Picasso.get()
                    .load(mImageUri)
                    .resize(Constants.NEW_IMAGE_SIZE, Constants.NEW_IMAGE_SIZE)
                    .centerCrop()
                    .into(profileImage)
            }
        } else if (requestCode == 925 && resultCode == Activity.RESULT_OK) {
            userQuestionnaire = data?.getSerializableExtra("model") as? SurveyRequest
            updateData()
        } else if (requestCode == 925 && resultCode == Activity.RESULT_CANCELED)
            showError()
        else if (requestCode == 387 && resultCode == Activity.RESULT_OK) {
            includeDelivery = data?.getBooleanExtra("includeDelivery", false)
            updateData()
        } else if (requestCode == 387 && resultCode == Activity.RESULT_CANCELED)
            showError()
        else if (requestCode == 922 && resultCode == Activity.RESULT_OK) {
            mMap.clear()
            locationDeliverySelected = data?.extras?.get("locationDeliverySelected") as? LatLng
            locationDeliverySelected?.let {
                mMap.addMarker(
                    MarkerOptions()
                        .position(it)
                        .snippet("")
                )
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 17f))
            }
            val validDelivery = data?.getBooleanExtra("validLocation", false)
            dispatchPointSelected = data?.getStringExtra("dispatchPointId")
            deliveryTotal = data?.getDoubleExtra("deliveryPrice", 0.0)
            lastDistance = data?.getFloatExtra("distance", 0.0f) ?: 0.0f
            getSchedules(dispatchPointSelected)
            validDelivery?.let {
                if (it) {
                    blockedView.visibility = View.GONE
                    deliveryLocationInfo.visibility = View.VISIBLE
                } else {
                    blockedView.visibility = View.VISIBLE
                    deliveryLocationInfo.visibility = View.GONE
                }
                save.isEnabled = it
            }
        } else if (requestCode == 922 && resultCode == Activity.RESULT_CANCELED)
            showError()

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                mImageUri = result.uri
                profileImage!!.setImageURI(mImageUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            permissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Functions.isLocationEnabled(this@EditProfileActivity2)) fetchLocation()
                } else {
                    clearDelivery()
                    deliveryItem.isChecked = false
                    deliveryData.visibility = View.GONE
                }
            }
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
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isZoomGesturesEnabled = false

        if (this::currentLocation.isInitialized) {
            val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            locationDeliverySelected = latLng
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        }
//        val markerOptions = MarkerOptions().position(latLng)
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
//        mMap.addMarker(markerOptions)
//        mMap.addCircle(
//                CircleOptions()
//                        .center(latLng)
//                        .radius(50.0)
//                        .strokeWidth(2f)
//                        .strokeColor(Color.BLUE)
//                        .fillColor(Color.argb(50, 65, 105, 225))
//        )

        val enabledDelivery = getCloserLocation()
        save.isEnabled = enabledDelivery

        if (enabledDelivery) {
            getSchedules(dispatchPointSelected)
            val price = deliveryPrices?.find { lastDistance >= it.startStripe && lastDistance < it.endStripe }
            deliveryTotal = price?.charge
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode
            )
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {
            if (it != null) currentLocation = it
            // Obtain the supportMapFragment and get notified when the map is ready to be used.
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
    }

    private fun updateLabel() {
        dateField.setText(Helpers.dateToStr(mCalendar.time, DateFormat.DATE_FIELD))
        mDateSelected = mCalendar.time
    }

    private fun updateDeliveryLabel() {
        deliveryDateField.setText(Helpers.dateToStr(mDeliveryCalendar.time, DateFormat.DATE_FIELD))
        deliveryDateSelected = mDeliveryCalendar.time
    }

    private fun getAllDepartments(list: List<Departamento?>) {
        list.let { dpt ->
            val nomDept = ArrayList<String>()
            for (i in dpt.indices) {
                nomDept.add(dpt[i]?.nombre ?: "")
            }
            departmentSpinner.adapter = ArrayAdapter(this@EditProfileActivity2, R.layout.spinner_item, nomDept)

            // Department Selection
            if (!departmentSelected.isNullOrEmpty()) {
                departmentSpinner.setSelection(nomDept.indexOf(departmentSelected))
                departmentSelected = ""
            }
            departmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, id: Long) {
                    getAllProvinces(dpt[position]?.idDepartamento ?: 0)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) { }
            }
        }
    }

    private fun getAllProvinces(departmentId: Int) {
        adapter.open()

        val prov: List<Provincia?> = adapter.getInfoPrv(departmentId) as List<Provincia?>
        val nomProv = ArrayList<String>()
        for (i in prov.indices) {
            nomProv.add(prov[i]!!.nombre)
        }
        provinceSpinner.adapter = ArrayAdapter(this@EditProfileActivity2, R.layout.spinner_item, nomProv)

        // Province Selection
        if (!provinceSelected.isNullOrEmpty()) {
            provinceSpinner.setSelection(nomProv.indexOf(provinceSelected))
            provinceSelected = ""
        }

        adapter.close()
    }

    private fun getDeliveryStates(states: MutableList<StatesResponse>) {
        deliveryDepartmentSpinner.adapter = ArrayAdapter(this@EditProfileActivity2, R.layout.spinner_item, states)

        deliveryDepartmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                parent?.let {
                    val selectedState = it.selectedItem as StatesResponse
                    state = selectedState.idState
                    getDeliveryCities(selectedState.cities)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
    }

    private fun getDeliveryCities(cities: List<City>?) {
        cities?.let {
            deliveryProvinceSpinner.adapter = ArrayAdapter(this@EditProfileActivity2, R.layout.spinner_item, it)

            deliveryProvinceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    parent?.let { p ->
                        city = it[position].idCity
                        Log.e("CitySelected", "position:$position - City: ${it[position].name}")
                        Log.e("CitySelected", "${p.selectedItem}")
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) { }
            }
        }
    }

    private fun showData() {
        Constants.userProfile?.let {
            it.photoUrl?.let { image ->
                Functions.showRoundedImage(image, profileImage)
                currentImage = image
            } ?: run {
                Functions.showRoundedImage(Constants.PROFILE_IMAGE, profileImage)
            }
            nameField.setText(it.names)
            lastNameField.setText(it.lastNames)
            documentField.setText(it.idDocument)
            phoneField.setText(it.phone)
            mDateSelected = format.parse(it.birthDate ?: "")
            dateField.setText(Helpers.dateToStr(mDateSelected ?: Date(), DateFormat.DATE_FIELD))
            it.gender?.let { g ->
                val adapter = genderSpinner.adapter as ArrayAdapter<String>
                val position = adapter.getPosition(g)
                genderSpinner.setSelection(position)
            }
            it.maritalStatus?.let { m ->
                val adapter = maritalStatusSpinner.adapter as ArrayAdapter<String>
                val position = adapter.getPosition(m)
                maritalStatusSpinner.setSelection(position)
            }
            it.countryName?.let { c -> countrySpinner!!.setSelection(nomCountry!!.indexOf(c)) }
            departmentSelected = it.state
            provinceSelected = it.city
            fromField.setText("${it.names} ${it.lastNames}")
        }
    }

    private fun getPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            Functions.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, 1)
            Functions.requestPermission(this, Manifest.permission.CAMERA, 2)
            Functions.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 3)
        }
    }

    /**
     * Save Data to FireStore
     */
    private fun saveData() {
        // Store profile picture
        if (mImageUri != null) {
            val imageName = FirebaseAuth.getInstance().uid
            val filePath = mStorage!!.child(Constants.PROFILE_IMAGES_PATH).child(imageName!!)
            filePath.putFile(mImageUri!!).addOnSuccessListener {
                val thumbPath = File(mImageUri!!.path!!)
                try {
                    val thumbBitmap = Compressor(applicationContext)
                        .setMaxWidth(Constants.MAX_THUMB_WIDTH)
                        .setMaxHeight(Constants.MAX_THUMB_HEIGHT)
                        .compressToBitmap(thumbPath)
                    val baos = ByteArrayOutputStream()
                    thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val thumbByte = baos.toByteArray()

                    val thumbRef = mStorage!!.child(Constants.PROFILE_THUMBS_IMAGE_PATH)
                        .child(imageName)
                    thumbRef.putBytes(thumbByte).addOnCompleteListener {
                        filePath.downloadUrl.addOnSuccessListener { uri ->
                            thumbRef.downloadUrl.addOnSuccessListener { uri2 ->
                                saveDataWS(uri.toString(), uri2.toString()) {
                                    if (it) this.finish()
                                    else showError()
                                }
                            }
                        }
                    }
                } catch (e: IOException) {
                    showError()
                    Log.e("Error", "${e.message}")
                }
            }.addOnFailureListener { e ->
                showError()
                Log.e("Error", "${e.message}")
            }
        } else {
            saveDataWS(currentImage, null) {
                if (it) this.finish()
                else showError()
            }
        }
    }

    private fun saveDataWS(pictureName: String?, thumbName: String?, completion: (Boolean) -> Unit) {
        val request = with(UserUpdateRequest()) {
            country = Constants.userProfile?.actualCountry ?: "BO"
            language = 1
            idUser = Constants.userProfile?.idUser
            idUserFirebase = Constants.userProfile?.idUserFirebase
            photoUrl = pictureName ?: Constants.PROFILE_IMAGE
            names = nameField.text.toString()
            lastNames = lastNameField.text.toString()
            phone = phoneField.text.toString().replace("-", "")
            birthDate = format.format(mDateSelected ?: Date())
            gender = genderSpinner.selectedItem.toString()
            martialStatus = maritalStatusSpinner.selectedItem.toString()
            if (provenance == 1) {
                countryUser = selectedCountry
                state = departmentSpinner.selectedItem.toString()
                city = provinceSpinner.selectedItem.toString()
            } else {
                countryUser = Constants.userProfile?.country ?: "BO"
                state = Constants.userProfile?.state
                city = Constants.userProfile?.city
            }
            idDocument = documentField.text.toString()
            this
        }
        val apiServe = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2)
        val inst = apiServe.create(NetworkService2::class.java)
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        scopeMainThread.launch {
            try {
                val ret = inst.updateUser(request)
                if (ret.isSuccessful) {
                    val response: BaseResponseBoolean = ret.body()!!
                    if (response.isSuccess) {
                        savePersistentData(pictureName)
                        completion(true)
                    } else {
                        showError()
                        completion(false)
                        Toast.makeText(
                            this@EditProfileActivity2,
                            response.description
                                ?: "",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    showError()
                    completion(false)
                    Toast.makeText(this@EditProfileActivity2, ret.message(), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                completion(false)
                Toast.makeText(this@EditProfileActivity2, "Ocurrió un error.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateData() {
        saveDataWS(currentImage, null) {
            if (it) {
                val intent = Intent()
                intent.putExtra("update", true)
                userQuestionnaire?.let { uq -> intent.putExtra("model", uq) }
                if (giftedItem.isChecked) {
                    val gift = UserArticleGift(
                        nameSendingUser = fromField.text.toString(),
                        nameReceivingUser = toField.text.toString(),
                        emailReceivingUser = recipientField.text.toString(),
                        dedicatory = dedicationField.text.toString()
                    )
                    intent.putExtra("giftModel", gift)
                }
                if (deliveryItem.isChecked) {
                    val delivery = DeliveryDetail(
                        direction = addressField.text.toString(),
                        latitude = locationDeliverySelected!!.latitude,
                        longitude = locationDeliverySelected!!.longitude,
                        idProvince = city ?: "",
                        contactName = receivedNameField.text.toString(),
                        contactPhone = deliveryPhoneField.text.toString(),
                        contactEmail = deliveryMailField.text.toString(),
                        idDispatchPoint = dispatchPointSelected ?: "",
                        deliveryDate = format.format(deliveryDateSelected ?: Date()),
                        startTime = startRangeSpinner.selectedItem.toString(),
                        endTime = endRangeSpinner.selectedItem.toString(),
                        deliveryPaid = includeDelivery ?: false,
                        deliveryAmount = deliveryTotal ?: 0.0,
                        itemsPrice = articleOriginalPrice ?: 0.0
                    )
                    intent.putExtra("deliveryModel", delivery)
                }
                setResult(Activity.RESULT_OK, intent)
                this.finish()
            } else showError()
        }
    }

    private fun getSchedules(id: String?) {
        id?.let {
            val request = ScheduleRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                idDispatchPoint = it,
                date = format.format(deliveryDateSelected ?: Date())
            )
            val apiServe = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2)
            val inst = apiServe.create(NetworkService2::class.java)
            val job = Job()
            val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
            scopeMainThread.launch {
                try {
                    val ret = inst.getDispatchPointSchedule(request)
                    if (ret.isSuccessful) {
                        val response = ret.body()!!
                        showSchedule(response.data)
                    } else
                        Log.e("Error", ret.message())
                } catch (e: Exception) {
                    Log.e("Error", "Ocurrió un error : ${e.printStackTrace()}")
                }
            }
        }
    }

    private fun validateArticleData() {
        when {
            nameField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = getString(R.string.required_label)
                lastNameLayout.error = null
                phoneLayout.error = null
            }
            lastNameField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = getString(R.string.required_label)
                phoneLayout.error = null
            }
            phoneField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = getString(R.string.required_label)
            }
            else -> {
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                updateData()
            }
        }
    }

    private fun validateArticleGiftData() {
        when {
            nameField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = getString(R.string.required_label)
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
            }
            lastNameField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = getString(R.string.required_label)
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
            }
            phoneField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = getString(R.string.required_label)
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
            }
            fromField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = getString(R.string.required_label)
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
            }
            toField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = getString(R.string.required_label)
                recipientLayout.error = null
                dedicationLayout.error = null
            }
            recipientField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = getString(R.string.required_label)
                dedicationLayout.error = null
            }
            !Patterns.EMAIL_ADDRESS.matcher(recipientField.text!!).matches() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = getString(R.string.e_invalid)
                dedicationLayout.error = null
            }
            else -> {
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
                updateData()
            }
        }
    }

    private fun validateDeliveryGift() {
        when {
            nameField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = getString(R.string.required_label)
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
            }
            lastNameField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = getString(R.string.required_label)
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
            }
            phoneField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = getString(R.string.required_label)
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
            }
            phoneField.text!!.length < 9 -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = getString(R.string.required_label)
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
            }
            fromField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = getString(R.string.required_label)
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
            }
            toField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = getString(R.string.required_label)
                recipientLayout.error = null
                dedicationLayout.error = null
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
            }
            recipientField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = getString(R.string.required_label)
                dedicationLayout.error = null
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
            }
            !Patterns.EMAIL_ADDRESS.matcher(recipientField.text!!).matches() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = getString(R.string.e_invalid)
                dedicationLayout.error = null
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
            }
            addressField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
                addressLayout.error = getString(R.string.required_label)
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
            }
            receivedNameField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
                addressLayout.error = null
                receivedNameLayout.error = getString(R.string.required_label)
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
            }
            deliveryPhoneField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = getString(R.string.required_label)
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
            }
            deliveryPhoneField.text!!.length < 9 -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = getString(R.string.required_label)
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
            }
            deliveryMailField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = getString(R.string.required_label)
                deliveryDateLayout.error = null
            }
            !Patterns.EMAIL_ADDRESS.matcher(deliveryMailField.text!!).matches() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = getString(R.string.e_invalid)
                deliveryDateLayout.error = null
            }
            deliveryDateField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = null
                deliveryDateLayout.error = getString(R.string.required_label)
            }
            else -> {
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                fromLayout.error = null
                toLayout.error = null
                recipientLayout.error = null
                dedicationLayout.error = null
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
                callIntent<InvoiceActivity>(387) {
                    this.putExtra("articleTotal", articleOriginalPrice)
                    this.putExtra("deliveryTotal", deliveryTotal)
                    this.putExtra("deliveryDistance", lastDistance)
                    this.putExtra("idDispatchPoint", dispatchPointSelected ?: "")
                    this.putExtra("userDeliveryLatitude", locationDeliverySelected!!.latitude)
                    this.putExtra("userDeliveryLongitude", locationDeliverySelected!!.longitude)
                }
            }
        }
    }

    private fun validateDelivery() {
        when {
            addressField.text.isNullOrEmpty() -> {
                showError()
                addressLayout.error = getString(R.string.required_label)
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
            }
            receivedNameField.text.isNullOrEmpty() -> {
                showError()
                addressLayout.error = null
                receivedNameLayout.error = getString(R.string.required_label)
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
            }
            deliveryPhoneField.text.isNullOrEmpty() -> {
                showError()
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = getString(R.string.required_label)
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
            }
            deliveryPhoneField.text!!.length < 9 -> {
                showError()
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = getString(R.string.required_label)
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
            }
            deliveryMailField.text.isNullOrEmpty() -> {
                showError()
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = getString(R.string.required_label)
                deliveryDateLayout.error = null
            }
            deliveryDateField.text.isNullOrEmpty() -> {
                showError()
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = null
                deliveryDateLayout.error = getString(R.string.required_label)
            }
            else -> {
                addressLayout.error = null
                receivedNameLayout.error = null
                deliveryPhoneLayout.error = null
                deliveryMailLayout.error = null
                deliveryDateLayout.error = null
                callIntent<InvoiceActivity>(387) {
                    this.putExtra("articleTotal", articleOriginalPrice)
                    this.putExtra("deliveryTotal", deliveryTotal)
                    this.putExtra("deliveryDistance", lastDistance)
                    this.putExtra("idDispatchPoint", dispatchPointSelected ?: "")
                    this.putExtra("userDeliveryLatitude", locationDeliverySelected!!.latitude)
                    this.putExtra("userDeliveryLongitude", locationDeliverySelected!!.longitude)
                }
            }
        }
    }

    private fun validateInsuredData() {
        when {
            nameField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = getString(R.string.required_label)
                lastNameLayout.error = null
                phoneLayout.error = null
            }
            lastNameField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = getString(R.string.required_label)
                phoneLayout.error = null
            }
            phoneField.text.isNullOrEmpty() -> {
                showError()
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = getString(R.string.required_label)
            }
            else -> {
                nameLayout.error = null
                lastNameLayout.error = null
                phoneLayout.error = null
                callIntent<SurveyActivity>(925) {
                    this.putExtra("insuranceType", insuredType)
                }
            }
        }
    }

    private fun savePersistentData(image: String?) {
        val u = Constants.userProfile
        u?.names = nameField.text.toString()
        u?.lastNames = lastNameField.text.toString()
        u?.photoUrl = image ?: u?.photoUrl
        u?.country = selectedCountry
        u?.phone = phoneField.text.toString().replace("-", "")
        u?.birthDate = format.format(mDateSelected ?: Date())
        u?.gender = genderSpinner.selectedItem.toString()
        u?.maritalStatus = maritalStatusSpinner.selectedItem.toString()
        if (provenance == 1) {
            u?.state = departmentSpinner.selectedItem.toString()
            u?.city = provinceSpinner.selectedItem.toString()
        }

        Constants.userProfile = u
        Functions.savePersistentProfile(this@EditProfileActivity2)
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
                            dispatchPointSelected = dp.idCommerceDispathPoint
                        }
                    } else {
                        if (distance < lastDistance && distance <= maxDistance) {
                            lastDistance = distance
                            closerLocation = dp
                            dispatchPointSelected = dp.idCommerceDispathPoint
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

    private fun showSchedule(model: ScheduleResponse?) {
        model?.let {
            val start = it.startTime.take(2).toInt() - 1
            val startEnd = it.startTime.take(2).toInt()

            val totalHours = it.endTime.take(2).toInt() - it.startTime.take(2).toInt()

            val startArray = IntArray(totalHours) { s -> (start + s) + 1 }
            val endArray = IntArray(totalHours) { e -> (startEnd + e) + 1 }

            val startDelivery: MutableList<String> = mutableListOf()
            val endDelivery: MutableList<String> = mutableListOf()

            startArray.forEach { sa ->
                startDelivery.add("$sa:${it.startTime.substring(3, 5)}")
            }

            endArray.forEach { ea ->
                endDelivery.add("$ea:${it.endTime.substring(3, 5)}")
            }

            startRangeSpinner.adapter = ArrayAdapter(this@EditProfileActivity2, R.layout.spinner_item, startDelivery)
            endRangeSpinner.adapter = ArrayAdapter(this@EditProfileActivity2, R.layout.spinner_item, endDelivery)

            startRangeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    endRangeSpinner.setSelection(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) { }
            }

            endRangeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    startRangeSpinner.setSelection(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) { }
            }
        }
    }

    private fun clearDelivery() {
        addressField.setText("")
        receivedNameField.setText("")
        deliveryPhoneField.setText("")
        deliveryMailField.setText("")
        deliveryDateField.setText("")
    }

    private fun showError() {
        save?.text = getString(R.string.accept)
        progress_bar_accept.visibility = View.GONE
    }
}
