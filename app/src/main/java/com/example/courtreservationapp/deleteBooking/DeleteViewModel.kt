package com.example.courtreservationapp.deleteBooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.courtreservationapp.data.Reservation
import android.content.Context
import android.widget.Toast
import com.example.courtreservationapp.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class DeleteViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val reservation = MutableLiveData<Reservation>()

    fun getReservation(id: String): LiveData<Reservation> {
        db.collection("Reservations").document(id).addSnapshotListener { result, e ->
            if (e == null && result != null) {
                reservation.value = result.toObject<Reservation>()
                reservation.value?.id = id
            }
        }

        return reservation
    }
    fun deleteBooking(reservationId: String, context: Context) {
        db.collection("Reservations").document(reservationId).delete()
            .addOnSuccessListener {
                Toast.makeText(context, context.getString(R.string.reservation_deleted_successfully), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, context.getString(R.string.reservation_deletion_failed), Toast.LENGTH_SHORT).show()
            }
    }
}