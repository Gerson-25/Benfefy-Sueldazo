package com.syntepro.appbeneficiosbolivia.ui.lealtad

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.core.app.ErrorMessage
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.PlanByCommerceResponse
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.loyalty_code_plan_dialog.*

class LoyaltyCodePlanDialog: BaseActivity() {

    private var list: MutableList<PlanByCommerceResponse>? = mutableListOf()
//    private lateinit var mPlan: PickerEditText<PlanByCommerceResponse>
    private var selectedPlan: PlanByCommerceResponse? = null
    private var commerceName: String? = ""
    private var commerceImage: String? = ""
    private var categoryName: String? = ""
    private var mCancel = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.loyalty_code_plan_dialog)
        this.setFinishOnTouchOutside(true)

//        mPlan = findViewById(R.id.planId)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            commerceName = extras.getString("mCommerceName")?:""
            nameId.text =  commerceName
            commerceImage = extras.getString("mCommerceImage")
            Functions.showRoundedImage(commerceImage, imageId)
            categoryName = extras.getString("mRubroName")
            categoryNameId.text = categoryName
            list = extras.getParcelableArrayList("modelArray")

            list?.let { pln ->
                val codeList = pln.filter { it.codeRequired }.toMutableList()
                val arrayPlanAdapter = object : ArrayAdapter<PlanByCommerceResponse>(
                        this@LoyaltyCodePlanDialog,
                        android.R.layout.simple_spinner_dropdown_item,
                        codeList) {}
                planSpinner.adapter = arrayPlanAdapter
//                mPlan.with(
//                        codeList,
//                        resources.getString(R.string.plan_lealtad_title),
//                        searchBy = PickerDialog.SEARCH_BY_NAME,
//                        layoutType = PickerDialog.TWO_ROWS_LAYOUT
//                )
            }
        }

        planSpinner.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                parent?.let {
                    selectedPlan = parent.selectedItem as PlanByCommerceResponse
                    selectedPlan?.let { plan ->
                        if (plan.codeRequired)
                            codeParentId.visibility = View.VISIBLE
                        else
                            codeParentId.visibility = View.GONE
                        codeId.text = null
                        codeParentId.error = null
                    }
                }
            }
        }

//        mPlan.setOnItemSelectedListener(object : PickerEditText.OnItemSelectedListener<PlanByCommerceResponse> {
//            override fun onItemSelectedListener(item: PlanByCommerceResponse, position: Int) {
//                selectedPlan = item
//                planId.setText(item.name)
//                if (item.codeRequired)
//                    codeParentId.visibility = View.VISIBLE
//                else
//                    codeParentId.visibility = View.GONE
//                codeId.text = null
//                codeParentId.error = null
//            }
//
//            override fun onResetListener() {
//                planId.text = null
//                planId.item = null
//                codeId.text = null
//            }
//        })

        addId.setOnClickListener {
            if (codeParentId.editText?.text.isNullOrEmpty()) {
                codeParentId.requestFocus()
                codeParentId.error = getString(R.string.required_label)
            } else {
                callIntent<TermsActivity>(requestCode = AddLoyaltyPlanDialog.ACTIVITY_TERMS_ID) {
                    this.putExtra("planId", selectedPlan?.idLoyaltyPlan)
                    this.putExtra("mCommerceName", commerceName)
                    this.putExtra("mCommerceImage", commerceImage)
                    this.putExtra("mRubroName", categoryName)
                    this.putExtra("planDesc", selectedPlan?.description)
                    this.putExtra("planName", selectedPlan?.name)
                    this.putExtra("mCode", codeParentId.editText?.text.toString())
                    this.putExtra("affiliate", true)
                }
            }
        }

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val view = window.decorView
        val lp = view.layoutParams as WindowManager.LayoutParams
        lp.let{
            it.gravity = Gravity.START or Gravity.CENTER
            it.x = 0
            it.y = 0
            it.horizontalMargin = 0f
            it.width = LinearLayout.LayoutParams.MATCH_PARENT
            it.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        windowManager.updateViewLayout(view, lp)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AddLoyaltyPlanDialog.ACTIVITY_TERMS_ID && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("error")) {
                if (data.extras!!.getString("error").isNullOrEmpty()) {
                    mCancel = false
                    returnResult()
                    openWelcome()
                } else {
                    val view = findViewById<View>(android.R.id.content).rootView
                    ErrorMessage.notifyWithAction(view, data.extras!!.getString("error")!!, R.string.action_close) {}
                }
            }
        }
    }

    private fun returnResult() {
        val data = Intent()
        if (mCancel) setResult(Activity.RESULT_CANCELED, data)
        else setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun openWelcome() {
        callIntent<FinishDialog>(requestCode =  FinishDialog.FINISH_DIALOG_ID) {
            this.putExtra("planName", selectedPlan?.name)
        }
        this.finish()
    }

}