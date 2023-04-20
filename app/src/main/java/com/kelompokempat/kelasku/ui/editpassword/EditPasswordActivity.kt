package com.kelompokempat.kelasku.ui.editpassword

import android.os.Bundle
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.data.Session
import com.kelompokempat.kelasku.databinding.EditPasswordActivityBinding
import com.kelompokempat.kelasku.databinding.EditProfileActivityBinding
import com.kelompokempat.kelasku.ui.editprofile.EditProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
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

    }

    private fun getUser() {
        viewModel.getProfile()
    }

}