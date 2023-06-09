package com.kelompokempat.kelasku.ui.editprofile

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import com.kelompokempat.kelasku.api.ApiService
import com.kelompokempat.kelasku.base.BaseObserver
import com.kelompokempat.kelasku.base.BaseViewModel
import com.kelompokempat.kelasku.data.Schools
import com.kelompokempat.kelasku.data.Session
import com.kelompokempat.kelasku.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session,
    private val observer: BaseObserver
    ): BaseViewModel() {

    private val _schools = MutableSharedFlow<List<Schools>>()
    val schools = _schools.asSharedFlow()

    fun getSchools() = viewModelScope.launch {
        ApiObserver({ apiService.getSchools()},false, object : ApiObserver.ResponseListener{
            override suspend fun onSuccess(response: JSONObject) {
                val data = response.getJSONArray(ApiCode.DATA).toList<Schools>(gson)
                _schools.emit(data)
            }
        })
    }

    fun getProfile(
    ) = viewModelScope.launch {
        observer(
            block = { apiService.getProfile() },
            toast = false,
            responseListener = object : ApiObserver.ResponseListener{
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
                    session.saveUser(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)

                }
            })
    }

    fun updateProfile(name: String, school: String) =
        viewModelScope.launch {
            observer(
                block = {
                    apiService.updateProfile(name, school)
                },
                toast = false,
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        _apiResponse.emit(ApiResponse().responseSuccess("Profile Updated"))
                    }

                    override suspend fun onError(response: ApiResponse) {
                        super.onError(response)
                        _apiResponse.emit(ApiResponse().responseError())
                    }
                })
        }

    fun updateProfilePicture(name: String, school: String, photo: File) =
        viewModelScope.launch {
            val fileBody = photo.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("photo", photo.name, fileBody)
            observer(
                block = {
                    apiService.updateProfilePicture(name, school, filePart)
                },
                toast = false,
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        _apiResponse.emit(ApiResponse().responseSuccess("Profile Updated"))
                    }

                    override suspend fun onError(response: ApiResponse) {
                        super.onError(response)
                        _apiResponse.emit(ApiResponse().responseError())
                    }
                })
        }

    fun updateProfileBanner(name: String, school: String, banner_photo: File) =
        viewModelScope.launch {
            val fileBody = banner_photo.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("banner_photo", banner_photo.name, fileBody)
            observer(
                block = {
                    apiService.updateProfileBanner(name, school, filePart)
                },
                toast = false,
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        _apiResponse.emit(ApiResponse().responseSuccess("Profile Updated"))
                    }

                    override suspend fun onError(response: ApiResponse) {
                        super.onError(response)
                        _apiResponse.emit(ApiResponse().responseError())
                    }
                })
        }

    fun updateProfileAll(name: String, school: String, profilePictureFile: File, profileBannerFile: File) =
        viewModelScope.launch {
            val fileBodyPicture = profilePictureFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val filePartPicture = MultipartBody.Part.createFormData("photo", profilePictureFile.name, fileBodyPicture)
            val fileBodyBanner = profileBannerFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val filePartBanner = MultipartBody.Part.createFormData("banner_photo", profileBannerFile.name, fileBodyBanner)
            observer(
                block = {
                    apiService.updateProfileAll(name, school, filePartPicture, filePartBanner)
                },
                toast = false,
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        _apiResponse.emit(ApiResponse().responseSuccess("Profile Updated"))
                    }

                    override suspend fun onError(response: ApiResponse) {
                        super.onError(response)
                        _apiResponse.emit(ApiResponse().responseError())
                    }
                })
        }

}