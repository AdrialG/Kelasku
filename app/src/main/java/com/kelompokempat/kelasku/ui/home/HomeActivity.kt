package com.kelompokempat.kelasku.ui.home

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.tos
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.data.FriendsList
import com.kelompokempat.kelasku.data.Session
import com.kelompokempat.kelasku.data.User
import com.kelompokempat.kelasku.databinding.ActivityHomeBinding
import com.kelompokempat.kelasku.databinding.ItemHomeRecyclerBinding
import com.kelompokempat.kelasku.ui.editpassword.EditPasswordActivity
import com.kelompokempat.kelasku.ui.editprofile.EditProfileActivity
import com.kelompokempat.kelasku.ui.login.LoginActivity
import com.kelompokempat.kelasku.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

    @Inject
    lateinit var session : Session

    private var friends = ArrayList<FriendsList?>()
    private var friendsSpecific = ArrayList<FriendsList?>()

    private val navigationView = binding.homeNavigation.getHeaderView(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askNotificationPermission()
        observe()
        getUser()
        getFriends()

        // TODO: Token Confirmation, can't seem to get friendlist & user

        val navPhoto = navigationView.findViewById<CircleImageView>(R.id.navigation_profile_picture)
        val navName = navigationView.findViewById<TextView>(R.id.navigation_name)
        val navEmail = navigationView.findViewById<TextView>(R.id.navigation_email)

        navName.setText()

        binding.homeOpenNav.setOnClickListener {
            this.binding.homeDrawerLayout.openDrawer(GravityCompat.START)
        }

        binding.homeNavigation.setNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.nav_header_back -> {
                    binding.homeDrawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    openActivity<ProfileActivity>()
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_edit_profile -> {
                    openActivity<EditProfileActivity>()
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_edit_password -> {
                    openActivity<EditPasswordActivity>()
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_logout -> {
                    binding.root.snacked("You Clicked Logout")
                    return@setNavigationItemSelectedListener true
                }

                else -> return@setNavigationItemSelectedListener false
            }

        }



        binding.homeSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                friends.clear()
                val friendsSearch = newText!!.lowercase(Locale.getDefault())

                if (friendsSearch.isNotEmpty()) {

                    friendsSpecific.forEach {
                        if (it?.name?.lowercase(Locale.getDefault())!!.contains(friendsSearch)) {
                            friends.add(it)
                        }
                    }

                    binding.homeRecycler.adapter?.notifyDataSetChanged()
                }

                else {
                    friends.clear()
                    friends.addAll(friendsSpecific)
                    binding.homeRecycler.adapter?.notifyDataSetChanged()
                }

                Timber.d("Keyword", "$newText")
                return false
            }
        })

        binding.homeRecycler.adapter = CoreListAdapter<ItemHomeRecyclerBinding, FriendsList>(R.layout.item_home_recycler)
            .initItem(friends) { position, data ->
                openActivity<LoginActivity> {
//                    putExtra(Const.TOUR.TOUR, data)
                }
            }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token 
        // val token = task.result
        })

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.SUCCESS -> {
                                val user = session.getUser()
                                binding.user = user

                            }
                            else -> {

                            }
                        }
                    }

                    viewModel.friends.collect {
                        friendsSpecific.clear()
                        friends.clear()

                        friendsSpecific.addAll(it)
                        friends.addAll(it)
                        binding.homeRecycler.adapter?.notifyDataSetChanged()
                    }

                }
            }
        }
    }

    private fun getUser() {
        viewModel.getProfile()
    }

    private fun getFriends() {
        viewModel.getFriends()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
            tos("Permission Granted")
        } else {
            // TODO: Inform user that that your app will not show notifications.
            tos("Permission Denied")
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            }
            else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            }
            else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

}