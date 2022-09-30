package com.syntepro.appbeneficiosbolivia.ui.sudamericana.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.merckers.core.extension.hideKeyboard
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.sudamericana.ui.activities.SurveyActivity
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.fragment_second_question.*
import kotlinx.android.synthetic.main.fragment_second_question.view.*

class SecondQuestionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_second_question, container, false)

        view.next.setOnClickListener {
            activity?.hideKeyboard(it)
            if (validateData()) {
                val host = (activity as SurveyActivity)
                host.question2Check = !view.noRadioButton.isChecked
                host.question2Description = view.descriptionId.text.toString()
                host.moveNext()
            } else
                Functions.showWarning(requireContext(), requireContext().getString(R.string.sud_required))
        }

        return view
    }

    private fun validateData(): Boolean {
        return !yesRadioButton.isChecked
    }

}