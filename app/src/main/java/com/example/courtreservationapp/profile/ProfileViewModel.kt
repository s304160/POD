package com.example.courtreservationapp.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.courtreservationapp.R
import com.example.courtreservationapp.data.Skill
import com.example.courtreservationapp.data.Sport
import com.example.courtreservationapp.data.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.toObject
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

@Suppress("UNCHECKED_CAST")
class ProfileViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val image = MutableLiveData<Bitmap>()
    val name = MutableLiveData<String>()
    val nick = MutableLiveData<String>()
    val age = MutableLiveData<Int>()
    val sport = MutableLiveData<String>()
    val skill = MutableLiveData<Map<String, String>>()
    val objective = MutableLiveData<String>()
    val achievement = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    private val user = MutableLiveData<User>().apply {
        Firebase.auth.currentUser?.uid?.let{
            db.collection("Users").document(it).addSnapshotListener { result, error ->
                if (error == null && result != null) {
                    value = result.toObject<User>()

                    name.value = value?.fullName
                    nick.value = value?.nickName
                    age.value = value?.age
                    sport.value = value?.sport
                    skill.value = value?.skillLevel
                    objective.value = value?.objective
                    achievement.value = value?.achievements
                    description.value = value?.description
                }
            }
        }
    }
    val sports = MutableLiveData<List<Sport>>().apply {
        db.collection("Sports").addSnapshotListener { result, error ->
            if (error == null && result != null)
                value = result.documents.map {
                    Sport(it.id, it.data?.get("name") as Map<String, String>)
                }
        }
    }
    val skills = MutableLiveData<List<Skill>>().apply {
        db.collection("SkillLevel").addSnapshotListener { result, error ->
            if (error == null && result != null)
                value = result.documents.map {
                    Skill(it.id, it.data?.get("text") as Map<String, String>)
                }
        }
    }

    fun getImage(ctx: Context): LiveData<Bitmap> {
        Firebase.auth.currentUser?.uid?.let {
            val sharedPrefs = ctx.getSharedPreferences("user_saved", Context.MODE_PRIVATE)
            val image = sharedPrefs.getString(it, null)

            this.image.value = image?.let { BitmapFactory.decodeStream(FileInputStream(File(image))) }
        }

        return this.image
    }

    fun setName(value: String) { name.value = value }
    fun setNick(value: String) { nick.value = value }
    fun setAge(value: Int?) { age.value = value ?: 0 }
    fun setSport(value: String) { sport.value = value }
    fun setSkill(value: Map<String, String>) { skill.value = value }
    fun setObjective(value: String) { objective.value = value }
    fun setAchievement(value: String) { achievement.value = value }
    fun setDescription(value: String) { description.value = value }
    fun setImage(image: Bitmap?) { this.image.value = image }

    fun saveUser(ctx: Context, user: User, image: Bitmap?, context: Context): Boolean {
        Firebase.auth.currentUser?.uid?.let {
            db.collection("Users").document(it).set(user)
                .addOnSuccessListener {
                    Toast.makeText(context, context.getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, context.getString(R.string.profile_update_failed), Toast.LENGTH_SHORT).show()
                }

            this.user.value = user

            val directory = File(ctx.cacheDir, "images")
            directory.mkdirs()

            val file =  File(directory, "profileImage${it}.jpg")

            val output = FileOutputStream(file)
            image?.compress(Bitmap.CompressFormat.JPEG, 100, output)
            output.close()

            val sharedPrefs = ctx.getSharedPreferences("user_saved", Context.MODE_PRIVATE)
            sharedPrefs.edit {
                putString(it, file.absolutePath)
                apply()
            }

            return true
        }

        return false
    }

    fun toBitmap(ctx: Context, imageUri: Uri?): Bitmap? {
        if (imageUri == null)
            return null

        try {
            val parcelFileDescriptor = ctx.contentResolver.openFileDescriptor(imageUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor

            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)

            parcelFileDescriptor.close()

            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}