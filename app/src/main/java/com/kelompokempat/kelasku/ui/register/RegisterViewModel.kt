package com.kelompokempat.kelasku.ui.register

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.extension.toList
import com.google.gson.Gson
import com.kelompokempat.kelasku.api.ApiService
import com.kelompokempat.kelasku.base.BaseViewModel
import com.kelompokempat.kelasku.data.Schools
import com.kelompokempat.kelasku.data.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val apiService: ApiService, private val gson: Gson, private val session: Session): BaseViewModel() {

    private val _schools = MutableSharedFlow<List<Schools>>()
    val schools = _schools.asSharedFlow()

    fun getSchools() = viewModelScope.launch {
        ApiObserver({ apiService.getSchools()},false, object : ApiObserver.ResponseListener{
            override suspend fun onSuccess(response: JSONObject) {
                val status = response.getInt(ApiCode.STATUS)
                if (status == ApiCode.SUCCESS){
                    val data = response.getJSONArray(ApiCode.DATA).toList<Schools>(gson)
                    _schools.emit(data)

                } else {
                    val message = response.getString(ApiCode.MESSAGE)
                }
            }
        })
    }

}