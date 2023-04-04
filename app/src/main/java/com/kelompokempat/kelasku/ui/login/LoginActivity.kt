package com.kelompokempat.kelasku.ui.login

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.databinding.ActivityLoginBinding
import com.kelompokempat.kelasku.ui.home.HomeActivity
import com.kelompokempat.kelasku.ui.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.loginButton.setOnClickListener {
            val email_or_phone = binding.loginInputEmailPn.textOf()
            val password = binding.loginInputPassword.textOf()
            viewModel.login(email_or_phone, password)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show(getString(R.string.logging_in))
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                openActivity<HomeActivity>()
                                finish()
                            }

                            ApiStatus.ERROR -> {
                                loadingDialog.dismiss()
                                tos(it.message ?: "Login Failed")
                            }
                            else -> loadingDialog.setResponse("This is the message of all time")
                        }
                    }
                }
            }
        }


        binding.loginToRegister.setOnClickListener {
            openActivity<RegisterActivity>()
        }

    }
}