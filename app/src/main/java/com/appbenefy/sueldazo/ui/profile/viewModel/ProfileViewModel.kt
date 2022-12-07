package com.appbenefy.sueldazo.ui.profile.viewModel

import androidx.lifecycle.MutableLiveData
import com.merckers.core.platform.BaseViewModel
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.profile.model.*
import com.appbenefy.sueldazo.ui.profile.usecase.DoTransactionDetail
import com.appbenefy.sueldazo.ui.profile.usecase.DoUserStats
import com.appbenefy.sueldazo.ui.profile.usecase.DoUserTransactions
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileViewModel
@Inject
constructor(
        private val doUserStats: DoUserStats,
        private val doUserTransactions: DoUserTransactions,
        private val doTransactionDetail: DoTransactionDetail
): BaseViewModel() {

    val userStats: MutableLiveData<BaseResponse<UserStatsResponse>> = MutableLiveData()
    val userTransactions: MutableLiveData<BaseResponse<List<TransactionResponse>>> = MutableLiveData()
    val transactionDetail: MutableLiveData<BaseResponse<TransactionDetailResponse>> = MutableLiveData()

    fun getUserStats(request: UserStatsRequest) =
            doUserStats(DoUserStats.Params(request)) {
                it.fold(::handleFailure, ::handleUserStats)
            }

    fun getUserTransactions(request: TransactionRequest) =
            doUserTransactions(DoUserTransactions.Params(request)) {
                it.fold(::handleFailure, ::handleUserTransactions)
            }

    fun getTransactionDetail(request: TransactionDetailRequest) =
            doTransactionDetail(DoTransactionDetail.Params(request)) {
                it.fold(::handleFailure, ::handleTransactionDetail)
            }

    private fun handleUserStats(response: BaseResponse<UserStatsResponse>) {
        this.userStats.value = response
    }

    private fun handleUserTransactions(response: BaseResponse<List<TransactionResponse>>) {
        this.userTransactions.value = response
    }

    private fun handleTransactionDetail(response: BaseResponse<TransactionDetailResponse>) {
        this.transactionDetail.value = response
    }

}