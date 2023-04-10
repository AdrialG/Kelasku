package com.kelompokempat.kelasku.api

import com.kelompokempat.kelasku.data.response.LoginResponse
import com.kelompokempat.kelasku.data.response.RegisterResponse
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email_or_phone") emailOrPhone: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String,
        @Field("school_id") schoolId: Int
    ): RegisterResponse

    @GET("schools")
    suspend fun getSchools(
    ): String

}