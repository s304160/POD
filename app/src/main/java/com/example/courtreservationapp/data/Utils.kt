package com.example.courtreservationapp.data

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.content.edit
import com.google.firebase.firestore.FirebaseFirestore
import java.io.FileDescriptor
import java.io.IOException

class Utils {
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var user: User

    fun recoverLocalStorageUser(contextParent: Context): User {
        sharedPrefs = contextParent.getSharedPreferences("user_saved", Context.MODE_PRIVATE)
        val nickName = sharedPrefs.getString("nickName", "Nickname").toString()
        val fullName = sharedPrefs.getString("fullName", "Full Name").toString()
        val age = sharedPrefs.getInt("age", 25)
        val objective = sharedPrefs.getString("objective", "Objective").toString()
        val skillLevel = sharedPrefs.getString("skillLevel", "Skill Level").toString()
        val sport = sharedPrefs.getString("sport", "Soccer").toString()
        val achievements = sharedPrefs.getString("achievements", "Top10World").toString()
        val description = sharedPrefs.getString("description", "Description of me").toString()
        val imageUri = sharedPrefs.getString("imageUri", "").toString()

        user = User(
            fullName,
            nickName,
            age,
            objective,
            skillLevel,
            description,
            sport,
            achievements,
            imageUri
        )

        user.setIDUser(idValue = sharedPrefs.getString("idUser","").toString())

        return user
    }

    fun setIDLocalStorageUser(contextParent: Context, idValue:String){
        sharedPrefs = contextParent.getSharedPreferences("user_saved", Context.MODE_PRIVATE)
        if(idValue!=null && idValue.isNotEmpty())
            sharedPrefs.edit { putString("idUser",idValue)}
    }

    fun getIDLocalStorageUser(contextParent: Context):String{
        sharedPrefs = contextParent.getSharedPreferences("user_saved", Context.MODE_PRIVATE)

        return sharedPrefs.getString("idUser","").toString()
    }

    /*fun processResponse(response: ActivityResult) {
        if (response.resultCode == AppCompatActivity.RESULT_OK) {
            val data: Intent? = response.data // typically extras store relevant info
            val updatedUser = data?.getSerializableExtra("user") as User
            user = updatedUser
            saveUser(user)
        }
    }*/
    fun saveLocalStorageUser(contextParent: Context, user: User) {
        sharedPrefs = contextParent.getSharedPreferences("user_saved", Context.MODE_PRIVATE)

        with(sharedPrefs.edit()) {
            putString("fullName", user.fullName)
            putString("nickName", user.nickName)
            putInt("age", user.age)
            putString("objective", user.objective)
            putString("skillLevel", user.skillLevel)
            putString("description", user.description)
            putString("achievements", user.achievements)
            putString("sport", user.sport)
            putString("imageUri", user.imageUri)
            putString("idUser",user.getIDUser())
            apply()
        }
    }
}