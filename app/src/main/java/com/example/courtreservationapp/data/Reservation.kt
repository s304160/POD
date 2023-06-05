package com.example.courtreservationapp.data

data class Reservation(
    val date: String? ="",
    val court: Court? = null,
    val sport: Sport? = null,
    val listTimeSlot: MutableList<TimeSlot>? = mutableListOf(),
    val description: String?="",
    val userID: String? = "",
) : java.io.Serializable {

    lateinit var id:String
    fun isValid(): Boolean {
        date ?: return false
        court ?: return false
        sport ?: return false
        (listTimeSlot!=null && listTimeSlot?.size!! >0) ?: return false
        //description ?: return false
        userID?.equals("")==false ?: return false

        return true
    }

    fun isUserValid():Boolean{
        if(userID?.isNotEmpty()==true) return true

        return false
    }

    fun toDatabase():ReservationToDatabase{
        return ReservationToDatabase(date,court?.toDatabase(),sport?.toDatabase(),listTimeSlot?.map {it.toDatabase()},description,userID)
    }
    data class ReservationToDatabase(
        val date: String?,
        val court: Court.CourtToDatabase?,
        val sport: Sport.SportToDatabase?,
        val listTimeSlot: List<TimeSlot.TimeSlotToDatabase?>?,
        val description: String?,
        val userID: String?,
    ) : java.io.Serializable
}
