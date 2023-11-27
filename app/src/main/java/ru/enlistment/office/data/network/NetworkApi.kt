package ru.enlistment.office.data.network

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.enlistment.office.data.network.model.auth.AuthRequest
import ru.enlistment.office.data.network.model.auth.AuthResponse
import ru.enlistment.office.data.network.model.user.Accounting
import ru.enlistment.office.data.network.model.user.CreateAccounting
import ru.enlistment.office.data.network.model.user.EnlistmentOffice
import ru.enlistment.office.data.network.model.user.EnlistmentSummonDocumentType
import ru.enlistment.office.data.network.model.user.Passport
import ru.enlistment.office.data.network.model.user.Study
import ru.enlistment.office.data.network.model.user.User
import ru.enlistment.office.data.network.model.user.UserShort
import ru.enlistment.office.data.network.model.user.Work

interface NetworkApi {

    @POST("/users/login")
    suspend fun login(@Body body: AuthRequest): Response<AuthResponse>

    @GET("/users/info")
    suspend fun getUserInfo(
        @Header("Authorization") token: String
    ): Response<User>

    @GET("/users/{id}")
    suspend fun getUserById(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Response<User>

    @GET("/users")
    suspend fun getUserAll(
        @Header("Authorization") token: String
    ): Response<List<UserShort>>

    @POST("/users/{id}/accounting")
    suspend fun userAddAccounting(
        @Path("id") id: Int,
        @Body body: CreateAccounting,
        @Header("Authorization") token: String
    ): Response<Unit?>

    @POST("/users/accounting/{id}/work")
    suspend fun userAddWork(
        @Path("id") id: Int,
        @Body body: Work,
        @Header("Authorization") token: String
    ): Response<Unit?>

    @POST("/users/accounting/{id}/study")
    suspend fun userAddStudy(
        @Path("id") id: Int,
        @Body body: Study,
        @Header("Authorization") token: String
    ): Response<Unit?>

    @POST("/users/accounting/{id}/passport")
    suspend fun userAddPassport(
        @Path("id") id: Int,
        @Body body: Passport,
        @Header("Authorization") token: String
    ): Response<Unit?>

    @POST("/users")
    suspend fun addUser(
        @Query("email") email: String,
        @Query("password") password: String,
        @Header("Authorization") token: String
    ): Response<Unit?>

    @POST("/enlistment-summon-document")
    suspend fun addEnlistmentSummon(
        @Query("account_id") accountId: Int,
        @Query("type") type: EnlistmentSummonDocumentType,
        @Header("Authorization") token: String
    ): Response<Unit?>

    @GET("/enlistment-offices")
    suspend fun getAllEnlistmentOffices(): Response<List<EnlistmentOffice>>
}

private val retrofit = Retrofit.Builder()
    .baseUrl("http://10.0.2.2:8080")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val networkApi by lazy { retrofit.create<NetworkApi>() }