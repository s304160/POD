package com.example.courtreservationapp.reservations

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.courtreservationapp.data.Reservation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ReservationsViewModel : ViewModel() {
    private val db = Firebase.firestore
    var loading = MutableLiveData(true)

    var reservations = MutableLiveData<List<Reservation>>().apply {
        Firebase.auth.currentUser?.uid?.let { id ->
            db.collection("Reservations").addSnapshotListener { snapshot, e ->
                loading.value = true

                if(e == null) {
                    value = snapshot!!.documents.map {
                        val tmp = it.toObject<Reservation>()!!
                        tmp.id = it.id
                        tmp.listTimeSlot = tmp.listTimeSlot.sortedBy{ l -> l.order}.toMutableList()
                        tmp
                    }.filter {
                        it.userID == id
                    }
                }

                loading.value = false
            }
        }
    }
}