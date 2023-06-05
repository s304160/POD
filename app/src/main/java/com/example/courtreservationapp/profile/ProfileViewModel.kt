package com.example.courtreservationapp.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.courtreservationapp.data.Sport
import com.example.courtreservationapp.data.User
import com.example.courtreservationapp.data.Utils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.util.Util
import com.google.firebase.ktx.Firebase
import es.dmoral.toasty.Toasty
import java.io.FileDescriptor
import java.io.IOException

class ProfileViewModel : ViewModel() {
    private var currentUser: User? = null
    private var profileImageUri: Uri? = null
    private var db = Firebase.firestore
    var showDialog by mutableStateOf(false)
    var selectedImage = MutableLiveData<Uri?>(null)
    private lateinit var getReservationsForUserFromDatabase : (String)->Unit

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var contentResolver: ContentResolver
    lateinit var sports: LiveData<List<Sport>>
    fun initialize(activity: ComponentActivity, currentUser: User?,getReservationsForUserFromDatabase:(String)->Unit) {
        this.currentUser = currentUser
        val user = currentUser
        val uri = Uri.parse(user?.imageUri)
        var path = uri.path
        this.getReservationsForUserFromDatabase = getReservationsForUserFromDatabase

        if (path != null && path != "") {
            path = path.substring(path.indexOf("external/")).split('/').subList(0, 4)
                .joinToString("/") { it }
            val builder = uri.buildUpon()
            builder.path(path)
            builder.authority("media")
            selectedImage.value = builder.build()
        }

        contentResolver = activity.contentResolver
        resultLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data: Intent? = result.data
            var imageUri: Uri? = profileImageUri

            if (result.resultCode != Activity.RESULT_OK || data == null) {
                return@registerForActivityResult
            } else if (imageUri == null) {
                imageUri = data.data
                user?.imageUri = imageUri.toString()
            }
            selectedImage.value = imageUri
            profileImageUri = null

        }
    }

    fun setListSport(listSport: MutableLiveData<List<Sport>>) {
        sports = listSport
    }

    fun showImageSelectionDialog() {
        showDialog = true
    }

    fun getDatabaseFirebase(): FirebaseFirestore {
        return db
    }

    fun dismissImageSelectionDialog() {
        showDialog = false
    }

    fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "ProfileImage")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Camera")

        profileImageUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, profileImageUri)
        resultLauncher.launch(cameraIntent)
    }

    fun openGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        resultLauncher.launch(galleryIntent)
    }

    fun toBitmap(imageUri: Uri?): Bitmap? {
        if (imageUri == null) return null
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(imageUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    @SuppressLint("RestrictedApi")
    fun saveUser(context: Context, user: User) {
        user.imageUri = selectedImage.value?.toString() ?: ""
        Utils().saveLocalStorageUser(context, user)
        if (!user.isDefaultUser() && !user.isDeafultNickName()) {
            var idUser = if (user.getIDUser() != null) user.getIDUser().orEmpty() else Util.autoId()
            db.collection("Users").document(idUser)
                .set(user.toDatabase(toBitmap(selectedImage.value)))
                .addOnSuccessListener {
                    getReservationsForUserFromDatabase(idUser)
                    Toasty.success(context, "User Updated !!!", Toast.LENGTH_SHORT, true).show()
                    Utils().setIDLocalStorageUser(context, idUser)
                    currentUser?.setIDUser(idUser)
                }
                .addOnFailureListener { exception ->
                    Log.d("User Firestore", "Error Update User, error : " + exception)
                    Toasty.error(context, "Error User Updating ...", Toast.LENGTH_SHORT, true)
                        .show()
                }
        }
    }

    fun setUser(user: User) {
        this.currentUser = user
    }
}