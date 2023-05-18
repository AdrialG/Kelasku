package com.kelompokempat.kelasku.ui.editcredentials

import android.app.AlertDialog
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
import com.kelompokempat.kelasku.databinding.ActivityEditCredentialsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditCredentialsActivity : BaseActivity<ActivityEditCredentialsBinding, EditCredentialsViewModel>(R.layout.activity_edit_credentials){

    @Inject
    lateinit var session : Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        getUser()

        binding.editCredentialsBack.setOnClickListener {
            finish()
        }

        binding.saveButton.setOnClickListener {
            validateForm()
        }

    }

    private fun validateForm() {
        val phone = binding.editNumber.textOf()
        val email = binding.editEmail.textOf()

        if (phone.isEmpty()) {
            binding.root.snacked("Please Insert Your Old Password")
            return
        }

        if (email.isEmpty()) {
            binding.root.snacked("Please Insert Your New Password")
            return
        }

        val isValid = isValidEmail(email)
        if (!isValid) {
            binding.root.snacked("Please input a valid Email address")
            return
        }

        if (phone == binding.data?.phone &&
            email == binding.data?.email) {
            binding.root.snacked("Nothing has changed.")
            return
        }

        val alert: AlertDialog.Builder = AlertDialog.Builder(this@EditCredentialsActivity)
        alert.setMessage("Do you wish to save these changes?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.updateProfileCredentials(phone, email)
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }

        alert.show()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> {
                                binding.root.snacked(getString(R.string.hang_tight))
                            }
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                binding.root.snacked("Update Success")

                                val coroutineScope = CoroutineScope(Dispatchers.Main)
                                coroutineScope.launch {
                                    delay(2000)
                                    // Code to be executed after the delay
                                    finish()
                                }
                            }
                            ApiStatus.ERROR -> {
                                loadingDialog.dismiss()
                                binding.root.snacked("Something Went Wrong")
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
            }
        }

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.SUCCESS -> {
                                val user = session.getUser()
                                binding.data = user

                            }
                            else -> {

                            }
                        }
                    }
                }
            }
        }
    }

    private fun getUser() {
        viewModel.getProfile()
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }
}