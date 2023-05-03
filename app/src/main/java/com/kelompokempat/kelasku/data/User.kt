package com.kelompokempat.kelasku.data

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @Expose
    @SerializedName("name")
    val name: String?,
    @Expose
    @SerializedName("email")
    val email: String?,
    @Expose
    @SerializedName("photo")
    val photo: String?,
    @Expose
    @SerializedName("banner_photo")
    val bannerPhoto: String?,
    @Expose
    @SerializedName("phone")
    val phone: String?,
    @Expose
    @SerializedName("created_at")
    val createdAt: String?,
    @Expose
    @SerializedName("total_likes")
    val totalLikes: Int?,
    @Expose
    @SerializedName("school")
    val school: Schools?
) : Parcelable {

    @Parcelize
    data class Schools(
        @Expose
        @SerializedName("id")
        val id: Int?,
        @Expose
        @SerializedName("school_name")
        val school_name: String
    ) : Parcelable {
        override fun toString(): String {
            return school_name
        }
    }

}