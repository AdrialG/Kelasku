package com.kelompokempat.kelasku.ui.detail

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.data.FriendsDetail
import com.kelompokempat.kelasku.data.FriendsList
import com.kelompokempat.kelasku.data.Session
import com.kelompokempat.kelasku.databinding.ActivityDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : BaseActivity<ActivityDetailBinding, DetailViewModel>(R.layout.activity_detail) {

    @Inject
    lateinit var session : Session

    private val friendsList : FriendsList? = null
    private val friendsDetail : FriendsDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        observe()
        getFriendsDetail()

        binding.detailBack.setOnClickListener {
            finish()
        }

        val id = intent.getStringExtra(friendsList?.id)

    }

//    private fun observe() {
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                launch {
//                    viewModel.apiResponse.collect {
//                        when (it.status) {
//                            ApiStatus.SUCCESS -> {
//
//
//                            }
//                            else -> {
//
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    private fun getFriendsDetail() {
        viewModel.getFriendsDetail(friendsList?.id)
    }

}