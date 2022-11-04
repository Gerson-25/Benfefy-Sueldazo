package com.syntepro.sueldazo.ui.wallet

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import com.merckers.core.extension.hideKeyboard
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.base.BaseActivity
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.utils.Helpers
import kotlinx.android.synthetic.main.miles_goals_info_dialog.*
import java.text.DateFormat
import java.util.*

class MilesGoalsInfoDialog: BaseActivity() {

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