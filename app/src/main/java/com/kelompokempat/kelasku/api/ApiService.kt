package com.kelompokempat.kelasku.api

import com.kelompokempat.kelasku.data.response.LoginResponse
import com.kelompokempat.kelasku.data.response.RegisterResponse
import okhttp3.MultipartBody
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
        @Field("name") name : String?,
        @Field("school_id") school : String
    ): String

    @Multipart
    @POST("user/profile")
    suspend fun updateProfilePicture(
        @Part("name") name : String?,
        @Part("school_id") school : String,
        @Part photo : MultipartBody.Part?
    ): String


    @Multipart
    @POST("user/profile")
    suspend fun updateProfileBanner(
        @Part("name") name : String?,
        @Part("school_id") school : String,
        @Part banner_photo : MultipartBody.Part?
    ): String

    @Multipart
    @POST("user/profile")
    suspend fun updateProfileAll(
        @Part("name") name : String?,
        @Part("school_id") school : String,
        @Part photo : MultipartBody.Part?,
        @Part banner_photo : MultipartBody.Part?
    ): String

    @FormUrlEncoded
    @POST("user/profile")
    suspend fun updateProfileCredentials(
        @Field("phone") phone : String?,
        @Field("email") email : String
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

    @GET("likes/me")
    suspend fun getLikesHistory(
    ): String

    @FormUrlEncoded
    @POST("colek")
    suspend fun colek(
        @Field("user_id") id : String?
    ): String

    @FormUrlEncoded
    @POST("likes")
    suspend fun like(
        @Field("user_id") id : String?
    ): String

    @DELETE("auth/logout")
    suspend fun logout(
    ):String

}