package com.syntepro.sueldazo.ui.coupon.model

import com.merckers.core.exception.Failure
import com.merckers.core.functional.Either
import com.merckers.core.platform.NetworkHandler
import com.syntepro.sueldazo.base.BaseRepository
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.agency.model.AgencyResponse
import javax.inject.Inject

interface CouponRepository {

    fun getCouponList(request: CouponListRequest): Either<Failure, BaseResponse<List<CouponListResponse>>>
    fun getCouponDetail(request: CouponDetailRequest): Either<Failure, BaseResponse<CouponDetailResponse>>
    fun getCouponAgency(request: CouponAgencyRequest): Either<Failure, BaseResponse<List<AgencyResponse>>>
    fun updateCouponUserQuantity(request: UpdateCouponQuantityRequest): Either<Failure, BaseResponse<Boolean>>
    fun saveCouponRating(request: SaveCouponRatingRequest): Either<Failure, BaseResponse<Boolean>>
    fun getBestDiscounts(request: BestDiscountRequest): Either<Failure, BaseResponse<List<BestDiscountResponse>>>
    fun getFeaturedCoupons(request: FeaturedCouponRequest): Either<Failure, BaseResponse<List<FeaturedCouponResponse>>>

    class Network
    @Inject constructor(
            private val networkHandler: NetworkHandler,
            private val service: CouponService
    ) : CouponRepository, BaseRepository() {

        override fun getCouponList(request: CouponListRequest): Either<Failure, BaseResponse<List<CouponListResponse>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getCouponList(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getCouponDetail(request: CouponDetailRequest): Either<Failure, BaseResponse<CouponDetailResponse>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getCouponDetail(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getCouponAgency(request: CouponAgencyRequest): Either<Failure, BaseResponse<List<AgencyResponse>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getCouponAgency(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun updateCouponUserQuantity(request: UpdateCouponQuantityRequest): Either<Failure, BaseResponse<Boolean>> {
            return when (networkHandler.isConnected) {
                true -> request(service.updateCouponQuantity(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun saveCouponRating(request: SaveCouponRatingRequest): Either<Failure, BaseResponse<Boolean>> {
            return when (networkHandler.isConnected) {
                true -> request(service.saveCouponRating(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getBestDiscounts(request: BestDiscountRequest): Either<Failure, BaseResponse<List<BestDiscountResponse>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getBestDiscounts(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getFeaturedCoupons(request: FeaturedCouponRequest): Either<Failure, BaseResponse<List<FeaturedCouponResponse>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getFeaturedCoupons(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

    }

}