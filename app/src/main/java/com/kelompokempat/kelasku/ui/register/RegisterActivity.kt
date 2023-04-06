package com.kelompokempat.kelasku.ui.register

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.extension.openActivity
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.data.Schools
import com.kelompokempat.kelasku.databinding.ActivityRegisterBinding
import com.kelompokempat.kelasku.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register) {

    private val listSchools = ArrayList<Schools>()
    private var idSchool = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.registerToLogin.setOnClickListener {
            openActivity<LoginActivity>()
        }

        observe()
        getSchools()

        val schoolsName = listSchools.map { it.school_name }
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