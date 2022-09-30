package com.syntepro.appbeneficiosbolivia.ui.lealtad

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.core.app.ErrorMessage
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.PlanByCommerceRequest
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.PlanByCommerceResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.adapters.PlanByCommerceAdapter
import com.syntepro.appbeneficiosbolivia.ui.lealtad.viewmodel.LoyaltyPlanViewModel
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.add_loyalty_plan_dialog.*
import javax.inject.Inject

class AddLoyaltyPlanDialog: BaseActivity() {

    @Inject
    lateinit var plansAdapter: PlanByCommerceAdapter

    private lateinit var loyaltyViewModel: LoyaltyPlanViewModel

    private var mCommerceId: String? = null
    private var mCommerceImage: String? = null
    private var mRubroName: String? = null
    private var mCommerceName: String? = null
    private var mCancel = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.add_loyalty_plan_dialog)
        this.setFinishOnTouchOutside(true)

        loyaltyViewModel = viewModel(viewModelFactory) {
            observe(plansCommerce, ::handlePlans)
            failure(failure, ::handleError)
        }

        // Extras
        val extras = intent.extras
        if (extras != null) {
            mCommerceId = extras.getString("commerceId")
            mCommerceImage = extras.getString("commerceImage")
            mRubroName = extras.getString("rubroName")
            mCommerceName = extras.getString("commerceName")
            nameId.text = mCommerceName
            Functions.showRoundedImage(mCommerceImage, imageId)
            categoryNameId.text = mRubroName
        }

        initList()
        loadPlans()

        plansAdapter.setActivity(this)
        closeId.setOnClickListener { onBackPressed() }
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

    fun showCode() {
        callIntent<LoyaltyCodePlanDialog>(requestCode = ACTIVITY_CODE_ID) {
            this.putExtra("mCommerceName", mCommerceName)
            this.putExtra("mCommerceImage", mCommerceImage)
            this.putExtra("mRubroName", mRubroName)
            this.putParcelableArrayListExtra("modelArray", ArrayList(plansAdapter.collection))
        }
    }

    fun showTerms() {
        callIntent<TermsActivity>(requestCode = ACTIVITY_TERMS_ID) {
            this.putExtra("planId", plansAdapter.getLastPlan()?.idLoyaltyPlan)
            this.putExtra("mCommerceName", mCommerceName)
            this.putExtra("mCommerceImage", mCommerceImage)
            this.putExtra("mRubroName", mRubroName)
            this.putExtra("planDesc", plansAdapter.getLastPlan()?.description)
            this.putExtra("planName", plansAdapter.getLastPlan()?.name)
            this.putExtra("mCode", plansAdapter.getPlanCode())
            this.putExtra("affiliate", true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTIVITY_TERMS_ID && resultCode == RESULT_OK) {
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
        } else if (requestCode == ACTIVITY_CODE_ID && resultCode == RESULT_OK) {
            mCancel = false
            returnResult()
        }
    }

    private fun openWelcome() {
        callIntent<FinishDialog>(requestCode =  FinishDialog.FINISH_DIALOG_ID) {
            this.putExtra("planName", plansAdapter.getLastPlan()?.name)
        }
    }

    private fun returnResult() {
        val data = Intent()
        if (mCancel) setResult(Activity.RESULT_CANCELED, data)
        else setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun loadPlans() {
        val request = PlanByCommerceRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                recordsNumber = Constants.LIST_PAGE_SIZE,
                pageNumber = 1,
                idCommerce = mCommerceId!!,
                idUser = Constants.userProfile?.idUser ?: ""
        )
        loyaltyViewModel.plansByCommerce(request)
    }

    private fun handlePlans(plans: BaseResponse<List<PlanByCommerceResponse>>?) {
        plansAdapter.collection = plans?.data.orEmpty()
        if(plans?.data.isNullOrEmpty())
            showError(getString(R.string.without_plans_label))
        else
            errorId.visibility = View.GONE
    }

    private fun showError(message: String) {
        errorId.text = message
        errorId.visibility = View.VISIBLE
    }

    private fun initList() {
        listId.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        listId.layoutManager = linearLayoutManager
        listId.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        listId.adapter = plansAdapter
        plansAdapter.setActivity(this)
    }

    companion object {
        const val ADD_LOYALTY_PLAN_DIALOG_ID = 810
        const val ACTIVITY_TERMS_ID = 811
        const val ACTIVITY_CODE_ID = 812
    }

}
