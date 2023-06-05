package com.example.courtreservationapp.courtavailability

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.courtreservationapp.MainViewModel
import com.example.courtreservationapp.data.Reservation
import com.example.courtreservationapp.data.TimeSlot
import es.dmoral.toasty.Toasty
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CAViewModel(val context: Context) : MainViewModel(context) {

    var userID : String = this.getCurrentUser()?.getIDUser().orEmpty()

    fun reserve(date: LocalDate, courtId: Int, timeSlots: List<Int>, description: String = "") {
        val dateString = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val tmpCourt = courts.value?.firstOrNull{it.id == courtId}
        val tmpSport = if(tmpCourt!=null && tmpCourt.sportID!=null) sports.value?.firstOrNull{it.id == tmpCourt?.sportID} else null
        val tmplistTimeSlot:MutableList<TimeSlot> = mutableListOf()

        timeSlots.forEach { ts ->
            val tmpTimeSlot = timeslots.value?.firstOrNull{it.id==ts}
            if(tmpTimeSlot!=null) tmplistTimeSlot.add(tmpTimeSlot)
        }

        if(!this.userID.isNotEmpty()){
            Toasty.error(context, "Error User Not logged...", Toast.LENGTH_SHORT, true).show()
            Log.d(
                "Reservation Firestore",
                "Error User Not Recognize ..."
            )
            return
            //log in
        }

        if(timeSlots.size == tmplistTimeSlot.size && tmplistTimeSlot.find { !it.isValid() }==null && tmpSport?.isValid()!! && tmpCourt?.isValid()!!){
                val tmpReservation:Reservation = Reservation(dateString, court = tmpCourt, sport = tmpSport , listTimeSlot = tmplistTimeSlot ,description = null,userID = userID )
                this.saveReservation(tmpReservation)
            }
            else{
                Log.d(
                    "Reservation Firestore",
                    "Error Saving the Reservation ... error : timeslot/court/timeslot not valid ..."
                )
                Toasty.error(context, "Error saving the reservation !!!", Toast.LENGTH_SHORT, true).show()
            }

    }


//    override fun onCleared() {
//        super.onCleared()
//    }
}