package com.syntepro.appbeneficiosbolivia.ui.sudamericana.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.sudamericana.model.SudamericanaRepository
import com.syntepro.appbeneficiosbolivia.ui.sudamericana.model.SurveyRequest
import javax.inject.Inject

class DoSaveSurvey
@Inject constructor(private val sudamericanaRepository: SudamericanaRepository) : UseCase<BaseResponse<Boolean>, DoSaveSurvey.Params>() {

    override suspend fun run(params: Params) = sudamericanaRepository.saveSurvey(params.request)

    data class Params(val request: SurveyRequest)
}