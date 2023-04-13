package com.kelompokempat.kelasku.ui.register

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.*
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.data.Schools
import com.kelompokempat.kelasku.databinding.ActivityRegisterBinding
import com.kelompokempat.kelasku.ui.home.HomeActivity
import com.kelompokempat.kelasku.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register) {

    private val listSchools = ArrayList<Schools>()
    private var idSchool = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.registerButton.setOnClickListener {
            if (binding.registerInputName.isEmptyRequired(R.string.fill_please) ||
                binding.registerInputEmail.isEmptyRequired(R.string.fill_please) ||
                binding.registerInputPhone.isEmptyRequired(R.string.fill_please) ||
//                binding.registerInputSchool.isEmptyRequired(R.string.fill_please) ||
                binding.registerInputPassword.isEmptyRequired(R.string.fill_please) ||
                binding.registerInputConfirmPassword.isEmptyRequired(R.string.fill_please)){
                return@setOnClickListener
            }

            val name = binding.registerInputName.textOf()
            val email = binding.registerInputEmail.textOf()
            val phone = binding.registerInputPhone.textOf()
            val schools = binding.registerInputSchool
            val password = binding.registerInputPassword.textOf()
            val confirmPassword = binding.registerInputPassword.textOf()

//            viewModel.register( name, email,  phone, password, confirmPassword, schools)

        }

        binding.registerToLogin.setOnClickListener {
            finish()
        }

        observe()
        getSchools()

        val schoolsName = listSchools/*.map { it.school_name }*/
        val adapterSchools = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, schoolsName)
        binding.registerInputSchool.adapter = adapterSchools

        binding.registerInputSchool.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val dataSelected = listSchools[position]
                idSchool = dataSelected.id.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Hang Tight...")
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