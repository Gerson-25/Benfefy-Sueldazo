package com.appbenefy.sueldazo.service

import com.appbenefy.sueldazo.entity.service.*
import com.appbenefy.sueldazo.entity.service.State
import com.appbenefy.sueldazo.ui.benefy.model.PurchasedProductDetail
import com.appbenefy.sueldazo.ui.benefy.model.PurchasedProductDetailRequest
import com.appbenefy.sueldazo.ui.home.model.RegisterCodeRequest
import com.appbenefy.sueldazo.ui.home.model.RegisterCodeResponse
import com.appbenefy.sueldazo.ui.login.model.*
import com.appbenefy.sueldazo.utils.Constants
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
    @POST("api/User/GetUserByDocument")
    suspend fun getUserByDocument(@Body body: GetUserRequest): Response<BaseResponseModel<User>>

    // User
    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/Login/ValidarClientePorDocumento")
    suspend fun validarClientePorDoucmento(@Body body: ValidarClienteRequest): Response<BaseResponseModel<ValidarClienteResponse>>

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/Login/EnviarToken")
    suspend fun obtenerToken(@Body body: ObtenerTokenRequest): Response<BaseResponseModel<ObtenerTokenResponse>>


    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/Login/ValidarToken")
    suspend fun validateOtp(@Body body: ValidateTokenRequest): Response<BaseResponseModel<ValidateTokenResponse>>

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/User/SaveUser")
    suspend fun saveUser(@Body body: SaveUserRequest): Response<BaseResponseString>

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/User/SaveAnonymousUser")
    suspend fun SaveAnonymousUser(@Body body: SaveUserRequest): Response<BaseResponseString>

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/User/SaveToken")
    suspend fun addUserToken(@Body body: UserTokenRequest): Response<BaseResponseBoolean>

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/User/UpdateActualCountry")
    suspend fun updateActualCountry(@Body body: UpdateActualCountryRequest): Response<BaseResponseBoolean>

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/User/UpdateUser")
    suspend fun updateUser(@Body body: UserUpdateRequest): Response<BaseResponseBoolean>

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("api/User/SaveAnonymousUser")
    suspend fun updateAnonymousUser(@Body body: UserUpdateRequest): Response<BaseResponseBoolean>

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
}