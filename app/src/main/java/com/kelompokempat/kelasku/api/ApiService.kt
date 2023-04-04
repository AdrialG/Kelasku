package com.kelompokempat.kelasku.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email_or_phone") email_or_phone: String,
        @Field("password") password: String
    ): String

}