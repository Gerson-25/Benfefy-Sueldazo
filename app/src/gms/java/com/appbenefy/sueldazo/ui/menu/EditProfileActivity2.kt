package com.appbenefy.sueldazo.ui.menu

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.base.BaseActivity
import com.appbenefy.sueldazo.database.DataBaseAdapter
import com.appbenefy.sueldazo.entity.app.Pais
import com.appbenefy.sueldazo.entity.service.*
import com.appbenefy.sueldazo.service.NetworkService2
import com.appbenefy.sueldazo.service.RetrofitClientInstance
import com.appbenefy.sueldazo.utils.Constants
import com.appbenefy.sueldazo.utils.Functions
import com.appbenefy.sueldazo.utils.Helpers
import com.appbenefy.sueldazo.utils.UserType
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.gms.activity_edit_profile2.*
import kotlinx.android.synthetic.gms.activity_edit_profile2.birthdayContainer
import kotlinx.android.synthetic.gms.activity_edit_profile2.dateField
import kotlinx.android.synthetic.gms.activity_edit_profile2.edt_email
import kotlinx.android.synthetic.gms.activity_edit_profile2.gender_container
import kotlinx.android.synthetic.gms.activity_edit_profile2.marital_status_container
import kotlinx.android.synthetic.gms.activity_edit_profile2.phoneContainer
import kotlinx.android.synthetic.gms.activity_edit_profile2.sp_estado
import kotlinx.android.synthetic.gms.activity_edit_profile2.sp_genero
import kotlinx.android.synthetic.gms.activity_reset_password.*
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

class EditProfileActivity2 : BaseActivity(){

    private val adapter by lazy { DataBaseAdapter(this.applicationContext) }
    private var nomCountry: ArrayList<String> = arrayListOf()
    private var mStorage: StorageReference? = null
    private val mCalendar = Calendar.getInstance()
    private var mImageUri: Uri? = null
    private var mDateSelected: Date? = Date()
    private var currentImage: String? = null
    private var selectedCountry: String? = null
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile2)

        // Extras
        val extras = intent.extras

        // Storage
        mStorage = FirebaseStorage.getInstance().reference

        // Get Countries
        adapter.open()
        val country = adapter.infoCountry as List<Pais?>
        for (i in country.indices) {
            nomCountry.add(country[i]!!.nombre)
        }

        adapter.close()

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

        profileImage!!.setOnClickListener {
            startActivityForResult(Helpers.getImageChooserIntent(packageManager, externalCacheDir!!, this), 200)
        }

        // Gender Status Spinner
        val g = arrayOf(getString(R.string.male), getString(R.string.female))
        sp_genero.adapter = ArrayAdapter(this, R.layout.spinner_item, g)

        // Marital Status Spinner
        val s = arrayOf(getString(R.string.soltero), getString(R.string.comprometido), getString(R.string.casado), getString(R.string.separado), getString(R.string.divorciado), getString(R.string.viudo))
        sp_estado.adapter = ArrayAdapter(this, R.layout.spinner_item, s)

        editPhoto.setOnClickListener { profileImage.callOnClick() }

        save.setOnClickListener {
            loading(true)
            if (checkForm()){
                saveData()
            } else {
                Functions.showError(
                    this,
                    getString(R.string.incomplete_form),
                    getString(R.string.incomplete_form_title)
                )
            }
//            save.text = ""
//            progress_bar_accept.visibility = View.VISIBLE
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
        }

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

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            permissionCode -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (Functions.isLocationEnabled(this@EditProfileActivity2)) fetchLocation()
//                } else {
//                    clearDelivery()
//                    deliveryItem.isChecked = false
//                    deliveryData.visibility = View.GONE
//                }
//            }
//        }
//    }

    private fun updateLabel() {
        dateField.setText(Helpers.dateToStr(mCalendar.time, DateFormat.DATE_FIELD))
        mDateSelected = mCalendar.time
    }

    private fun showData() {
        Constants.userProfile?.let {
            it.photoUrl?.let { image ->
                Functions.showRoundedImage(image, profileImage)
                currentImage = image
            } ?: run {
                Functions.showRoundedImage(Constants.PROFILE_IMAGE, profileImage)
            }
            when(Constants.TYPE_OF_USER){
                UserType.ANONYMOUSE_USER -> {
                    edt_email.isEnabled = true
                    documentContainer.visibility = View.GONE
                    phoneContainer.visibility = View.GONE
                    birthdayContainer.visibility = View.GONE
                    marital_status_container.visibility = View.GONE
                    gender_container.visibility = View.GONE
                    formTitle.text = getString(R.string.editar_perfil_anonimo)
                }
                else -> {}
            }
            nameField.setText(it.names)
            lastNameField.setText(it.lastNames)
            documentField.setText(it.idDocument)
            phoneField.setText(it.phone)
            if (!it.birthDate.isNullOrEmpty()){
                mDateSelected = format.parse(it.birthDate ?: "")
                dateField.setText(Helpers.dateToStr(mDateSelected ?: Date(), DateFormat.DATE_FIELD))
            }
            edt_email.setText(it.email)
            userId = it.idDocument ?: ""
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
            val filePath = mStorage!!.child(Constants.PROFILE_IMAGES_PATH).child(userId)
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
                        .child(userId)
                    thumbRef.putBytes(thumbByte).addOnCompleteListener {
                        filePath.downloadUrl.addOnSuccessListener { uri ->
                            thumbRef.downloadUrl.addOnSuccessListener { uri2 ->
                                if (Constants.TYPE_OF_USER == UserType.ANONYMOUSE_USER){
                                    savePersistentData(uri.toString())
                                    Handler().postDelayed({
                                        this.finish()
                                    }, 3000)
                                    loading(false)
                                } else {
                                    saveDataWS(uri.toString(), uri2.toString()) {
                                        if (it) this.finish()
                                        else showError()
                                    }
                                }
                            }
                        }
                    }
                } catch (e: IOException) {
                    loading(false)
                    showError()
                    Log.e("Error", "${e.message}")
                }
            }.addOnFailureListener { e ->
                loading(false)
                showError()
                Log.e("Error", "${e.message}")
            }
        } else {
            if (Constants.TYPE_OF_USER == UserType.ANONYMOUSE_USER){
                savePersistentData(currentImage)
                Handler().postDelayed({
                    this.finish()
                }, 3000)
                loading(false)
            } else {
                saveDataWS(currentImage, null) {
                    if (it) this.finish()
                    else showError()
                }
            }
        }
    }

    private fun checkForm(): Boolean{
        return if (Constants.TYPE_OF_USER == UserType.ANONYMOUSE_USER){
            !nameField.text.isNullOrEmpty() && !lastNameField.text.isNullOrEmpty() && !edt_email.text.isNullOrEmpty()
        } else {
            !nameField.text.isNullOrEmpty() && !lastNameField.text.isNullOrEmpty() && !emailField.text.isNullOrEmpty()
        }
    }
    
    private fun saveDataWS(pictureName: String?, thumbName: String?, completion: (Boolean) -> Unit) {
        val request = with(UserUpdateRequest()) {
            country = Constants.userProfile?.actualCountry ?: "BO"
            language = 1
            idUser = Constants.userProfile?.idUser
            photoUrl = pictureName ?: Constants.PROFILE_IMAGE
            names = nameField.text.toString()
            lastNames = lastNameField.text.toString()
            birthDate = format.format(mDateSelected ?: Date())
            gender = sp_genero.selectedItem.toString()
            martialStatus = sp_estado.selectedItem.toString()
            this
        }
        val apiServe = RetrofitClientInstance.getClient(Constants.BASE_URL_MICROSUELDAZO)
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
                        loading(false)
                        Toast.makeText(
                            this@EditProfileActivity2,
                            response.description
                                ?: "",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    showError()
                    loading(false)
                    completion(false)
                    Toast.makeText(this@EditProfileActivity2, ret.message(), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                completion(false)
                loading(false)
                Toast.makeText(this@EditProfileActivity2, "Ocurrió un error.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun savePersistentData(image: String?) {
        val u = Constants.userProfile
        if (Constants.TYPE_OF_USER == UserType.ANONYMOUSE_USER){
            u?.names = nameField.text.toString()
            u?.lastNames = lastNameField.text.toString()
            u?.email = edt_email.text.toString()
            u?.photoUrl = image ?: u?.photoUrl
        } else {
            u?.names = nameField.text.toString()
            u?.lastNames = lastNameField.text.toString()
            u?.email = edt_email.text.toString()
            u?.photoUrl = image ?: u?.photoUrl
            u?.country = selectedCountry
            u?.phone = phoneField.text.toString().replace("-", "")
            u?.birthDate = format.format(mDateSelected ?: Date())
            u?.gender = sp_genero.selectedItem.toString()
            u?.maritalStatus = sp_estado.selectedItem.toString()
        }

        Constants.userProfile = u
        Functions.savePersistentProfile(this@EditProfileActivity2)
        loading(false)
    }

    private fun loading(loading: Boolean){
        progress_update.visibility = if (loading) View.VISIBLE else View.GONE
        save.visibility = if (loading) View.GONE else View.VISIBLE
    }

    private fun showError() {
        save?.text = getString(R.string.accept)
    }
}
