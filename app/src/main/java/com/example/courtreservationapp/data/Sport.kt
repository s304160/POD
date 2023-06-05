package com.example.courtreservationapp.data

data class Sport(var id:Int=0, var name:String="", var referenceDocID:String = ""): java.io.Serializable{
    fun isValid(): Boolean {
        id==0 ?: return false
        name.isEmpty() ?: return false

        return true
    }

    fun toDatabase():SportToDatabase{
        return SportToDatabase(id,name)
    }

    data class SportToDatabase(var id:Int=0, var name:String="")
}
