package com.syntepro.appbeneficiosbolivia.ui.sudamericana.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.merckers.core.extension.hideKeyboard
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.sudamericana.ui.activities.SurveyActivity
import kotlinx.android.synthetic.main.fragment_sixth_question.view.*

class SixthQuestionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sixth_question, container, false)

        view.endQuestions.setOnClickListener {
            activity?.hideKeyboard(it)
            view.endQuestions.text = ""
            view.progress_bar_accept.visibility = View.VISIBLE
            val host = (activity as SurveyActivity)
            host.doctorsName = view.doctorNameId.text.toString()
            host.doctorsAddress = view.doctorAddressId.text.toString()
            host.updateSurvey()
        }

        return view
    }

}