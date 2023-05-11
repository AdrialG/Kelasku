package com.kelompokempat.kelasku.ui.detail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.snacked
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
    private var name: String? = null
    private var likeByYou: Boolean? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

        observe()
        getFriendsData()

        binding.detailBack.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        binding.buttonAlert.setOnClickListener {
            colek()
        }

        binding.likeCounterButton.setOnClickListener {
            like()
            binding.likeCounterButton.postDelayed({
                // Delayed code to be executed
                recreate()
            }, 1000) // Delay time in milliseconds
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
                        name = friends.name
                        likeByYou = friends.likeByYou
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
        observeColek()
        val id = intent.getStringExtra(Const.FRIENDS.FRIENDS_ID)
        viewModel.colek(id)
        Timber.tag("notified").d(id.toString())
    }

    private fun observeColek() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        if (it.status == ApiStatus.SUCCESS) {
                            binding.root.snacked("$name Notified")
                        }
                        else if (it.status == ApiStatus.ERROR) {
                            binding.root.snacked("You can only notify 10 times per day")
                        }
                    }
                }
            }
        }
    }

    private fun like() {
        val id = intent.getStringExtra(Const.FRIENDS.FRIENDS_ID)
        viewModel.like(id)
        Timber.tag("liked").d(id.toString())

        if (likeByYou == true) {
            binding.root.snacked("$name Unliked")
        }
        else if (likeByYou == false) {
            binding.root.snacked("$name Liked")
        }

    }

    private fun whatsApp(number: String?) {
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$number")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

}