package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.merckers.core.exception.Failure
import com.merckers.core.functional.Either
import com.merckers.core.platform.NetworkHandler
import com.syntepro.appbeneficiosbolivia.base.BaseRepository
import javax.inject.Inject

interface LoyaltyPlanRepository {

    fun getLoyaltyPlansByUser(request: LoyaltyPlanListRequest): Either<Failure, BaseResponse<LoyaltyPlanListResponse>>
    fun getCommerceByCategory(request: CommerceRequest): Either<Failure, BaseResponse<List<Commerce>>>
    fun getPlansByCommerce(request: PlanByCommerceRequest): Either<Failure, BaseResponse<List<PlanByCommerceResponse>>>
    fun affiliateToPlan(request: AffiliateToPlanRequest): Either<Failure, BaseResponse<AffiliateToPlanResponse>>
    fun unlinkPlan(request: UnlinkPlanRequest): Either<Failure, BaseResponse<Boolean>>
    fun getCardPlanDetail(request: PlanByUserDetailRequest): Either<Failure, BaseResponse<PlanCardDetailResponse>>
    fun getCorporatePlanDetail(request: PlanByUserDetailRequest): Either<Failure, BaseResponse<PlanCorporateDetailResponse>>
    fun getMilesPlanDetail(request: PlanByUserDetailRequest): Either<Failure, BaseResponse<PlanMilesDetailResponse>>
    fun getPlanDetail(request: PlanDetailRequest): Either<Failure, BaseResponse<PlanDetailResponse>>
    fun getCardDetail(request: CardDetailRequest): Either<Failure, BaseResponse<CardDetailResponse>>
    fun saveMilesGoals(request: MilesGoalsRequest): Either<Failure, BaseResponse<Boolean>>

    class Network
    @Inject constructor(
            private val networkHandler: NetworkHandler,
            private val service: LoyaltyPlanListService
    ) : LoyaltyPlanRepository, BaseRepository() {

        override fun getLoyaltyPlansByUser(request: LoyaltyPlanListRequest): Either<Failure, BaseResponse<LoyaltyPlanListResponse>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getLoyaltyPlansByUser(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getCommerceByCategory(request: CommerceRequest): Either<Failure, BaseResponse<List<Commerce>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getCommerceByCategory(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getPlansByCommerce(request: PlanByCommerceRequest): Either<Failure, BaseResponse<List<PlanByCommerceResponse>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getPlansByCommerce(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun affiliateToPlan(request: AffiliateToPlanRequest): Either<Failure, BaseResponse<AffiliateToPlanResponse>> {
            return when (networkHandler.isConnected) {
                true -> request(service.affiliateToPlan(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun unlinkPlan(request: UnlinkPlanRequest): Either<Failure, BaseResponse<Boolean>> {
            return when (networkHandler.isConnected) {
                true -> request(service.unlinkPlan(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getCardPlanDetail(request: PlanByUserDetailRequest): Either<Failure, BaseResponse<PlanCardDetailResponse>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getCardPlanDetail(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getCorporatePlanDetail(request: PlanByUserDetailRequest): Either<Failure, BaseResponse<PlanCorporateDetailResponse>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getCorporatelanDetail(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getMilesPlanDetail(request: PlanByUserDetailRequest): Either<Failure, BaseResponse<PlanMilesDetailResponse>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getMileslanDetail(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getPlanDetail(request: PlanDetailRequest): Either<Failure, BaseResponse<PlanDetailResponse>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getPlanlanDetail(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getCardDetail(request: CardDetailRequest): Either<Failure, BaseResponse<CardDetailResponse>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getCardlanDetail(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun saveMilesGoals(request: MilesGoalsRequest): Either<Failure, BaseResponse<Boolean>> {
            return when (networkHandler.isConnected) {
                true -> request(service.saveMilesGoals(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

    }
}
