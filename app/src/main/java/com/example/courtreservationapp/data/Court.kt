package com.example.courtreservationapp.data

data class Court(var id: Int = 0, var sportID: Int = 0, var name: String = "", val address : String="", var referenceDocID:String = ""): java.io.Serializable{

    fun toDatabase():CourtToDatabase{
        return CourtToDatabase(id,sportID,name,address)
    }

    fun isValid(): Boolean {
        id==0 ?: return false
        name.isEmpty() ?: return false
        sportID==0 ?: return false
        return true
    }

    data class CourtToDatabase(var id: Int = 0, var sportID: Int = 0, var name: String = "", val address : String="")
}
