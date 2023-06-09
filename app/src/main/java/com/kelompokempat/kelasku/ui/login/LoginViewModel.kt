package com.kelompokempat.kelasku.ui.login

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.google.gson.Gson
import com.kelompokempat.kelasku.api.ApiService
import com.kelompokempat.kelasku.base.BaseViewModel
import com.kelompokempat.kelasku.data.Const
import com.kelompokempat.kelasku.data.Session
import com.kelompokempat.kelasku.data.response.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val session: Session
    ) : BaseViewModel() {

    private val _loginResponse = MutableSharedFlow<LoginResponse>()

    fun login(emailOrPhone: String, password: String ) = viewModelScope.launch {
        ApiObserver.run(
            block = {apiService.login(emailOrPhone, password)},
            toast = false,
            listener = object : ApiObserver.ModelResponseListener<LoginResponse> {

                override suspend fun onLoading(response: LoginResponse) {
                    _loginResponse.emit(response)
                    _apiResponse.emit(ApiResponse().responseLoading("Logging In…"))
                }

                override suspend fun onSuccess(response: LoginResponse) {
                    response.token?.let { session.setValue(Const.TOKEN.API_TOKEN, it) }
                    _loginResponse.emit(response)
                    _apiResponse.emit(ApiResponse().responseSuccess("Logged In"))
                }
            }
        )
    }

}