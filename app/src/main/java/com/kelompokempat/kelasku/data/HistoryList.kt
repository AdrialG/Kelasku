package com.kelompokempat.kelasku.data

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryList(
    @Expose
    @SerializedName("id")
    val id: String?,
    @Expose
    @SerializedName("name")
    val name: String?,
    @Expose
    @SerializedName("photo")
    val photo: String?,
    @Expose
    @SerializedName("like_at")
    val like_at: String?,
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