package com.appbenefy.sueldazo.ui.login

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.rtoshiro.util.format.SimpleMaskFormatter
import com.github.rtoshiro.util.format.text.MaskTextWatcher
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.database.DataBaseAdapter
import com.appbenefy.sueldazo.entity.app.Pais
import com.appbenefy.sueldazo.entity.service.SaveUserRequest
import com.appbenefy.sueldazo.entity.service.User
import com.appbenefy.sueldazo.room.database.RoomDataBase
import com.appbenefy.sueldazo.service.NetworkService2
import com.appbenefy.sueldazo.service.RetrofitClientInstance
import com.appbenefy.sueldazo.utils.Constants
import com.appbenefy.sueldazo.utils.Functions
import com.appbenefy.sueldazo.utils.Functions.Companion.showError
import com.appbenefy.sueldazo.utils.Helpers
import kotlinx.android.synthetic.gms.activity_sign_up.*
import kotlinx.android.synthetic.gms.activity_sign_up.progress_circular
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    private val adapter by lazy { DataBaseAdapter(this.applicationContext) }
    private val mCalendar = Calendar.getInstance()
    private var mDateSelected: Date? = Date()
    private val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var isAnonymousUser = false
    var document = ""
    var names: String? = null
    var lastNames: String? = null
    var dateBirth: String? = null
    var email: String?  = null
    var cellPhone: String = "-------"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Init DataBase
        adapter.createDatabase()


        val extras = intent.extras
        if (extras != null) {
            cellPhone = extras.getString("cellPhone", "-------")!!
            isAnonymousUser = extras.getBoolean("isAnonymousUser", false)
            document = extras.getString("documentId", "")
            names = extras.getString("names", "")
            lastNames = extras.getString("lastNames", "")
            dateBirth = extras.getString("dateBirth", "")
            email = extras.getString("email", "")
        }

        initForm()

        // Gender Status Spinner
        val g = arrayOf(getString(R.string.male), getString(R.string.female))
        sp_genero.adapter = ArrayAdapter(this, R.layout.spinner_item, g)

        // Marital Status Spinner
        val s = arrayOf(getString(R.string.soltero), getString(R.string.comprometido), getString(R.string.casado), getString(R.string.separado), getString(R.string.divorciado), getString(R.string.viudo))
        sp_estado.adapter = ArrayAdapter(this, R.layout.spinner_item, s)

        // Phone MaskH
        val smfT = SimpleMaskFormatter("NNNN-NNNN")
        val mtwT = MaskTextWatcher(edt_telefono, smfT)
        edt_telefono.addTextChangedListener(mtwT)
        edt_telefono.hint = "xxxx-xxxx"

        dateField.setOnClickListener {
            openCalendar()
        }

        setAnonymousFields(isAnonymousUser)

        signup.setOnClickListener {
            showLoading(true)
            val formIsValid = if (isAnonymousUser) validateAnonymousForm() else validateForm()
            if (formIsValid) {
                if (isAnonymousUser) saveAnonymousData() else saveData()
            } else {
                showError(this, getString(R.string.incomplete_form), getString(R.string.incomplete_form_title))
            }
        }

    }

    override fun onDestroy() {
        RoomDataBase.destroyInstance()
        super.onDestroy()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    private fun validateAnonymousForm(): Boolean{
        return validateEmail() && validateName() && validateLastName()
    }

    private fun validateForm(): Boolean {
        return validateEmail() && validateName() && validateLastName() &&validateBirthday()
    }
    /**
     * @author Gerson Aquino 19OCT2022
     *
     * This function hide fields that are not needed when user
     * is login anonymously.
     *
     * @property isAnonymous define is the user will hace to fill.
     */
    private fun setAnonymousFields(isAnonymous: Boolean){
        if (isAnonymous){
            birthdayContainer.visibility = View.GONE
            phoneContainer.visibility = View.GONE
            gender_container.visibility = View.GONE
            marital_status_container.visibility = View.GONE
        }
    }

    private fun initForm(){
        edt_nombre.setText(names ?: "")
        edt_apellido.setText(lastNames ?: "")
        edt_telefono.setText(cellPhone)
        edt_email.setText(email ?: "")
        dateField.setText(dateBirth ?: "")

        edt_nombre.isEnabled = false
        edt_apellido.isEnabled = false
        edt_telefono.isEnabled =  cellPhone == "-------"
        edt_email.isEnabled = false
        dateField.isEnabled = false
    }

    private fun updateLabel() {
        dateField.setText(Helpers.dateToStrReg(mCalendar.time, DateFormat.DATE_FIELD))
        mDateSelected = mCalendar.time
    }

    // Validate Name
    private fun validateName(): Boolean {
        val name = tl_nombre.editText?.text.toString()
        return name.isNotEmpty() && NAME_PATTERN.matcher(name).matches()
    }

    // Validate Last Name
    private fun validateLastName(): Boolean {
        val name = tl_apellido.editText?.text.toString()
        return name.isNotEmpty() && NAME_PATTERN.matcher(name).matches()
    }

    // Validate Email
    private fun validateBirthday(): Boolean {
        val birthday = dateLayout.editText?.text.toString()
        return birthday.isNotEmpty()
    }

    private fun validateEmail(): Boolean{
        val email  = tl_correo.editText?.text.toString().trim { it <= ' ' }
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun openCalendar() {
        DatePickerDialog(
            this, { _, year, monthOfYear, dayOfMonth ->
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, monthOfYear)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveData(){
        val request = with(SaveUserRequest()) {
            country = "BO"
            actualCountry = "BO"
            names = tl_nombre.editText?.text.toString()
            lastNames = tl_apellido.editText?.text.toString()
            birthDate = LocalDate.parse(dateBirth).atStartOfDay(ZoneOffset.UTC).toString()
            email = edt_email.text.toString()
            phone = edt_telefono.text.toString().replace("-", "")
            idDocument = document.toLong()
            gender = sp_genero.selectedItem.toString()
            maritalStatus = sp_estado.selectedItem.toString()
            photoUrl = "https://firebasestorage.googleapis.com/v0/b/beneficios-uat.appspot.com/o/imagesProfile%2FBdq8CvOHCGgIvnYmGLSKF9HNzlv2?alt=media&token=821c4ca1-11ca-440b-9a37-1e0888398eec"
            this
        }
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICROSUELDAZO).create(
            NetworkService2::class.java)
        scopeMainThread.launch {
            try {
                val response = apiService.saveUser(request)
                when {
                    response.isSuccessful -> {
                        val ret = response.body()!!
                        if (ret.isSuccess) {
                            ret.data?.let { data ->
                                val userProfile = with(User()) {
                                    idUser =  data
                                    names = request.names
                                    lastNames = request.lastNames
                                    phone = request.phone?.replace("-", "")
                                    birthDate = request.birthDate.toString()
                                    gender = request.gender
                                    maritalStatus = request.maritalStatus
                                    photoUrl = request.photoUrl
                                    email = request.email
                                    adapter.open()
                                    val userCountry: Pais = adapter.getCountryInfoAbr("BO")
                                    adapter.close()
                                    actualCountry = userCountry.abreviacion
                                    countryName = userCountry.nombre
                                    currency = userCountry.moneda
                                    areaCode = userCountry.codigoArea
                                    this
                                }
                                Constants.userProfile = userProfile
                                Functions.savePersistentProfile(this@SignUpActivity)
                                UserData.firebaseToken(data)
                                UserData.getToken { }
                                updateUI()
                            }
                        } else {
                            val message = ret.description ?: getString(R.string.social_error)
                            showError(this@SignUpActivity, message, getString(R.string.error_message_title))
                            showLoading(false)
                        }
                    }
                    else -> {
                        val message = response.message() ?: getString(R.string.social_error)
                        showError(this@SignUpActivity, message, getString(R.string.error_message_title))
                        showLoading(false)
                    }
                }
            } catch (e: Exception) {
                showError(this@SignUpActivity, getString(R.string.social_error), getString(R.string.error_message_title))
                showLoading(false)
            }
        }
    }

    private fun saveAnonymousData(){
        val userProfile = with(User()) {
            names = tl_nombre.editText?.text.toString()
            lastNames = tl_apellido.editText?.text.toString()
            email = edt_email.text.toString()
            this
        }
        Constants.userProfile = userProfile
        updateUI()
    }

    fun showLoading(show: Boolean){
        progress_circular.visibility = if (show) View.VISIBLE else View.GONE
        signup.visibility = if (show) View.GONE else View.VISIBLE
    }

    // Update UI
    private fun updateUI() {
        val name = tl_nombre.editText?.text.toString() + tl_apellido.editText?.text.toString()
        val intent = Intent(applicationContext, CitySelectionActivity::class.java)
        intent.putExtra("userName", name)
        startActivity(intent)
    }

    companion object {
        private val NAME_PATTERN = Pattern.compile("^[a-zA-Z][0-9a-zA-Z .,'´ñ-]*$")
    }
}
