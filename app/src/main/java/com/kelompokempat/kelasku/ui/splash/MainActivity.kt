package com.kelompokempat.kelasku.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.crocodic.core.base.activity.NoViewModelActivity
import com.crocodic.core.extension.openActivity
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.data.Session
import com.kelompokempat.kelasku.databinding.ActivityMainBinding
import com.kelompokempat.kelasku.ui.home.HomeActivity
import com.kelompokempat.kelasku.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : NoViewModelActivity<ActivityMainBinding>(R.layout.activity_main) {

    @Inject
    lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Handler Looper Splash
        Handler(Looper.getMainLooper()).postDelayed({
            val isUser = session.getUser()
            if (isUser == null){
                openActivity<LoginActivity>()
                finish()
            }else{
                openActivity<HomeActivity>()
                finish()

            }
        },3000)

    }
}
