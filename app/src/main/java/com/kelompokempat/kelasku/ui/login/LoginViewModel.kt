package com.kelompokempat.kelasku.ui.login

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.api.ApiStatus
import com.google.gson.Gson
import com.kelompokempat.kelasku.api.ApiService
import com.kelompokempat.kelasku.base.BaseViewModel
import com.kelompokempat.kelasku.data.Const
import com.kelompokempat.kelasku.data.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val apiService: ApiService, private val gson: Gson, private val session: Session) : BaseViewModel() {

    fun login(email_or_phone: String, password: String ) = viewModelScope.launch {

        _apiResponse.send(ApiResponse(ApiStatus.LOADING))
        ApiObserver({ apiService.login(email_or_phone, password) },
            false, object : ApiObserver.ResponseListener {

                override suspend fun onSuccess(response: JSONObject) {
                    val token = response.getString("token")
                    val message = response.getString("info")
                    session.setValue(Const.TOKEN.TOKEN,token)
                    _apiResponse.send(ApiResponse(ApiStatus.SUCCESS, message = message))

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.send(ApiResponse(ApiStatus.ERROR, message = "Login Failed"))

                }
            })
    }

}