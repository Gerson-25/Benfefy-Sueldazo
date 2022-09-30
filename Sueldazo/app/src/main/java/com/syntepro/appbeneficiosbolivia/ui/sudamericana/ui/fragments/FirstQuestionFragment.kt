package com.syntepro.appbeneficiosbolivia.ui.sudamericana.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.merckers.core.extension.hideKeyboard
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.shop.model.ArticleResponse
import com.syntepro.appbeneficiosbolivia.ui.sudamericana.ui.activities.SurveyActivity
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.fragment_first_question.*
import kotlinx.android.synthetic.main.fragment_first_question.view.*

class FirstQuestionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_first_question, container, false)

        val host = (activity as SurveyActivity)

        if (host.insuredType == ArticleResponse.COVID_INSURANCE) {
            view.questionNumberId.text = requireContext().getString(R.string.unique_question)
            view.questionOneId.text = requireContext().getString(R.string.covid_question)
            view.next.text = requireContext().getString(R.string.finish)
        }

        view.next.setOnClickListener {
            activity?.hideKeyboard(it)
            if (validateData()) {
                if (host.insuredType == ArticleResponse.COVID_INSURANCE) {
                    activity?.hideKeyboard(it)
                    host.updateSurvey()
                } else {
                    host.question1Check = !view.noRadioButton.isChecked
                    host.question1Description = view.descriptionId.text.toString()
                    host.moveNext()
                }
            } else
                Functions.showWarning(requireContext(), requireContext().getString(R.string.sud_required))
        }

        return view
    }

    private fun validateData(): Boolean {
        return !yesRadioButton.isChecked
    }

}