package com.kelompokempat.kelasku.ui.history

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.google.gson.Gson
import com.kelompokempat.kelasku.api.ApiService
import com.kelompokempat.kelasku.base.BaseObserver
import com.kelompokempat.kelasku.base.BaseViewModel
import com.kelompokempat.kelasku.data.HistoryList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val observer: BaseObserver
): BaseViewModel() {

    private val _history = MutableSharedFlow<List<HistoryList>>()
    val history = _history.asSharedFlow()

    fun getLikeHistory() = viewModelScope.launch {
        observer(
            block = { apiService.getLikesHistory() },
            toast = false,
            responseListener = object : ApiObserver.ResponseListener{
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<HistoryList>(gson)
                    _apiResponse.emit(ApiResponse().responseSuccess())
                    _history.emit(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            })
    }

}