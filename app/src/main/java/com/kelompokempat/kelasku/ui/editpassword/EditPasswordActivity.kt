package com.kelompokempat.kelasku.ui.editpassword

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.data.Session
import com.kelompokempat.kelasku.databinding.EditPasswordActivityBinding
import com.kelompokempat.kelasku.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditPasswordActivity : BaseActivity<EditPasswordActivityBinding, EditPasswordViewModel>(R.layout.edit_password_activity) {

    @Inject
    lateinit var session : Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getUser()

        binding.editPasswordBack.setOnClickListener {
            finish()
        }

        binding.saveButton.setOnClickListener {
            validateForm()
        }

    }

    private fun validateForm() {
        val oldPassword = binding.editPasswordOldPassword.textOf()
        val newPassword = binding.editPasswordNewPassword.textOf()
        val passwordConfirmation = binding.editPasswordConfirmPassword.textOf()

        if (oldPassword.isEmpty()) {
            binding.root.snacked("Please Insert Your Old Password")
            return
        }

        else if (newPassword.isEmpty()) {
            binding.root.snacked("Please Insert Your New Password")
            return
        }

        else if (passwordConfirmation.isEmpty()) {
            binding.root.snacked("Please Confirm Your New Password")
            return
        }

        else if (passwordConfirmation != newPassword) {
            binding.root.snacked("Password and Confirm Password Must Match!")
            return
        } else {

            viewModel.updatePassword(oldPassword, newPassword, passwordConfirmation)

        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Updating Password")
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                binding.root.snacked("Update Success")
                            }
                            ApiStatus.ERROR -> {
                                loadingDialog.dismiss()
                                binding.root.snacked("Failed to update password")
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
            }
        }

    }

    private fun getUser() {
        viewModel.getProfile()
    }

}