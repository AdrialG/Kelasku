package com.kelompokempat.kelasku.ui.detail

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import com.kelompokempat.kelasku.api.ApiService
import com.kelompokempat.kelasku.base.BaseObserver
import com.kelompokempat.kelasku.base.BaseViewModel
import com.kelompokempat.kelasku.data.FriendsDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val observer: BaseObserver
    ): BaseViewModel() {

    private val _friends = MutableSharedFlow<FriendsDetail>()
    val friends = _friends.asSharedFlow()

    fun getFriendsDetail(id: String?) = viewModelScope.launch {
        observer(
            block = { apiService.getFriendsDetail(id) },
            toast = false,
            responseListener = object : ApiObserver.ResponseListener{
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<FriendsDetail>(gson)
                    _apiResponse.emit(ApiResponse().responseSuccess())
                    _friends.emit(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            })
    }

    fun colek(id: String?) = viewModelScope.launch {
        observer(
            block = { apiService.colek(id)},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener{
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.emit(ApiResponse().responseSuccess())
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            })
    }

    fun like(id: String?) = viewModelScope.launch {
        observer(
            block = { apiService.like(id)},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener{
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.emit(ApiResponse().responseSuccess())
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            })
    }

}