package com.example.courtreservationapp.courtavailability

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.courtreservationapp.R
import com.example.courtreservationapp.data.Court
import com.example.courtreservationapp.data.Info
import com.example.courtreservationapp.data.Reservation
import com.example.courtreservationapp.data.Sport
import com.example.courtreservationapp.data.TimeSlot
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CAViewModel : ViewModel() {
    private val db = Firebase.firestore

    var loading = MutableLiveData(true)

    val sportId = MutableLiveData<String>()
    val sports = MutableLiveData<List<Sport>>().apply {
        db.collection("Sports").get()
            .addOnSuccessListener {
                value = it.documents.map { s ->
                    val tmp = s.toObject<Sport>()

                    Sport(s.id, tmp!!.name)
                }

                sportId.value = value!!.first().id
            }
    }

    val reservations = MutableLiveData<List<Reservation>>().apply {
        db.collection("Reservations").addSnapshotListener { snapshot, e ->
            loading.value = true

            if(e != null) { Log.w("reservations", e.message!!) }
            else {
                value = snapshot!!.documents.map {
                    val tmp = it.toObject<Reservation>()!!
                    tmp.id = it.id
                    tmp
                }
            }

            loading.value = false
        }

    }

    val timeSlots = MutableLiveData<List<TimeSlot>>().apply {
        db.collection("TimeSlots").get()
            .addOnSuccessListener { value = it.documents.map { ts ->
                val tmp = ts.toObject<TimeSlot>()
                TimeSlot(ts.id, tmp!!.timeslot, tmp.order) }.sortedBy { ts -> ts.order } }
    }

    val courts = MutableLiveData<List<Court>>().apply {
        db.collection("Courts").get()
            .addOnSuccessListener {
                value = it.documents.map { c ->
                    val tmp = c.toObject<Court>()
                    Court(id = c.id, sportID = tmp!!.sportID, name = tmp.name, address = tmp.address)
                }.sortedBy { x -> x.id }
            }
    }

    fun setSportId(value: String) {
        sportId.value = value
    }

    fun addDate(date: String, sportId: String): Map<TimeSlot, List<Info>> {
        val courts = this.courts.value?.filter { it.sportID == sportId }
        val bookedToday = reservations.value?.filter { it.date == date }

        return timeSlots.value?.groupBy { it }?.mapValues { timeSlot ->
            courts?.map { court ->
                Info(court,
                    bookedToday?.none {
                        it.court?.id == court.id && it.date == date &&
                        it.listTimeSlot.contains(timeSlot.key)
                    }?: true)
            }?: listOf()
        }?: mapOf()
    }



    fun reserve(date: LocalDate, courtId: String, selectedTs: List<String>, context: Context) {

        val dateString = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val tmpCourt = courts.value?.firstOrNull{it.id == courtId}
        val tmpSport = if(tmpCourt?.sportID != null) sports.value?.firstOrNull{it.id == tmpCourt.sportID} else null
        val tmpListTimeSlot: MutableList<TimeSlot> = mutableListOf()

        selectedTs.forEach { ts ->
            val tmpTimeSlot = timeSlots.value!!.firstOrNull{it.id == ts}
            if(tmpTimeSlot!=null) tmpListTimeSlot.add(tmpTimeSlot)
        }

        db.collection("Reservations").document()
            .set(Reservation(
                date = dateString,
                court = tmpCourt,
                sportID = tmpSport?.id,
                listTimeSlot = tmpListTimeSlot,
                userID = Firebase.auth.currentUser!!.uid)
            )
            .addOnSuccessListener {
                Toast.makeText(context, context.getString(R.string.reservation_created_successfully), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, context.getString(R.string.reservation_creation_failed) , Toast.LENGTH_SHORT).show()
            }
    }
}