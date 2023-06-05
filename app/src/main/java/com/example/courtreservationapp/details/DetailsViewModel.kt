package com.example.courtreservationapp.details

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.courtreservationapp.R
import com.example.courtreservationapp.data.Reservation
import com.example.courtreservationapp.data.TimeSlot
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class DetailsViewModel : ViewModel() {
    private val db = Firebase.firestore

    var reservationToChange = MutableLiveData<Reservation>()
    var selectedTimeSlots = MutableLiveData<MutableList<TimeSlot>>()

    var timeSlots = MutableLiveData<List<TimeSlot>>().apply {
        db.collection("TimeSlots").get()
            .addOnSuccessListener { value = it.documents.map { ts ->
                val tmp = ts.toObject<TimeSlot>()!!
                tmp.id = ts.id
                tmp
            }.sortedBy { ts -> ts.order } }
    }

    private val reservationsByDate = MutableLiveData<List<Reservation?>>()

    val unavailableTimeSlots = MutableLiveData<List<String>>()


    fun setup(id: String) {
        Firebase.auth.currentUser?.uid?.let { userID ->
            db.collection("Reservations")
                .addSnapshotListener { snapshot, e ->
                    if (e == null && snapshot != null) {
                        val tmp = snapshot.documents.find {
                            it.id == id
                        }?.toObject<Reservation>()
                        tmp?.id = id

                        selectedTimeSlots.value = tmp?.listTimeSlot

                        reservationsByDate.value = snapshot.documents.map {
                            it.toObject<Reservation>()
                        }.filter {
                            it!!.sportID!! == tmp?.sportID &&
                                    it.court!!.id == tmp.court?.id
                            it.date == tmp?.date
                        }

                        unavailableTimeSlots.value = reservationsByDate.value?.filter {
                            it?.userID != userID
                        }?.map {
                            it?.listTimeSlot?: listOf()
                        }?.flatten()?.map {
                            it.id
                        }

                        reservationToChange.value = tmp
                        if (description.value == null)
                            description.value = tmp?.description
                    }
                }
        }
    }

    val description = MutableLiveData<String>()
    fun setDescription(value: String) {
        description.value = value
    }

    fun selectTimeSlot(timeSlotId: String) {
        val tmpList = selectedTimeSlots.value?.toMutableList() ?: mutableListOf()
        val tmpTimeSlot = tmpList.find { it.id == timeSlotId }

        if(tmpTimeSlot != null)
            tmpList.remove(tmpTimeSlot)
        else
            tmpList.add(timeSlots.value!!.find { it.id == timeSlotId }!!)

        selectedTimeSlots.value = tmpList
    }


    fun updateReservation(context: Context) {
        val newReservation = Reservation(
            id = reservationToChange.value!!.id,
            date =reservationToChange.value!!.date,
            court = reservationToChange.value!!.court,
            sportID = reservationToChange.value!!.sportID,
            listTimeSlot = selectedTimeSlots.value!!,
            description = description.value?: "",
            userID = reservationToChange.value!!.userID
        )
        db.collection("Reservations").document(reservationToChange.value!!.id!!).set(newReservation)
            .addOnSuccessListener {
                Toast.makeText(context, context.getString(R.string.reservation_modified_successfully), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, context.getString(R.string.reservation_modification_failed), Toast.LENGTH_SHORT).show()
            }
    }
}
