package com.kelompokempat.kelasku.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.snacked
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.data.Const
import com.kelompokempat.kelasku.data.FriendsDetail
import com.kelompokempat.kelasku.data.FriendsList
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

    private val friendsDetail : FriendsDetail? = null

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
            whatsApp(friendsDetail?.phone)
        }

        friendsDetail?.phone?.let { Log.d("friend id", it) }

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.friends.collect{ friends ->
                        binding.data = friends
                    }
                }
            }
        }
    }

    private fun getFriendsData() {
        val id = intent.getStringExtra(Const.FRIENDS.FRIENDS_ID)
        viewModel.getFriendsDetail(id)

        Log.d("friend id", id.toString())
    }

    private fun colek() {
        val id = intent.getStringExtra(Const.FRIENDS.FRIENDS_ID)
        viewModel.colek(id)
        Timber.tag("notified").d(id.toString())
    }

    private fun whatsApp(number: String?) {
        val intentUri = Uri.parse("https://api.whatsapp.com/send?phone="+number)
        val whatsappIntent = Intent(Intent.ACTION_VIEW)
        whatsappIntent.setData(intentUri)
        startActivity(whatsappIntent)
    }

}