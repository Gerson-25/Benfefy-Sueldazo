package com.syntepro.appbeneficiosbolivia.ui.sudamericana.ui.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.merckers.core.extension.hideKeyboard
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.service.NetworkService2
import com.syntepro.appbeneficiosbolivia.service.RetrofitClientInstance
import com.syntepro.appbeneficiosbolivia.ui.home.model.SudamericanaData
import com.syntepro.appbeneficiosbolivia.ui.shop.model.CountriesRequest
import com.syntepro.appbeneficiosbolivia.ui.shop.model.CountriesResponse
import com.syntepro.appbeneficiosbolivia.ui.shop.model.ArticleResponse
import com.syntepro.appbeneficiosbolivia.ui.sudamericana.model.SurveyRequest
import com.syntepro.appbeneficiosbolivia.ui.sudamericana.ui.fragments.*
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.utils.Helpers
import kotlinx.android.synthetic.main.activity_survey.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class SurveyActivity : BaseActivity() {

    private lateinit var collectionAdapter: CollectionAdapter
    private val mCalendar = Calendar.getInstance()
    private var mBirthDateSelected: Date? = Date()
    private var mExpiredDateSelected: Date? = Date()
    private var selectedSubsidiaryCode: Int = 0
    private var selectedDocumentExtension: String = ""
    private var selectedPetType: String = ""
    private var selectedPetYears: Int = 0
    private var selectedPetMonths: Int = 0
    var insuredType: Int = 0
    var question1Check: Boolean = false
    var question1Description: String? = null
    var question2Check: Boolean = false
    var question2Description: String? = null
    var question3Check: Boolean = false
    var question3Description: String? = null
    var userWeight: Int = 0
    var userHeight: Int = 0
    var question5Check: Boolean = false
    var question5Description: String? = null
    var doctorsName: String? = null
    var doctorsAddress: String? = null
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_survey)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.back_toolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = getString(R.string.policy_data)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            insuredType = extras.getInt("insuranceType")
            when(insuredType) {
                ArticleResponse.PET_INSURANCE -> {
                    petInformation.visibility = View.VISIBLE
                    getPetType()
                    getPetYears()
                    getPetMonths()
                }
                ArticleResponse.COVID_INSURANCE -> {
                    covidInsurance.visibility = View.VISIBLE
                    getCountryList()
                }
            }
//            if (insuredType == Item.PET_INSURANCE) {
//                petInformation.visibility = View.VISIBLE
//                getPetType()
//                getPetYears()
//                getPetMonths()
//            } else if (insuredType == Item.PET_INSURANCE) {
//                covidInsurance.visibility = View.VISIBLE
//                getCountryList()
//            }
        }

        // Adapter
        collectionAdapter = CollectionAdapter(this@SurveyActivity)
        pagerId.adapter = collectionAdapter
        pagerId.isUserInputEnabled = false

        pagerId.offscreenPageLimit = 1

        TabLayoutMediator(tabId, pagerId) { tab, position ->
            when(position) {
                0 -> tab.text = null
                1 -> tab.text = null
                2 -> tab.text = null
                3 -> tab.text = null
                4 -> tab.text = null
                5 -> tab.text = null
            }
        }.attach()

        dateField.setOnClickListener {
            DatePickerDialog(
                    this, { _, year, monthOfYear, dayOfMonth ->
                mCalendar.set(Calendar.YEAR, year)
                mCalendar.set(Calendar.MONTH, monthOfYear)
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateBirthDateLabel()
            }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        expeditionDateField.setOnClickListener {
            DatePickerDialog(
                    this, { _, year, monthOfYear, dayOfMonth ->
                mCalendar.set(Calendar.YEAR, year)
                mCalendar.set(Calendar.MONTH, monthOfYear)
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateExpiredDateLabel()
            }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        next.setOnClickListener {
            hideKeyboard(it)
            if (insuredData.isVisible && validateData()) {
                if (validateClientData()) {
                    if (insuredType == ArticleResponse.PET_INSURANCE) {
                        if (validatePetData()) {
                            insuredData.visibility = View.GONE
                            questions.visibility = View.VISIBLE
                        }
                    } else if(insuredType == ArticleResponse.COVID_INSURANCE) {
                        if (validateCovidData()) {
                            insuredData.visibility = View.GONE
                            questions.visibility = View.VISIBLE
                        }
                    } else {
                        insuredData.visibility = View.GONE
                        questions.visibility = View.VISIBLE
                    }
                } else
                    Functions.showWarning(this@SurveyActivity, getString(R.string.age_does_not_apply))
            }
        }

        showData()
        getExtensions(Constants.sudamericanaParameters?.toMutableList())
    }

    override fun onBackPressed() {
        if (pagerId.currentItem != 0)
            movePrevious()
        else if (!insuredData.isVisible) {
            insuredData.visibility = View.VISIBLE
            questions.visibility = View.GONE
        } else {
            setResult(Activity.RESULT_CANCELED)
            this.finish()
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /*
    * App Functions
    */
    fun moveNext() {
        //it doesn't matter if you're already in the last item
        pagerId.currentItem = pagerId.currentItem + 1
    }

    private fun movePrevious() {
        //it doesn't matter if you're already in the first item
        pagerId.currentItem = pagerId.currentItem - 1
    }

    private fun validateData(): Boolean {
        return when {
            firstNameField.text.isNullOrEmpty() -> {
                firstNameLayout.error = getString(R.string.c_required)
                secondNameLayout.error = null
                firstLastNameLayout.error = null
                secondLastNameLayout.error = null
                dateLayout.error = null
                phoneLayout.error = null
                documentLayout.error = null
                expeditionDateLayout.error = null
                beneficiaryNameLayout.error = null
                beneficiaryLastNameLayout.error = null
                documentBeneficiaryLayout.error = null
                relationshipLayout.error = null
                false
            }
            firstLastNameField.text.isNullOrEmpty() -> {
                firstNameLayout.error = null
                secondNameLayout.error = null
                firstLastNameLayout.error = getString(R.string.c_required)
                secondLastNameLayout.error = null
                dateLayout.error = null
                phoneLayout.error = null
                documentLayout.error = null
                expeditionDateLayout.error = null
                beneficiaryNameLayout.error = null
                beneficiaryLastNameLayout.error = null
                documentBeneficiaryLayout.error = null
                relationshipLayout.error = null
                false
            }
            dateField.text.isNullOrEmpty() -> {
                firstNameLayout.error = null
                secondNameLayout.error = null
                firstLastNameLayout.error = null
                secondLastNameLayout.error = null
                dateLayout.error = getString(R.string.c_required)
                phoneLayout.error = null
                documentLayout.error = null
                expeditionDateLayout.error = null
                beneficiaryNameLayout.error = null
                beneficiaryLastNameLayout.error = null
                documentBeneficiaryLayout.error = null
                relationshipLayout.error = null
                false
            }
            phoneField.text.isNullOrEmpty() -> {
                firstNameLayout.error = null
                secondNameLayout.error = null
                firstLastNameLayout.error = null
                secondLastNameLayout.error = null
                dateLayout.error = null
                phoneLayout.error = getString(R.string.c_required)
                documentLayout.error = null
                expeditionDateLayout.error = null
                beneficiaryNameLayout.error = null
                beneficiaryLastNameLayout.error = null
                documentBeneficiaryLayout.error = null
                relationshipLayout.error = null
                false
            }
            documentField.text.isNullOrEmpty() -> {
                firstNameLayout.error = null
                secondNameLayout.error = null
                firstLastNameLayout.error = null
                secondLastNameLayout.error = null
                dateLayout.error = null
                phoneLayout.error = null
                documentLayout.error = getString(R.string.c_required)
                expeditionDateLayout.error = null
                beneficiaryNameLayout.error = null
                beneficiaryLastNameLayout.error = null
                documentBeneficiaryLayout.error = null
                relationshipLayout.error = null
                false
            }
            documentField.text!!.length > 10 -> {
                firstNameLayout.error = null
                secondNameLayout.error = null
                firstLastNameLayout.error = null
                secondLastNameLayout.error = null
                dateLayout.error = null
                phoneLayout.error = null
                documentLayout.error = getString(R.string.longitude_document_validation)
                expeditionDateLayout.error = null
                beneficiaryNameLayout.error = null
                beneficiaryLastNameLayout.error = null
                documentBeneficiaryLayout.error = null
                relationshipLayout.error = null
                false
            }
            expeditionDateField.text.isNullOrEmpty() -> {
                firstNameLayout.error = null
                secondNameLayout.error = null
                firstLastNameLayout.error = null
                secondLastNameLayout.error = null
                dateLayout.error = null
                phoneLayout.error = null
                documentLayout.error = null
                expeditionDateLayout.error = getString(R.string.c_required)
                beneficiaryNameLayout.error = null
                beneficiaryLastNameLayout.error = null
                documentBeneficiaryLayout.error = null
                relationshipLayout.error = null
                false
            }
            phoneField.text.toString().length < 9 -> {
                firstNameLayout.error = null
                secondNameLayout.error = null
                firstLastNameLayout.error = null
                secondLastNameLayout.error = null
                dateLayout.error = getString(R.string.c_required)
                phoneLayout.error = getString(R.string.f_invalid)
                documentLayout.error = null
                expeditionDateLayout.error = null
                beneficiaryNameLayout.error = null
                beneficiaryLastNameLayout.error = null
                documentBeneficiaryLayout.error = null
                relationshipLayout.error = null
                false
            }
            beneficiaryNameField.text.isNullOrEmpty() -> {
                firstNameLayout.error = null
                secondNameLayout.error = null
                firstLastNameLayout.error = null
                secondLastNameLayout.error = null
                dateLayout.error = null
                phoneLayout.error = null
                documentLayout.error = null
                expeditionDateLayout.error = null
                beneficiaryNameLayout.error = getString(R.string.c_required)
                beneficiaryLastNameLayout.error = null
                documentBeneficiaryLayout.error = null
                relationshipLayout.error = null
                false
            }
            beneficiaryLastNameField.text.isNullOrEmpty() -> {
                firstNameLayout.error = null
                secondNameLayout.error = null
                firstLastNameLayout.error = null
                secondLastNameLayout.error = null
                dateLayout.error = null
                phoneLayout.error = null
                documentLayout.error = null
                expeditionDateLayout.error = null
                beneficiaryNameLayout.error = null
                beneficiaryLastNameLayout.error = getString(R.string.c_required)
                documentBeneficiaryLayout.error = null
                relationshipLayout.error = null
                false
            }
            documentBeneficiaryField.text.isNullOrEmpty() -> {
                firstNameLayout.error = null
                secondNameLayout.error = null
                firstLastNameLayout.error = null
                secondLastNameLayout.error = null
                dateLayout.error = null
                phoneLayout.error = null
                documentLayout.error = null
                expeditionDateLayout.error = null
                beneficiaryNameLayout.error = null
                beneficiaryLastNameLayout.error = null
                documentBeneficiaryLayout.error = getString(R.string.c_required)
                relationshipLayout.error = null
                false
            }
            documentBeneficiaryField.text!!.length > 10 -> {
                firstNameLayout.error = null
                secondNameLayout.error = null
                firstLastNameLayout.error = null
                secondLastNameLayout.error = null
                dateLayout.error = null
                phoneLayout.error = null
                documentLayout.error = null
                expeditionDateLayout.error = null
                beneficiaryNameLayout.error = null
                beneficiaryLastNameLayout.error = null
                documentBeneficiaryLayout.error = getString(R.string.longitude_document_validation)
                relationshipLayout.error = null
                false
            }
            relationshipField.text.isNullOrEmpty() -> {
                firstNameLayout.error = null
                secondNameLayout.error = null
                firstLastNameLayout.error = null
                secondLastNameLayout.error = null
                dateLayout.error = null
                phoneLayout.error = null
                documentLayout.error = null
                expeditionDateLayout.error = null
                beneficiaryNameLayout.error = null
                beneficiaryLastNameLayout.error = null
                documentBeneficiaryLayout.error = null
                relationshipLayout.error = getString(R.string.c_required)
                false
            }
            else -> {
                firstNameLayout.error = null
                secondNameLayout.error = null
                firstLastNameLayout.error = null
                secondLastNameLayout.error = null
                dateLayout.error = null
                phoneLayout.error = null
                documentLayout.error = null
                expeditionDateLayout.error = null
                beneficiaryNameLayout.error = null
                beneficiaryLastNameLayout.error = null
                documentBeneficiaryLayout.error = null
                relationshipLayout.error = null
                true
            }
        }
    }

    private fun validateClientData(): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = mBirthDateSelected ?: Date()
        val age = getAge(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
        return age in 18..64
    }

    private fun validatePetData(): Boolean {
        return when {
            petNameField.text.isNullOrEmpty() -> {
                petNameLayout.error = getString(R.string.c_required)
                petBreedLayout.error = null
                false
            }
            petBreedField.text.isNullOrEmpty() -> {
                petNameLayout.error = null
                petBreedLayout.error = getString(R.string.c_required)
                false
            }
            else -> {
                petNameLayout.error = null
                petBreedLayout.error = null
                true
            }
        }
    }

    private fun validateCovidData(): Boolean {
        return when {
            addressField.text.isNullOrEmpty() -> {
                addressLayout.error = getString(R.string.required_label)
                false
            }
            else -> {
                addressLayout.error = null
                true
            }
        }
    }

    private fun showData() {
        Constants.userProfile?.let {
            firstNameField.setText(it.names)
            firstLastNameField.setText(it.lastNames)
            mBirthDateSelected = format.parse(it.birthDate ?: "")
            dateField.setText(Helpers.dateToStr(mBirthDateSelected ?: Date(), DateFormat.DATE_FIELD))
            documentField.setText(it.idDocument)
        }
    }

    private fun updateBirthDateLabel() {
        dateField.setText(Helpers.dateToStr(mCalendar.time, DateFormat.DATE_FIELD))
        mBirthDateSelected = mCalendar.time
    }

    private fun updateExpiredDateLabel() {
        expeditionDateField.setText(Helpers.dateToStr(mCalendar.time, DateFormat.DATE_FIELD))
        mExpiredDateSelected = mCalendar.time
    }

    fun updateSurvey() {
        val request = SurveyRequest(
                insuredFirstName = firstNameField.text.toString(),
                insuredMiddleName = secondNameField.text.toString(),
                insuredLastName = firstLastNameField.text.toString(),
                insuredOtherLastName = secondLastNameField.text.toString(),
                insuredBirthDate = format.format(mBirthDateSelected ?: Date()),
                insuredIdentityDocument = documentField.text.toString(),
                documentExtension = selectedDocumentExtension,
                insuredExpeditionDateIdentityDocument = format.format(mExpiredDateSelected
                        ?: Date()),
                subsidiaryCode = selectedSubsidiaryCode,
                phoneNumber = phoneField.text.toString().replace("-", ""),
                beneficiaryFullName = "${beneficiaryNameField.text.toString()} ${beneficiaryLastNameField.text.toString()}",
                beneficiaryDocumentId = documentBeneficiaryField.text.toString(),
                beneficiaryPercentage = 100,
                beneficiaryRelationship = relationshipField.text.toString(),
                question1 = question1Check,
                descriptionQuestion1 = question1Description,
                question2 = if (insuredType == ArticleResponse.COVID_INSURANCE) null else question2Check,
                descriptionQuestion2 = if (insuredType == ArticleResponse.COVID_INSURANCE) null else question2Description,
                question3 = if (insuredType == ArticleResponse.COVID_INSURANCE) null else question3Check,
                descriptionQuestion3 = if (insuredType == ArticleResponse.COVID_INSURANCE) null else question3Description,
                weight = if (insuredType == ArticleResponse.COVID_INSURANCE) null else userWeight,
                height = if (insuredType == ArticleResponse.COVID_INSURANCE) null else userHeight,
                question5 = if (insuredType == ArticleResponse.COVID_INSURANCE) null else question5Check,
                descriptionQuestion5 = if (insuredType == ArticleResponse.COVID_INSURANCE) null else question5Description,
                question6 = if (insuredType == ArticleResponse.COVID_INSURANCE) null else !(doctorsName.isNullOrEmpty() && doctorsAddress.isNullOrEmpty()),
                doctorName = if (insuredType == ArticleResponse.COVID_INSURANCE) null else doctorsName,
                doctorAddress = if (insuredType == ArticleResponse.COVID_INSURANCE) null else doctorsAddress,
                petType = if (insuredType == ArticleResponse.PET_INSURANCE) selectedPetType else null,
                petBreed = if (insuredType == ArticleResponse.PET_INSURANCE) petBreedField.text.toString() else null,
                petYears = if (insuredType == ArticleResponse.PET_INSURANCE) selectedPetYears else null,
                petMonths = if (insuredType == ArticleResponse.PET_INSURANCE) selectedPetMonths else null,
                petName = if (insuredType == ArticleResponse.PET_INSURANCE) petNameField.text.toString() else null,
                userCountry = if (insuredType == ArticleResponse.COVID_INSURANCE) bornCountrySpinner.selectedItem.toString() else null,
                userCountryResidence = if (insuredType == ArticleResponse.COVID_INSURANCE) livingCountrySpinner.selectedItem.toString() else null,
                homeAddress = if (insuredType == ArticleResponse.COVID_INSURANCE) addressField.text.toString() else null
        )
        val intent = Intent()
        intent.putExtra("model", request)
        setResult(Activity.RESULT_OK, intent)
        this.finish()
//        surveyViewModel.saveUserSurvey(request)
    }

    private fun getAge(year: Int, month: Int, day: Int): Int {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[year, month] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) age--
        return age
    }

    private fun getExtensions(extensions: MutableList<SudamericanaData>?) {
        extensionSpinner.adapter = ArrayAdapter(this@SurveyActivity, R.layout.spinner_item, extensions ?: mutableListOf())

        extensionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                parent.let {
                    val extension = it?.selectedItem as SudamericanaData
                    selectedSubsidiaryCode = extension.subsidiary_code
                    selectedDocumentExtension = extension.extension
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
    }

    private fun getPetType() {
        val petType = mutableListOf("Perro", "Gato")
        petTypeSpinner.adapter = ArrayAdapter(this@SurveyActivity, R.layout.spinner_item, petType)

        petTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                parent?.let {
                    selectedPetType = it.selectedItem as String
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
    }

    private fun getPetYears() {
        val years = Array(16) { "$it ${getString(R.string.years)}" }
        petYearsSpinner.adapter = ArrayAdapter(this@SurveyActivity, R.layout.spinner_item, years.toMutableList())

        petYearsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedPetYears = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

    }

    private fun getPetMonths() {
        val months = Array(12) { "$it ${getString(R.string.months)}" }
        petMonthsSpinner.adapter = ArrayAdapter(this@SurveyActivity, R.layout.spinner_item, months.toMutableList())

        petMonthsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPetMonths = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
    }

    private fun configureSpinners(countries: MutableList<CountriesResponse>) {
        bornCountrySpinner.adapter = ArrayAdapter(this@SurveyActivity, R.layout.spinner_item, countries)

        bornCountrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.e("Selected", "Item: ${parent?.selectedItem}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        livingCountrySpinner.adapter = ArrayAdapter(this@SurveyActivity, R.layout.spinner_item, countries)

        livingCountrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.e("Selected", "Item: ${parent?.selectedItem}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
    }

    /*
    * WS Functions
    */
    private fun getCountryList() {
        val request = CountriesRequest(
                country = "BO",
                language = Functions.getLanguage(),
                filterType = 1
        )
        val apiServe = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2)
        val inst = apiServe.create(NetworkService2::class.java)
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        scopeMainThread.launch {
            try {
                val ret = inst.getCountries(request)
                if (ret.isSuccessful) {
                    val response = ret.body()!!
                    response.data?.let { configureSpinners(it) }
                }
            } catch (e: Exception) {
                Log.e("Error", "Ocurrió un error : ${e.printStackTrace()}")
            }
        }
    }


    class CollectionAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 6
        override fun createFragment(position: Int): Fragment {
            // Return a NEW fragment instance in createFragment(int)
            lateinit var fragment: Fragment
            when(position) {
                0 -> fragment = FirstQuestionFragment()
                1 -> fragment = SecondQuestionFragment()
                2 -> fragment = ThirdQuestionFragment()
                3 -> fragment = FourthQuestionFragment()
                4 -> fragment = FifthQuestionFragment()
                5 -> fragment = SixthQuestionFragment()
            }

            return fragment
        }
    }

}