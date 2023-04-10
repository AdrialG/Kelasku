package com.kelompokempat.kelasku.data.response

import com.crocodic.core.api.ModelResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kelompokempat.kelasku.data.User

data class RegisterResponse (
    @SerializedName("data")
    @Expose
    val user: User?
): ModelResponse()