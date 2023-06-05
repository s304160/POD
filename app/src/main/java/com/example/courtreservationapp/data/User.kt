package com.example.courtreservationapp.data

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.core.net.toUri
import com.example.courtreservationapp.R
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.IOException
import java.util.Objects
private lateinit var contentResolver: ContentResolver
data class User(
    var fullName: String = "Full Name",
    var nickName: String = "Nickname",
    var age: Int = 0,
    var objective: String = "Objective",
    var skillLevel: String = "Beginner",
    var description: String = "Description",
    var sport: String = "Soccer",
    var achievements: String = "Top10World",
    var imageUri: String = "",
    var skillLevelIndex: Int = 0
) : java.io.Serializable {

    private var idUser:String? = null
    private var reservations : MutableList<Reservation> = mutableListOf<Reservation>()

    fun getIDUser():String?{
        return idUser
    }
    fun setIDUser(idValue:String){
        idUser = if(idValue.isNotEmpty()) idValue else null
    }
    fun isDefaultUser():Boolean{
        if (!this.fullName.equals("Full Name")) return false
        if (!this.nickName.equals("Nickname")) return false
        if (this.age!=0) return false

        return true;
    }
    fun isDeafultFullName(): Boolean {
        if (!this.fullName.equals("Full Name")) return false

        return true
    }

    fun isDeafultNickName(): Boolean {
        if (!this.nickName.equals("Nickname")) return false
        return true
    }

    fun isDeafultImageProfile(): Boolean {
        if (!this.imageUri.equals("")) return false
        return true
    }

    fun getPropertiesUserFromKey(key:String):String{
        when(key){
            "fullName" -> return this.fullName
            "nickName" -> return this.nickName
            "achievements" -> return this.achievements
            "description" -> return this.description
            "objective" -> return this.objective
            "achievements" -> return this.achievements
            else -> return ""
        }
    }

    fun setReservations(reservations : MutableList<Reservation>?){
        this.reservations = reservations!!;
    }

    fun getReservations():MutableList<Reservation>{
        return reservations
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

    fun toDatabase(bitMapImage:Bitmap?):userModel{
        return userModel(fullName, nickName, age, objective, skillLevel, description, sport, achievements, encodeImage(bitMapImage), skillLevelIndex)
    }

    private fun encodeImage(bm: Bitmap?): String? {
        if(bm == null){

        }
        else
        {
            val baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            return Base64.encodeToString(b, Base64.DEFAULT)
        }

        return ""
    }

    data class userModel(
        val fullName: String = "Full Name",
        val nickName: String = "Nickname",
        val age: Int = 0,
        val objective: String = "Objective",
        val skillLevel: String = "Beginner",
        val description: String = "Description",
        val sport: String = "Soccer",
        val achievements: String = "Top10World",
        val imageUri: String?  ,
        val skillLevelIndex: Int = 0
    )
}



