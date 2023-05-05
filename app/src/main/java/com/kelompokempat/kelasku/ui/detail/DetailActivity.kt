package com.kelompokempat.kelasku.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.data.Const
import com.kelompokempat.kelasku.data.Session
import com.kelompokempat.kelasku.databinding.ActivityDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : BaseActivity<ActivityDetailBinding, DetailViewModel>(R.layout.activity_detail) {

    @Inject
    lateinit var session : Session
    private var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        getFriendsData()

        binding.detailBack.setOnClickListener {
            finish()
        }

        binding.buttonAlert.setOnClickListener {
            colek()
        }

        binding.buttonWhatsapp.setOnClickListener {
            whatsApp(phoneNumber)
        }

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.friends.collect{ friends ->
                        binding.data = friends
                        phoneNumber = friends.phone
                    }
                }
            }
        }
    }

    private fun getFriendsData() {
        val id = intent.getStringExtra(Const.FRIENDS.FRIENDS_ID)
        viewModel.getFriendsDetail(id)

    }

    private fun colek() {
        val id = intent.getStringExtra(Const.FRIENDS.FRIENDS_ID)
        viewModel.colek(id)
        Timber.tag("notified").d(id.toString())
    }

    private fun whatsApp(number: String?) {
        val phoneNumber = "+62 + $number"
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

}