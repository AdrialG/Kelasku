package com.kelompokempat.kelasku.data

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FriendsDetail(
    @Expose
    @SerializedName("id")
    val id: String?,
    @Expose
    @SerializedName("name")
    val name: String?,
    @Expose
    @SerializedName("email")
    val email: String?,
    @Expose
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("banner_photo")
    val bannerPhoto: String?,
    @Expose
    @SerializedName("phone")
    val phone: String?,
    @Expose
    @SerializedName("total_likes")
    val totalLikes: Int?,
    @Expose
    @SerializedName("like_by_you")
    val likeByYou: Boolean,
    @Expose
    @SerializedName("created_at")
    val createdAt: Boolean,
    @Expose
    @SerializedName("school_name")
    val schoolName: String?,
) : Parcelable