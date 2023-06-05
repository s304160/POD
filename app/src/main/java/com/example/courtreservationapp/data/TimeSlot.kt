package com.example.courtreservationapp.data

data class TimeSlot(var id:Int=0, var timeslot: String="", var referenceDocID:String = ""): java.io.Serializable{

    fun isValid(): Boolean {
        id==0 ?: return false
        timeslot.isEmpty() ?: return false

        return true
    }

    fun toDatabase():TimeSlotToDatabase{
        return TimeSlotToDatabase(id,timeslot)
    }

    data class TimeSlotToDatabase(var id:Int=0, var timeslot: String="") : java.io.Serializable
}
