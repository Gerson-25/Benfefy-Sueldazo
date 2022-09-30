package com.syntepro.appbeneficiosbolivia.ui.home.model

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.merckers.core.exception.Failure
import com.merckers.core.functional.Either
import com.merckers.core.platform.NetworkHandler
import com.syntepro.appbeneficiosbolivia.base.BaseRepository
import com.syntepro.appbeneficiosbolivia.entity.service.*
import com.syntepro.appbeneficiosbolivia.ui.shop.model.GiftCard
import com.syntepro.appbeneficiosbolivia.ui.shop.model.GiftCardRequest
import com.syntepro.appbeneficiosbolivia.ui.shop.model.ArticleResponse
import com.syntepro.appbeneficiosbolivia.ui.shop.model.ArticleRequest

import javax.inject.Inject

interface HomeRepository {

    fun getCategories(request: CategoryRequest): Either<Failure, BaseResponse<List<Category>>>
    fun getItems(request: ArticleRequest): Either<Failure, BaseResponse<List<ArticleResponse>>>
    fun getParameters(request: ParameterRequest): Either<Failure, BaseResponse<ParameterResponse>>
    fun getFavorites(request: FavoriteRequest): Either<Failure, BaseResponse<List<FavoriteResponse>>>
    fun getBanners(request: BannerRequest): Either<Failure, BaseResponse<List<BannerResponse>>>
    fun getPurchaseProducts(request: PurchasedProductsRequest): Either<Failure, BaseResponse<List<PurchasedProductsResponse>>>
    fun getStates(request: StatesRequest): Either<Failure, BaseResponse<List<StatesResponse>>>
    fun getGiftCards(request: GiftCardRequest): Either<Failure, BaseResponse<List<GiftCard>>>
    fun getFeaturedGiftCards(request: FeaturedGiftCardRequest): Either<Failure, BaseResponse<List<GiftCard>>>
    fun getUserSavings(request: UserSavingsRequest): Either<Failure, BaseResponse<Double>>

    class Network
    @Inject constructor(
            private val networkHandler: NetworkHandler,
            private val service: HomeService
    ) : HomeRepository, BaseRepository() {

        override fun getCategories(request: CategoryRequest): Either<Failure, BaseResponse<List<Category>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getCategories(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getItems(request: ArticleRequest): Either<Failure, BaseResponse<List<ArticleResponse>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getItems(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getParameters(request: ParameterRequest): Either<Failure, BaseResponse<ParameterResponse>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getParameters(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getFavorites(request: FavoriteRequest): Either<Failure, BaseResponse<List<FavoriteResponse>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getFavorites(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getBanners(request: BannerRequest): Either<Failure, BaseResponse<List<BannerResponse>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getBanners(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getPurchaseProducts(request: PurchasedProductsRequest): Either<Failure, BaseResponse<List<PurchasedProductsResponse>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getPurchaseProducts(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getStates(request: StatesRequest): Either<Failure, BaseResponse<List<StatesResponse>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getStates(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getGiftCards(request: GiftCardRequest): Either<Failure, BaseResponse<List<GiftCard>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getGiftCards(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getFeaturedGiftCards(request: FeaturedGiftCardRequest): Either<Failure, BaseResponse<List<GiftCard>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getFeaturedGiftCards(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getUserSavings(request: UserSavingsRequest): Either<Failure, BaseResponse<Double>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getUserSavings(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

    }
}