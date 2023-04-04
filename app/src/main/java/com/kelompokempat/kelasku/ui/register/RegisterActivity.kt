package com.kelompokempat.kelasku.ui.register

import android.os.Bundle
import com.crocodic.core.extension.openActivity
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.databinding.ActivityRegisterBinding
import com.kelompokempat.kelasku.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.registerToLogin.setOnClickListener {
            openActivity<LoginActivity>()
        }

    }
}