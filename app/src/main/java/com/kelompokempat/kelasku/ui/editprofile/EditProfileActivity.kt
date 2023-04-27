package com.kelompokempat.kelasku.ui.editprofile

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.data.Session
import com.kelompokempat.kelasku.databinding.ActivityProfileBinding
import com.kelompokempat.kelasku.databinding.EditProfileActivityBinding
import com.kelompokempat.kelasku.ui.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileActivity : BaseActivity<EditProfileActivityBinding, EditProfileViewModel>(R.layout.edit_profile_activity) {

    @Inject
    lateinit var session : Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
//        getUser()

        val user = session.getUser()
        if (user != null) {
            binding.data = user
        }

        binding.editProfileBack.setOnClickListener {
            finish()
        }

        binding.saveButton.setOnClickListener {
//            validateForm()
        }

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Update Profile")
                            ApiStatus.SUCCESS -> {
                                binding.root.snacked(it.message ?: "Profile Updated")
                                loadingDialog.dismiss()
                            }

                            ApiStatus.ERROR -> {
                                disconnect(it)
                            }

                            else -> loadingDialog.setResponse("Else")
                        }
                    }
                }
            }
        }
    }

    private fun getUser() {
        viewModel.getProfile()
    }

//    private fun validateForm() {
//        val name = binding.editName.textOf()
//        val school = binding.editSchool.textOf()
//
//        if (name.isEmpty()) {
//            binding.root.snacked("Name Can't Be Empty.")
//            return
//        }
//
//        if (school.isEmpty()) {
//            binding.root.snacked("Telephone Number Can't Be Empty.")
//            return
//        }
//
//        viewModel.updateUser(name, school)
//
//    }

}