package com.kelompokempat.kelasku.ui.editprofile

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import com.crocodic.core.helper.DateTimeHelper
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.data.Schools
import com.kelompokempat.kelasku.data.Session
import com.kelompokempat.kelasku.databinding.EditProfileActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileActivity : BaseActivity<EditProfileActivityBinding, EditProfileViewModel>(R.layout.edit_profile_activity) {

    @Inject
    lateinit var session : Session

    private var username: String? = null
    private var filePhotoPicture: File? = null
    private var filePhotoBanner: File? = null

    private val listSchools = ArrayList<Schools>()
    private var schoolId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        getSchools()
        getUser()
        autocompleteSpinner()

        val user = session.getUser()
        if (user != null) {
            binding.data = user
        }

        binding.editProfileBack.setOnClickListener {
            finish()
        }


        binding.profilePictureEdit.setOnClickListener {
//            if (checkPermissionGallery()) {
            openGallery()
//            } else {
//                requestPermissionGallery()
//            }
        }

        binding.bannerEdit.setOnClickListener {
//            if (checkPermissionGallery()) {
                openGalleryBanner()
//            } else {
//                requestPermissionGallery()
//            }
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
        val school = binding.editInputSchool.textOf()

        if (name.isEmpty()) {
            binding.root.snacked("Name Can't Be Empty.")
            return
        }

        if (school.isEmpty()) {
            binding.root.snacked("School Can't Be Empty.")
            return
        }

        if (filePhotoPicture == null && filePhotoBanner == null) {
            if (name == username) {
                return
            }
            viewModel.updateProfile(name, school)

        }

        if (filePhotoBanner == null) {
            lifecycleScope.launch {
                val compressedFile = compressFile(filePhotoPicture!!)
                Timber.d("File: $compressedFile")
                if (compressedFile != null) {
                    viewModel.updateProfilePicture(name, school, compressedFile)
                }
            }

            if (name == username) {
                return
            }

        }

        if (filePhotoPicture == null) {
            lifecycleScope.launch {
                val compressedFile = compressFile(filePhotoBanner!!)
                Timber.d("File: $compressedFile")
                if (compressedFile != null) {
                    viewModel.updateProfileBanner(name, school, compressedFile)
                }
            }

            if (name == username) {
                return
            }

        }

        else {
            lifecycleScope.launch {
                val compressedFilePicture = compressFile(filePhotoPicture!!)
                val compressedFileBanner = compressFile(filePhotoBanner!!)
                Timber.d("Picture File: $compressedFilePicture")
                Timber.d("Banner File: $compressedFileBanner")
                if (compressedFilePicture != null && compressedFileBanner != null) {
                    viewModel.updateProfileAll(name, school, compressedFilePicture, compressedFileBanner)
                }
            }
        }
    }

    //MultiPart Gallery Profile Picture
    private var activityLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let {
                generateFileImage(it)
            }
        }

    //MultiPart Gallery Banner
    private var activityLauncherGalleryBanner =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let {
                generateFileBanner(it)
            }
        }

    //cek Permission Gallery
    private fun checkPermissionGallery(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    // function Open Gallery for Profile Picture
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityLauncherGallery.launch(galleryIntent)
    }

    // function Open Gallery for Banner
    private fun openGalleryBanner() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityLauncherGalleryBanner.launch(galleryIntent)
    }

    // Request Permission Gallery
    private fun requestPermissionGallery() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            110
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 200) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                openGallery()
            } else {
                Toast.makeText(this, "Gallery Access Denied", Toast.LENGTH_SHORT).show()
            }
        }

    }

    // Generate Image File
    private fun generateFileImage(uri: Uri) {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()

            val orientation = getOrientation2(uri)
            val file = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                createImageFile()
            } else {
                File(externalCacheDir?.absolutePath, getNewFileName())
            }

            val fos = FileOutputStream(file)
            var bitmap = image

            if (orientation != -1 && orientation != 0) {

                val matrix = Matrix()
                when (orientation) {
                    6 -> matrix.postRotate(90f)
                    3 -> matrix.postRotate(180f)
                    8 -> matrix.postRotate(270f)
                    else -> matrix.postRotate(orientation.toFloat())
                }
                bitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            binding.profilePicture.setImageBitmap(bitmap)
            filePhotoPicture = file
            Timber.tag("checkfile").d("file : %s", filePhotoPicture)
        } catch (e: Exception) {
            e.printStackTrace()
            binding.root.snacked("This File Can't Be Used")
        }
    }

    private fun generateFileBanner(uri: Uri) {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()

            val orientation = getOrientation2(uri)
            val file = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                createImageFile()
            } else {
                File(externalCacheDir?.absolutePath, getNewFileName())
            }

            val fos = FileOutputStream(file)
            var bitmap = image

            if (orientation != -1 && orientation != 0) {

                val matrix = Matrix()
                when (orientation) {
                    6 -> matrix.postRotate(90f)
                    3 -> matrix.postRotate(180f)
                    8 -> matrix.postRotate(270f)
                    else -> matrix.postRotate(orientation.toFloat())
                }
                bitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            binding.profileBanner.setImageBitmap(bitmap)
            filePhotoBanner = file
            Timber.tag("checkfile").d("file : %s", filePhotoPicture)
        } catch (e: Exception) {
            e.printStackTrace()
            binding.root.snacked("This File Can't Be Used")
        }
    }

    @SuppressLint("Range")
    private fun getOrientation(shareUri: Uri): Int {
        val orientationColumn = arrayOf(MediaStore.Images.Media.ORIENTATION)
        val cur = contentResolver.query(
            shareUri,
            orientationColumn,
            null,
            null,
            null
        )
        var orientation = -1
        if (cur != null && cur.moveToFirst()) {
            if (cur.columnCount > 0) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
            }
            cur.close()
        }
        return orientation
    }

    @SuppressLint("NewApi")
    private fun getOrientation2(shareUri: Uri): Int {
        val inputStream = contentResolver.openInputStream(shareUri)
        return getOrientation3(inputStream)
    }

    @SuppressLint("NewApi")
    private fun getOrientation3(inputStream: InputStream?): Int {
        val exif: ExifInterface
        var orientation = -1
        inputStream?.let {
            try {
                exif = ExifInterface(it)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return orientation
    }

    //function Create Image
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = DateTimeHelper().createAtLong().toString()
        val storageDir =
            getAppSpecificAlbumStorageDir(Environment.DIRECTORY_DOCUMENTS, "Attachment")
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    //Get Image File Name
    private fun getNewFileName(isPdf: Boolean = false): String {
        val timeStamp = DateTimeHelper().createAtLong().toString()
        return if (isPdf) "PDF_${timeStamp}_.pdf" else "JPEG_${timeStamp}_.jpg"
    }

    //Get Image Album Storage
    private fun getAppSpecificAlbumStorageDir(albumName: String, subAlbumName: String): File {
        // Get the pictures directory that's inside the app-specific directory on
        // external storage.
        val file = File(getExternalFilesDir(albumName), subAlbumName)
        if (!file.mkdirs()) {
        }
        return file
    }

    private suspend fun compressFile(filePhoto: File): File? {
        println("Compress 1")
        return try {
            println("Compress 2")
            Compressor.compress(this, filePhoto) {
                resolution(720, 720)
                quality(50)
                format(Bitmap.CompressFormat.JPEG)
                size(514)
            }
        } catch (e: Exception) {
            println("Compress 3")
            tos("Compression Failed")
            e.printStackTrace()
            null
        }

    }

}