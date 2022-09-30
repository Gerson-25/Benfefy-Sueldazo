package com.syntepro.appbeneficiosbolivia.ui.commerce.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.BranchRequest
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.BranchResponse
import com.syntepro.appbeneficiosbolivia.ui.commerce.ui.adapters.BranchAdapter
import com.syntepro.appbeneficiosbolivia.ui.commerce.viewModel.CommerceViewModel
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.activity_branch_filter.*
import javax.inject.Inject

class BranchFilterActivity: BaseActivity() {

    @Inject
    lateinit var branchAdapter: BranchAdapter
    private lateinit var commerceViewModel: CommerceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_branch_filter)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.mainToolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = null

        commerceViewModel = viewModel(viewModelFactory) {
            observe(branch, ::handleBranch)
            failure(failure, ::handleError)
        }

        selectAll.setOnClickListener {
            val boxes = branchAdapter.getAllCheckBoxes()
            boxes?.let {
                if (selectAll.isChecked)
                    for (cb in it) cb.isChecked = true
                else
                    for (cb in it) cb.isChecked = false
            }
        }

        apply.setOnClickListener {
            if (branchAdapter.getBranches().isNullOrEmpty()) Constants.branchFilteredList = null
            else Constants.branchFilteredList = branchAdapter.getBranches()
            val intent = Intent()
            intent.putStringArrayListExtra("branches", ArrayList(branchAdapter.getBranches() ?: mutableListOf()))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        initList()
        loadBranch()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initList() {
        listId.setHasFixedSize(true)
        listId.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        listId.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        listId.adapter = branchAdapter
        branchAdapter.setActivity(this)
    }

    private fun loadBranch() {
        val request = BranchRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage()
        )
        commerceViewModel.getBranch(request)
    }

    private fun handleBranch(response: BaseResponse<List<BranchResponse>>?) {
        branchAdapter.collection = response?.data.orEmpty()
        if(response?.data.isNullOrEmpty())
            Log.e("Ok", "EmptyList")
    }

}