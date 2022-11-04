package com.syntepro.sueldazo.ui.filter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.base.BaseActivity
import com.syntepro.sueldazo.entity.service.City
import com.syntepro.sueldazo.ui.home.model.StatesResponse
import com.syntepro.sueldazo.utils.Constants
import kotlinx.android.synthetic.main.activity_filter.*

class FilterActivity : BaseActivity() {

    private var selectedState: StatesResponse? = null
    private var state: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_filter)
        this.setFinishOnTouchOutside(true)

        if (Constants.stateFiltered.isNullOrEmpty()) clean.visibility = View.GONE

        apply.setOnClickListener {
            val intent = Intent()
            intent.putExtra("state", state)
            intent.putExtra("stateName", departmentSpinner.selectedItem.toString())
            intent.putExtra("cityName", provinceSpinner.selectedItem.toString())
            setResult(Activity.RESULT_OK, intent)
            this.finish()
        }

        clean.setOnClickListener {
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            this.finish()
        }

        // Show Data
        Constants.countryStates?.let { getStates(it) }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val view = window.decorView
        val lp = view.layoutParams as WindowManager.LayoutParams
        lp.gravity = Gravity.START or Gravity.CENTER
        lp.x = 0
        lp.y = 0
        lp.horizontalMargin = 0f
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        windowManager.updateViewLayout(view, lp)
    }

//    private fun getData() {
//        val request = with(StatesRequest()) {
//            country = Constants.userProfile?.actualCountry ?: "BO"
//            this
//        }
//        val job = Job()
//        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
//        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2).create(NetworkService2::class.java)
//        scopeMainThread.launch {
//            try {
//                val response = apiService.getStates(request)
//                when {
//                    response.isSuccessful -> {
//                        val ret = response.body()!!
//                        if (ret.isSuccess) {
//                            ret.data?.let { getStates(it) }
//                        } else { Log.e("Error", "${ret.code}") }
//                    }
//                    else -> { Log.e("Error", "${response.message()} - ${response.errorBody()}") }
//                }
//            } catch (e: Exception) {
//                Log.e("Exception", e.message?:e.cause?.message?:e.cause.toString())
//            }
//        }
//    }

    private fun getStates(states: MutableList<StatesResponse>) {
        departmentSpinner.adapter = ArrayAdapter(this@FilterActivity, R.layout.spinner_item, states)

        departmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                parent.let {
                    selectedState = it?.selectedItem as StatesResponse
                    state = selectedState?.idState
                    getCities(selectedState?.cities)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
    }

    private fun getCities(cities: List<City>?) {
        cities?.let {
            val c: MutableList<String> = mutableListOf()
            for (ct in it) c.add(ct.name ?: "-")
            provinceSpinner.adapter = ArrayAdapter(this@FilterActivity, R.layout.spinner_item, c)
        }
    }

}