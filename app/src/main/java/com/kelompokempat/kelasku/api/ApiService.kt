package com.kelompokempat.kelasku.api

import com.kelompokempat.kelasku.data.response.LoginResponse
import com.kelompokempat.kelasku.data.response.RegisterResponse
import retrofit2.http.*

interface ApiService {

    @POST("auth/refresh")
    suspend fun refreshToken(): String

    @FormUrlEncoded
    @POST("auth/login?expires_in=1")
    suspend fun login(
        @Field("email_or_phone") emailOrPhone: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("auth/me")
    suspend fun getProfile(
    ): String

    @FormUrlEncoded
    @POST("user/profile")
    suspend fun updateProfile(
        @Field("name") name : String,
        @Field("school_id") school : String
    ): String

    @FormUrlEncoded
    @POST("user/profile")
    suspend fun updateProfilePicture(
        @Field("name") name : String,
        @Field("school_id") school : String,
        @Field("photo") photo : String
    ): String


    @FormUrlEncoded
    @POST("user/profile")
    suspend fun updateProfileBanner(
        @Field("name") name : String,
        @Field("school_id") school : String,
        @Field("banner_photo") bannerPhoto : String
    ): String

    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String,
        @Field("school_id") schoolId: String?
    ): RegisterResponse

    @FormUrlEncoded
    @POST("auth/change-password")
    suspend fun updatePassword(
        @Field("old_password") oldPassword : String,
        @Field("new_password") newPassword : String,
        @Field("password_confirmation") passwordConfirmation : String
    ): String

    @GET("schools")
    suspend fun getSchools(
    ): String

    @GET("friends")
    suspend fun getFriends(
    ): String

    @GET("friends/{id}")
    suspend fun getFriendsDetail(
        @Path("id") id : String?
    ): String

    @FormUrlEncoded
    @POST("colek/{id}")
    suspend fun colek(
        @Path("id") id : String?
    ): String

    @DELETE("auth/logout")
    suspend fun logout(
    ):String

}