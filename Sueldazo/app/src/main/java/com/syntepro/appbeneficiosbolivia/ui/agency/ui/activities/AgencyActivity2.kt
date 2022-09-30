package com.syntepro.appbeneficiosbolivia.ui.agency.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import androidx.appcompat.widget.Toolbar
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.entity.service.Agency
import com.syntepro.appbeneficiosbolivia.entity.service.State
import com.syntepro.appbeneficiosbolivia.entity.service.StatesRequest
import com.syntepro.appbeneficiosbolivia.service.NetworkService2
import com.syntepro.appbeneficiosbolivia.service.RetrofitClientInstance
import com.syntepro.appbeneficiosbolivia.ui.coupon.AgencyDetailActivity
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.activity_agency2.*
import kotlinx.android.synthetic.main.agency_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AgencyActivity2 : BaseActivity() {

    private var list: MutableList<Agency>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agency2)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.back_toolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = null

        // Extras
        val extras = intent.extras
        if (extras != null) {
            val image = extras.getString("image")
            val name = extras.getString("name")

            Functions.showImage(image, pictureId)
            commerceNameId.text = name
            list = extras.getParcelableArrayList("model")

            // Show Data
            list?.let { listView.adapter = Adapter(it) }
        }

        // Show Data
        getData()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getData() {
        val request = with(StatesRequest()) {
            country = Constants.userProfile?.actualCountry ?: "BO"
            this
        }
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2).create(NetworkService2::class.java)
        scopeMainThread.launch {
            try {
                val response = apiService.getStates(request)
                when {
                    response.isSuccessful -> {
                        val ret = response.body()!!
                        if (ret.isSuccess) {
                            ret.data?.let { getStates(it) }
                        } else { Log.e("Error", "${ret.code}") }
                    }
                    else -> { Log.e("Error", "${response.message()} - ${response.errorBody()}") }
                }
            } catch (e: Exception) {
                Log.e("Exception", e.message?:e.cause?.message?:e.cause.toString())
            }
        }
    }

    private fun getStates(states: MutableList<State>) {
        val all = with(State()) {
            idState = null
            name = "Ver Todas"
            cities = mutableListOf()
            this
        }

        states.add(0, all)
        filterSpinner.adapter = ArrayAdapter(this@AgencyActivity2, R.layout.spinner_item, states)

        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                parent.let {
                    val selectedState = it?.selectedItem as State
                    if (selectedState.idState.isNullOrEmpty()) {
                        listView.adapter = Adapter(list ?: mutableListOf())
                        showEmptyLayout(false)
                    } else {
                        val filtered = list?.filter { s -> s.idCity == selectedState.idState }
                        filtered?.let { a ->
                            listView.adapter = Adapter(a)
                            if (a.isEmpty()) showEmptyLayout(true)
                            else showEmptyLayout(false)
                        } ?: run { showEmptyLayout(true) }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
    }

    class Adapter(agencies: List<Agency>): BaseAdapter() {
        private val agency: List<Agency> = agencies

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(viewGroup!!.context)
            val row = layoutInflater.inflate(R.layout.agency_item, viewGroup, false)

            row.agencyNameId.text = agency[position].nameProduct
            row.agencyAddressId.text = agency[position].addressProduct

            row.setOnClickListener {
                val intent = Intent(viewGroup.context, AgencyDetailActivity::class.java)
                intent.putExtra("branchId", agency[position].idAgency)
                viewGroup.context.startActivity(intent)
            }

            return row
        }

        override fun getItem(position: Int): Any { return "TEST STRING" }

        override fun getItemId(position: Int): Long { return position.toLong() }

        override fun getCount(): Int { return agency.size }
    }

}