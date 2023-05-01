package com.kelompokempat.kelasku.ui.editpassword

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import com.kelompokempat.kelasku.api.ApiService
import com.kelompokempat.kelasku.base.BaseObserver
import com.kelompokempat.kelasku.base.BaseViewModel
import com.kelompokempat.kelasku.data.Session
import com.kelompokempat.kelasku.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class EditPasswordViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session,
    private val observer: BaseObserver
) : BaseViewModel() {

    fun getProfile(
    ) = viewModelScope.launch {
        observer(
            block = { apiService.getProfile() },
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
                    _apiResponse.emit(ApiResponse().responseSuccess())
                    session.saveUser(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            })
    }

    fun updatePassword(oldPassword: String, newPassword: String, passwordConfirmation: String) =
        viewModelScope.launch {
            observer(
                block = {
                    apiService.updatePassword(
                        oldPassword,
                        newPassword,
                        passwordConfirmation
                    )
                },
                toast = false,
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        _apiResponse.emit(ApiResponse().responseSuccess("Password Updated"))
                    }

                    override suspend fun onError(response: ApiResponse) {
                        super.onError(response)
                        _apiResponse.emit(ApiResponse().responseError())
                    }

                })
        }

}