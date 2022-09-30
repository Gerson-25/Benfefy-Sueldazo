package com.syntepro.appbeneficiosbolivia.service

import com.syntepro.appbeneficiosbolivia.entity.service.*
import com.syntepro.appbeneficiosbolivia.entity.service.State
import com.syntepro.appbeneficiosbolivia.ui.benefy.model.PurchasedProductDetail
import com.syntepro.appbeneficiosbolivia.ui.benefy.model.PurchasedProductDetailRequest
import com.syntepro.appbeneficiosbolivia.ui.home.model.RegisterCodeRequest
import com.syntepro.appbeneficiosbolivia.ui.home.model.RegisterCodeResponse
import com.syntepro.appbeneficiosbolivia.ui.shop.model.*
import com.syntepro.appbeneficiosbolivia.utils.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NetworkService2 {

    // Version
    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/Configuration/GetVersions")
    suspend fun getVersions(@Body body: VersionsRequest): Response<BaseResponse<Versions>>

    // Token
    @Headers("Content-Type: application/json", "X-Api-Key: ${Constants.API_KEY}", "No-Authentication: true")
    @POST("api/AppMovil/Authenticate")
    suspend fun getToken(@Body body: TokenRequest): Response<BaseResponseString>

    // Get States
    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/Country/GetStates")
    suspend fun getStates(@Body body: StatesRequest): Response<BaseResponse<State>>

    // User
    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/User/GetUser")
    suspend fun getUser(@Body body: GetUserRequest): Response<BaseResponseModel<User>>

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/User/SaveUser")
    suspend fun saveUser(@Body body: SaveUserRequest): Response<BaseResponseString>

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/User/SaveTokenFirebase")
    suspend fun addUserToken(@Body body: UserTokenRequest): Response<BaseResponseBoolean>

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/User/UpdateActualCountry")
    suspend fun updateActualCountry(@Body body: UpdateActualCountryRequest): Response<BaseResponseBoolean>

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/User/UpdateUser")
    suspend fun updateUser(@Body body: UserUpdateRequest): Response<BaseResponseBoolean>

    // Items
    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/Item/GetItems")
    suspend fun getItems(@Body body: ArticleRequest): Response<BaseResponse<ArticleResponse>>

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/Item/GetItemDetail")
    suspend fun getItemDetail(@Body body: ItemDetailRequest): Response<BaseResponseModel<ArticleResponse>>

    // GiftCard
    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/Giftcard/GetGiftcards")
    suspend fun getGiftCards(@Body body: GiftCardRequest): Response<BaseResponse<GiftCard>>

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/Giftcard/GetGiftcardDetail")
    suspend fun getGiftCardDetail(@Body body: GiftCardDetailRequest): Response<BaseResponseModel<GiftCard>>

    // Purchase
    @Headers("Content-Type: application/json")
    @POST("api/Purchase/CreatePaymentOrder")
    suspend fun createPaymentOrder(@Body body: PaymentOrderRequest): Response<BaseResponseModel<PaymentOrderResponse>>

    @Headers("Content-Type: application/json")
    @POST("api/Purchase/GetPurchasedProductDetail")
    suspend fun getPurchasedProductDetail(@Body body: PurchasedProductDetailRequest): Response<BaseResponseModel<PurchasedProductDetail>>

    // Category
    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/BusinessLine/GetCategories")
    suspend fun getCategory(@Body body: CategoryRequest): Response<BaseResponse<Category>>

    // Favorite
    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/Favorite/AddFavorite")
    suspend fun addFavorite(@Body body: AddFavoriteRequest): Response<BaseResponseBoolean>

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/Favorite/RemoveFavorite")
    suspend fun removeFavorite(@Body body: AddFavoriteRequest): Response<BaseResponseBoolean>

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/PromotionalCode/RegisterPromotionalCode")
    suspend fun registerCode(@Body body: RegisterCodeRequest): Response<BaseResponseModel<RegisterCodeResponse>>

    // Delivery Schedules
    @Headers("Content-Type: application/json")
    @POST("api/ScheduledDelivery/GetDispatchPointsSchedule")
    suspend fun getDispatchPointSchedule(@Body body: ScheduleRequest): Response<BaseResponseModel<ScheduleResponse>>

    @Headers("Content-Type: application/json")
    @POST("api/ScheduledDelivery/GetDistanceAmountDelivery")
    suspend fun getDistanceAmountDelivery(@Body body: DistanceAmountDeliveryRequest): Response<BaseResponseModel<DistanceAmountDeliveryResponse>>

    // Process Payment
    @Headers("Content-Type: application/json")
    @POST("api/ProcessCardPayment/ProcessPayment")
    suspend fun processPayment(@Body body: ProcessPaymentRequest): Response<BaseResponseBoolean>

    // Countries List
    @Headers("Content-Type: application/json")
    @POST("api/Country/GetCountries")
    suspend fun getCountries(@Body body: CountriesRequest): Response<BaseResponse<CountriesResponse>>
}