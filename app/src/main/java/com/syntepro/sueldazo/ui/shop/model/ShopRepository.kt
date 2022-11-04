package com.syntepro.sueldazo.ui.shop.model

import com.merckers.core.exception.Failure
import com.merckers.core.functional.Either
import com.merckers.core.platform.NetworkHandler
import com.syntepro.sueldazo.base.BaseRepository
import com.syntepro.sueldazo.core.entities.BaseResponse
import javax.inject.Inject

interface ShopRepository {

    fun getGiftDetail(request: GiftDetailRequest): Either<Failure, BaseResponse<GiftDetailResponse>>

    class Network
    @Inject constructor(
            private val networkHandler: NetworkHandler,
            private val service: ShopService
    ) : ShopRepository, BaseRepository() {

        override fun getGiftDetail(request: GiftDetailRequest): Either<Failure, BaseResponse<GiftDetailResponse>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getGiftDetail(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

    }

}