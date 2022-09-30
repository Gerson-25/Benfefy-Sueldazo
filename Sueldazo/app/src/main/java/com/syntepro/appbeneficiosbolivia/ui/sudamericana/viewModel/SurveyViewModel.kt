package com.syntepro.appbeneficiosbolivia.ui.sudamericana.viewModel

import androidx.lifecycle.MutableLiveData
import com.merckers.core.platform.BaseViewModel
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.sudamericana.model.SurveyRequest
import com.syntepro.appbeneficiosbolivia.ui.sudamericana.usecase.DoSaveSurvey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SurveyViewModel
@Inject
constructor(
        private val doSaveSurvey: DoSaveSurvey
): BaseViewModel() {

    val saveSurvey: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()

    fun saveUserSurvey(request: SurveyRequest) =
            doSaveSurvey(DoSaveSurvey.Params(request)) {
                it.fold(::handleFailure, ::handleUserSurvey)
            }

    private fun handleUserSurvey(response: BaseResponse<Boolean>) {
        this.saveSurvey.value = response
    }

}