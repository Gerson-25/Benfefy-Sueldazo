package com.syntepro.appbeneficiosbolivia.ui.sudamericana.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.merckers.core.extension.hideKeyboard
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.sudamericana.ui.activities.SurveyActivity
import kotlinx.android.synthetic.main.fragment_fourth_question.view.*

class FourthQuestionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_fourth_question, container, false)

        view.next.setOnClickListener {
            activity?.hideKeyboard(it)
            if (validateData(view)) {
                val host = (activity as SurveyActivity)
                host.userWeight = view.weightField.text.toString().toInt()
                host.userHeight = view.heightField.text.toString().toInt()
                host.moveNext()
            }
        }

        return view
    }

    private fun validateData(view: View): Boolean {
        return when {
            view.weightField.text.isNullOrEmpty() -> {
                view.weightLayout.error = activity?.getString(R.string.c_required)
                view.heightLayout.error = null
                false
            }
            view.heightField.text.isNullOrEmpty() -> {
                view.weightLayout.error = null
                view.heightLayout.error = activity?.getString(R.string.c_required)
                false
            }

            else -> {
                view.weightLayout.error = null
                view.heightLayout.error = null
                true
            }
        }
    }

}