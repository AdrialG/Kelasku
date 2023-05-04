package com.kelompokempat.kelasku.ui.login

import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.data.Const
import com.kelompokempat.kelasku.data.Session
import com.kelompokempat.kelasku.databinding.ActivityLoginBinding
import com.kelompokempat.kelasku.ui.home.HomeActivity
import com.kelompokempat.kelasku.ui.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    @Inject
    lateinit var session : Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                // Show dialog or handle back press here
                val builder = AlertDialog.Builder(this@LoginActivity)
                builder.setMessage("Are you sure you want to exit?")
                builder.setPositiveButton("Yes") { _, _ ->
                    finish()
                }
                builder.setNegativeButton("No", null)
                val dialog = builder.create()
                dialog.show()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

        binding.loginButton.setOnClickListener {
            val emailOrPhone = binding.loginInputEmailPn.textOf()
            val password = binding.loginInputPassword.textOf()
            viewModel.login(emailOrPhone, password)
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.w(task.exception, "Fetching FCM registration token failed")
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            Timber.tag(ContentValues.TAG).d(msg)
            session.setValue(Const.TOKEN.DEVICETOKEN, token)
        })

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
                                binding.root.snacked("Login Failed")
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