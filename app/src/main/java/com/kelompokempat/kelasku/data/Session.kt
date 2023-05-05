package com.kelompokempat.kelasku.data

import android.content.Context
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.toJson
import com.crocodic.core.extension.toObject
import com.google.gson.Gson

class Session(context: Context, private val gson: Gson) : CoreSession(context) {
    fun saveUser(user: User) {
        setValue(PREF_USER, user.toJson(gson))
    }

    fun getUser(): User? {
        getString(PREF_USER).also {
            return if (it.isEmpty()) {
                null
            } else {
                it.toObject<User>(gson)
            }
        }
    }

    fun clearUser() = setValue(PREF_USER, "")

    companion object {
        const val PREF_USER = "user"
    }
}