package com.example.courtreservationapp.ratingCourt

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.courtreservationapp.R
import com.example.courtreservationapp.data.Rating
import com.example.courtreservationapp.data.Reservation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RatingViewModel: ViewModel() {
    private val db = Firebase.firestore

    var loading = MutableLiveData(true)

    val reservations = MutableLiveData<List<Reservation>>().apply {
        Firebase.auth.currentUser?.uid?.let { id ->
            db.collection("Reservations").addSnapshotListener { snapshot, e ->
                loading.value = true

                if(e == null) {
                    value = snapshot!!.documents.map {
                        val tmp = it.toObject<Reservation>()!!
                        tmp.id = it.id
                        tmp
                    }.filter {
                        it.userID == id
                    }.sortedBy {
                        LocalDate.parse(it.date, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    }
                }

                loading.value = false
            }
        }
    }
    val ratings = MutableLiveData<List<Rating>>().apply {
        Firebase.auth.currentUser?.uid?.let { id ->
            db.collection("RatingCourts").addSnapshotListener { result, error ->
                if (error == null && result != null)
                    value = result.documents.map {
                        val tmp = it.toObject<Rating>()!!
                        tmp.id = it.id

                        tmp
                    }.filter {
                        it.userID == id
                    }
            }
        }
    }

    fun rate(rating: Rating, context: Context) {
        Firebase.auth.currentUser?.uid?.let {
            rating.userID = it

            db.collection("RatingCourts").document().set(rating)
                .addOnSuccessListener {
                    Toast.makeText(context, context.getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, context.getString(R.string.profile_update_failed), Toast.LENGTH_SHORT).show()
                }
        }
    }
}