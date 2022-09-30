package com.syntepro.appbeneficiosbolivia.ui.wallet

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import com.merckers.core.extension.failure
import com.merckers.core.extension.hideKeyboard
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.MilesGoalsRequest
import com.syntepro.appbeneficiosbolivia.ui.lealtad.viewmodel.LoyaltyPlanViewModel
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.utils.Helpers
import kotlinx.android.synthetic.main.miles_goals_info_dialog.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MilesGoalsInfoDialog: BaseActivity() {

    private lateinit var loyaltyViewModel: LoyaltyPlanViewModel
    private val mCalendar = Calendar.getInstance()
    private var mDateSelected: Date? = Date()
    private var mGoalId: String? = ""
    private var mPlanId: String? = ""
    private var mUserPlanId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        appComponent.inject(this)
        setContentView(R.layout.miles_goals_info_dialog)
        this.setFinishOnTouchOutside(true)

        loyaltyViewModel = viewModel(viewModelFactory) {
            observe(milesGoals, ::applyGoals)
            failure(failure, ::handleError)
        }

        // Extras
        val extras = intent.extras
        if (extras != null) {
            mGoalId = extras.getString("goalId")
            mPlanId = extras.getString("planId")
            mUserPlanId = extras.getString("userPlanId")
        }

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

        apply.setOnClickListener {
            hideKeyboard(it)
            apply.text = ""
            progress_circular.visibility = View.VISIBLE
            if (validateData()) getData()
            else {
                apply.text = "Aplicar"
                progress_circular.visibility = View.GONE
                Functions.showWarning(this@MilesGoalsInfoDialog, "Datos Incompletos")
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

    private fun getData() {
        val request = MilesGoalsRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                idGoal = mGoalId,
                idUser = Constants.userProfile?.idUser ?: "",
                idLoyaltyPlan = mPlanId ?: "",
                goal = goalField.text.toString().toInt(),
                dateGoal = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).format(mDateSelected ?: Date())
        )
        loyaltyViewModel.saveMilesGoals(request)
    }

    private fun applyGoals(response: BaseResponse<Boolean>?) {
        response?.data?.let {
            if (it) {
                val intent = Intent()
                intent.putExtra("userPlanId", mUserPlanId)
                setResult(Activity.RESULT_OK, intent)
                this.finish()
            } else Log.e("Error", response.description)
        }
    }

    private fun updateLabel() {
        dateField.setText(Helpers.dateToStr(mCalendar.time, DateFormat.DATE_FIELD))
        mDateSelected = mCalendar.time
    }

    private fun validateData(): Boolean {
        return when {
            goalField.text.isNullOrEmpty() -> false
            dateField.text.isNullOrEmpty() -> false
            else -> true
        }
    }

}