package com.kelompokempat.kelasku.ui.home

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.extension.toList
import com.google.gson.Gson
import com.kelompokempat.kelasku.api.ApiService
import com.kelompokempat.kelasku.base.BaseViewModel
import com.kelompokempat.kelasku.data.FriendsList
import com.kelompokempat.kelasku.data.Schools
import com.kelompokempat.kelasku.data.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val apiService: ApiService, private val gson: Gson, private val session: Session): BaseViewModel() {

    private val _friends = MutableSharedFlow<List<FriendsList>>()
    val friends = _friends.asSharedFlow()

    fun getFriends() = viewModelScope.launch {
        ApiObserver({ apiService.getSchools()},false, object : ApiObserver.ResponseListener{
            override suspend fun onSuccess(response: JSONObject) {
                val status = response.getInt(ApiCode.STATUS)
                if (status == ApiCode.SUCCESS){
                    val data = response.getJSONArray(ApiCode.DATA).toList<FriendsList>(gson)
                    _friends.emit(data)

                } else {
                    val message = response.getString(ApiCode.MESSAGE)
                }
            }
        })
    }

}