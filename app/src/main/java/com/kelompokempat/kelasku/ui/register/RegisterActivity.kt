package com.kelompokempat.kelasku.ui.register

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.data.Schools
import com.kelompokempat.kelasku.databinding.ActivityRegisterBinding
import com.kelompokempat.kelasku.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register) {

    private val listSchools = ArrayList<Schools>()
    private var schoolId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.registerButton.setOnClickListener {
            if (binding.registerInputName.isEmptyRequired(R.string.fill_please) ||
                binding.registerInputEmail.isEmptyRequired(R.string.fill_please) ||
                binding.registerInputPhone.isEmptyRequired(R.string.fill_please) ||
                binding.registerInputSchool.isEmptyRequired(R.string.fill_please) ||
                binding.registerInputPassword.isEmptyRequired(R.string.fill_please) ||
                binding.registerInputConfirmPassword.isEmptyRequired(R.string.fill_please)){
                return@setOnClickListener
            }

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
                                openActivity<HomeActivity>()
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
        val options = arrayListOf("Choose School") // Replace with your own options
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listSchools)
        autoCompleteSpinner.setAdapter(adapter)


        // Show the dropdown list when the AutoCompleteTextView is clicked
        autoCompleteSpinner.setOnClickListener {
            autoCompleteSpinner.showDropDown()
            autoCompleteSpinner.setDropDownVerticalOffset(-autoCompleteSpinner.height)

        }

        autoCompleteSpinner.setOnItemClickListener { parent, view, position, id ->
            // Handle item selection here
            val selectedItem = listSchools[position]
            schoolId = selectedItem.id.toString()

        }

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