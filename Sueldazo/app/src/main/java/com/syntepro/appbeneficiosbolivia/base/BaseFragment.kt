package com.syntepro.appbeneficiosbolivia.base

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.syntepro.appbeneficiosbolivia.R

open class BaseFragment: Fragment() {
    private var mEmptyId: ConstraintLayout? = null
    private var mProgressId: LinearLayout? = null
    protected lateinit var rootView: View

    open fun showProgress(show: Boolean, center: Boolean = false) {
        if( mProgressId?.layoutParams is CoordinatorLayout.LayoutParams) {
            val params = CoordinatorLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            if (center)
                params.apply { gravity = Gravity.CENTER }
            else
                params.apply { gravity = Gravity.BOTTOM }
            mProgressId?.layoutParams = params
        }
        mProgressId?.visibility = if(show) View.VISIBLE else View.GONE
    }

    open fun showEmptyLayout(show: Boolean)  {
        mEmptyId?.visibility = if(show) View.VISIBLE else View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initConf()
    }

    private fun initConf() {
        mEmptyId = rootView.findViewById(R.id.emptyId)
        mProgressId = rootView.findViewById(R.id.progressId)

    }

    open fun getData() {}
}