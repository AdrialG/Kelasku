package com.kelompokempat.kelasku.ui.editprofile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.data.Schools
import com.kelompokempat.kelasku.data.Session
import com.kelompokempat.kelasku.databinding.EditProfileActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileActivity : BaseActivity<EditProfileActivityBinding, EditProfileViewModel>(R.layout.edit_profile_activity) {

    @Inject
    lateinit var session : Session

    private val galleryPermissionRequestCode = 100

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
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                requestGalleryPermission(1)
            } else {
              openGallery(1)
            }
        }

        binding.bannerEdit.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                requestGalleryPermission(2)
            } else {
                openGallery(2)
            }
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

    private fun isGalleryPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above, check if the permission is granted using the new media permission
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // For Android 9 and below, check if the permission is granted using the old storage permission
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestGalleryPermission(requestCode: Int) {
        if (isGalleryPermissionGranted()) {
            // Permission already granted, perform gallery-related operations
            if (requestCode == 1) {
                openGallery(1)
            }
            if (requestCode == 2) {
                openGallery(2)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10 and above, request the permission directly
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    galleryPermissionRequestCode
                )
            } else {
                // For Android 9 and below, show a rationale dialogue explaining the need for permission
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder(this)
                        .setTitle("Gallery Permission")
                        .setMessage("This app requires access to your gallery to function properly.")
                        .setPositiveButton("Grant") { _, _ ->
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                galleryPermissionRequestCode
                            )
                        }
                        .setNegativeButton("Deny") { _, _ ->
                            tos("Gallery Permission Denied")
                        }
                        .show()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        galleryPermissionRequestCode
                    )
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
                profilePictureUri?.let { uri ->
                    // Call compressImage from a coroutine
                    lifecycleScope.launch {
                        val compressedImage = compressImage(uri)
                        // Set the compressed image to the ImageView
                        binding.profilePicture.setImageURI(compressedImage)
                    }
                }
            } else if (requestCode == pickProfileBannerRequest) {
                profileBannerUri = data?.data
                profileBannerUri?.let { uri ->
                    // Call compressImage from a coroutine
                    lifecycleScope.launch {
                        val compressedImage = compressImage(uri)
                        // Set the compressed image to the ImageView
                        binding.profileBanner.setImageURI(compressedImage)
                    }
                }
            }
        }
    }

    private suspend fun compressImage(uri: Uri): Uri? = withContext(Dispatchers.IO) {
        val filePath = getRealPathFromURI(uri) // Retrieve the file path from the URI
        val file = filePath?.let { File(it) }
        val compressedFile = file?.let {
            Compressor.compress(this@EditProfileActivity, it) {
                default()
                resolution(720, 720)
                quality(80)
            }
        }
        return@withContext compressedFile?.let { Uri.fromFile(it) }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        var realPath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                realPath = it.getString(columnIndex)
            }
            cursor.close()
        }
        return realPath
    }

}