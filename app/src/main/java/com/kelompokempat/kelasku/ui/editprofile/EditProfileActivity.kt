package com.kelompokempat.kelasku.ui.editprofile

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.data.Schools
import com.kelompokempat.kelasku.data.Session
import com.kelompokempat.kelasku.databinding.EditProfileActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileActivity : BaseActivity<EditProfileActivityBinding, EditProfileViewModel>(R.layout.edit_profile_activity) {

    @Inject
    lateinit var session : Session

    private var profilePictureUri: Uri? = null
    private var profileBannerUri: Uri? = null

    private val pickProfilePictureRequest = 1
    private val pickProfileBannerRequest = 2

    private val listSchools = ArrayList<Schools>()
    private var schoolId: String = "0"
    private var dataUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                val intent = Intent()
                intent.putExtra("data_updated", dataUpdated)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

        observe()
        getSchools()
        getUser()
        autocompleteSpinner()

        val user = session.getUser()
        if (user != null) {
            binding.data = user
            schoolId = user.school?.id.toString()
        }

        binding.editProfileBack.setOnClickListener {
            val intent = Intent()
            intent.putExtra("data_updated", dataUpdated)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        binding.profilePictureEdit.setOnClickListener {
            openGallery(1)
        }

        binding.bannerEdit.setOnClickListener {
            openGallery(2)
        }

        binding.saveButton.setOnClickListener {
            validateForm()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Updating Profile...")
                            ApiStatus.SUCCESS -> {
                                binding.root.snacked(it.message ?: "Profile Updated")
                                loadingDialog.dismiss()
                                dataUpdated = true
                            }

                            ApiStatus.ERROR -> {
                                disconnect(it)
                            }

                            else -> loadingDialog.setResponse("Else")
                        }
                    }
                }
            }
        }

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.schools.collect {
                        listSchools.addAll(it)

                        val autoCompleteSpinnerEdit = binding.editInputSchool
                        val adapter = ArrayAdapter(this@EditProfileActivity, android.R.layout.simple_dropdown_item_1line, listSchools)
                        autoCompleteSpinnerEdit.setAdapter(adapter)
                    }
                }
            }
        }
    }

    private fun getSchools() {
        viewModel.getSchools()
    }

    private fun getUser() {
        viewModel.getProfile()
    }

    private fun autocompleteSpinner(){

        val autoCompleteSpinnerEdit = binding.editInputSchool
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listSchools)
        autoCompleteSpinnerEdit.setAdapter(adapter)

        // Show the dropdown list when the AutoCompleteTextView is clicked
        autoCompleteSpinnerEdit.setOnClickListener {
            autoCompleteSpinnerEdit.showDropDown()
            autoCompleteSpinnerEdit.dropDownVerticalOffset = -autoCompleteSpinnerEdit.height

       }

        autoCompleteSpinnerEdit.setOnItemClickListener { _, _, position, _ ->
            // Handle item selection here
            val selectedItem = listSchools[position]
            schoolId = selectedItem.id.toString()

        }

    }

    private fun validateForm() {
        val name = binding.editName.textOf()
        val school = schoolId

        // Condition 1: If all fields are empty
        if (name.isEmpty() &&
            school.isEmpty() &&
            profilePictureUri == null &&
            profileBannerUri == null
        ) {
            binding.root.snacked("Everything needs to be filled.")
            return
        }

        // Condition 2: If nothing has changed
        if (name == binding.data?.name &&
            school == binding.data?.school?.school_name &&
            profilePictureUri == null &&
            profileBannerUri == null
        ) {
            binding.root.snacked("Nothing has changed.")
            return
        }

        val compressedFilePicture = profilePictureUri?.let { getFileFromUri(it) }
        val compressedFileBanner = profileBannerUri?.let { getFileFromUri(it) }

        lifecycleScope.launch {
            if (compressedFilePicture != null && compressedFileBanner != null) {
                viewModel.updateProfileAll(name, school, compressedFilePicture, compressedFileBanner)
            } else if (compressedFileBanner != null) {
                viewModel.updateProfileBanner(name, school, compressedFileBanner)
            } else if (compressedFilePicture != null) {
                viewModel.updateProfilePicture(name, school, compressedFilePicture)
            } else {
                viewModel.updateProfile(name, school)
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File {
        val filePath = getRealPathFromURI(uri)
        return File(filePath.toString())
    }

    private fun openGallery(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, requestCode)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == pickProfilePictureRequest) {
                profilePictureUri = data?.data
                binding.profilePicture.setImageURI(profilePictureUri)
            } else if (requestCode == pickProfileBannerRequest) {
                profileBannerUri = data?.data
                binding.profileBanner.setImageURI(profileBannerUri)
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use { innerCursor ->
            if (innerCursor.moveToFirst()) {
                val columnIndex = innerCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                if (columnIndex >= 0) {
                    return innerCursor.getString(columnIndex)
                }
            }
        }
        cursor?.close()
        return null
    }

}