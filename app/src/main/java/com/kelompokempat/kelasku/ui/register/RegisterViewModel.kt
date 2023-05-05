package com.kelompokempat.kelasku.ui.register

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.google.gson.Gson
import com.kelompokempat.kelasku.api.ApiService
import com.kelompokempat.kelasku.base.BaseViewModel
import com.kelompokempat.kelasku.data.Schools
import com.kelompokempat.kelasku.data.response.RegisterResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson
    ): BaseViewModel() {

    private val _schools = MutableSharedFlow<List<Schools>>()
    val schools = _schools.asSharedFlow()
    private val _registerResponse = MutableSharedFlow<RegisterResponse>()

    fun getSchools() = viewModelScope.launch {
        ApiObserver({ apiService.getSchools()},false, object : ApiObserver.ResponseListener{
            override suspend fun onSuccess(response: JSONObject) {
                val status = response.getInt(ApiCode.STATUS)
                if (status == ApiCode.SUCCESS){
                    val data = response.getJSONArray(ApiCode.DATA).toList<Schools>(gson)
                    _schools.emit(data)
                }
            }
        })
    }

    fun register(name: String, email: String, phone: String, password: String, passwordConfirmation: String, schoolId: String?) = viewModelScope.launch {
        ApiObserver.run(
            block = {apiService.register(name, email, phone, password, passwordConfirmation, schoolId)},
            toast = false,
            listener = object : ApiObserver.ModelResponseListener<RegisterResponse> {

                override suspend fun onLoading(response: RegisterResponse) {
                    _registerResponse.emit(response)
                    _apiResponse.emit(ApiResponse().responseLoading("Signing In…"))
                }

                override suspend fun onSuccess(response: RegisterResponse) {
                    _registerResponse.emit(response)
                    _apiResponse.emit(ApiResponse().responseSuccess("Signed In"))
                }

            }
        )

    }

}