package com.kelompokempat.kelasku.ui.register

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.*
import com.crocodic.core.helper.log.Log
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.data.Schools
import com.kelompokempat.kelasku.databinding.ActivityRegisterBinding
import com.kelompokempat.kelasku.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@SuppressLint("InflateParams")
@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register) {

    private val listSchools = ArrayList<Schools>()
    private var schoolId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fullNumber = binding.ccp.registerCarrierNumberEditText(binding.registerInputPhone)


        binding.textKelasku.setOnClickListener {
            tos("$fullNumber")
        }

        binding.registerInputEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                popupEmail()
            }
        }

        binding.registerInputPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                popupPassword()
            }
        }

        binding.registerButton.setOnClickListener {

            if (listOf(
                    binding.registerInputName,
                    binding.registerInputEmail,
                    binding.registerInputPhone,
                    binding.registerInputPassword,
                    binding.registerInputConfirmPassword)
                    .isEmptyRequired(R.string.fill_please)){
                return@setOnClickListener
            }

            val name = binding.registerInputName.textOf()
            val email = binding.registerInputEmail.textOf()
            val phone = binding.registerInputPhone.textOf()
            val schools = schoolId
            val password = binding.registerInputPassword.textOf()
            val confirmPassword = binding.registerInputPassword.textOf()

            val isValid = isValidEmail(email)
            if (isValid) {
                Log.d("Email Validation Success")
            } else {
                binding.root.snacked("Please input a valid Email address")
                return@setOnClickListener
            }

            if (password.length < 8) {
                binding.root.snacked("Password can't be less than 8 characters")
                return@setOnClickListener
            }

            viewModel.register( name, email, phone, password, confirmPassword, schools)

        }

        binding.registerToLogin.setOnClickListener {
            finish()
        }

        observe()
        getSchools()
        autocompleteSpinner()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Signing In…")
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                openActivity<LoginActivity>()
                                finish()
                            }
                            ApiStatus.ERROR -> {
                                loadingDialog.dismiss()
                                binding.root.snacked("Register Failed")
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
            }
        }

    }

    private fun getSchools() {
        viewModel.getSchools()
    }

    private fun autocompleteSpinner(){

        val autoCompleteSpinner = findViewById<AutoCompleteTextView>(R.id.register_input_school)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listSchools)
        autoCompleteSpinner.setAdapter(adapter)

        // Show the dropdown list when the AutoCompleteTextView is clicked
        autoCompleteSpinner.setOnClickListener {
            autoCompleteSpinner.showDropDown()
            autoCompleteSpinner.dropDownVerticalOffset = -autoCompleteSpinner.height
        }

        autoCompleteSpinner.setOnItemClickListener { _, _, position, _ ->
            // Handle item selection here
            val selectedItem = listSchools[position]
            schoolId = selectedItem.id.toString()
        }

    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    private fun popupEmail() {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popout_email, null)
        val popupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.elevation = 10f
        popupWindow.showAtLocation(binding.registerInputPassword, Gravity.TOP, 0, 0)
    }

    private fun popupPassword() {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popout_password, null)
        val popupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.elevation = 10f
        popupWindow.showAtLocation(binding.registerInputPassword, Gravity.TOP, 0, 0)
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.schools.collect {
                        listSchools.addAll(it)
                    }
                }
            }
        }
    }

}