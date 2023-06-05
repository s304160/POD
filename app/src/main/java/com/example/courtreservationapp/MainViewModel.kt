package com.example.courtreservationapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtreservationapp.data.Court
import com.example.courtreservationapp.data.Rating
import com.example.courtreservationapp.data.Reservation
import com.example.courtreservationapp.data.Sport
import com.example.courtreservationapp.data.TimeSlot
import com.example.courtreservationapp.data.User
import com.example.courtreservationapp.data.Utils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

open class MainViewModel(context: Context) : ViewModel() {
    private var context: Context = context
    private var database = Firebase.firestore
    var userCurrent: User = Utils().recoverLocalStorageUser(context)
    var reservationsCurrentUser: MutableLiveData<MutableList<Reservation>> = MutableLiveData(mutableListOf())
    var sports: MutableLiveData<List<Sport>> = MutableLiveData(listOf())
    var timeslots: MutableLiveData<List<TimeSlot>> = MutableLiveData(listOf())
    var courts: MutableLiveData<List<Court>> = MutableLiveData(listOf())
    var showLoader: MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)
    private var listTotalReservation : MutableLiveData<MutableList<Reservation>> = MutableLiveData(mutableListOf<Reservation>())
    init {
        viewModelScope.launch { async { prepareViewModel() } }
    }
    fun getCurrentUser(): User? {
        return if (userCurrent != null) userCurrent else Utils().recoverLocalStorageUser(context)
    }

    fun prepareViewModel() {
        getUserFromDatabase()
        getReservationsForUserFromDatabase(userCurrent.getIDUser())
        getSportFromDatabase()
        getTimeSlotsFromDatabase()
        getCourtsFromDatabase()
        getRatingForUserFromDatabase(userCurrent.getIDUser())
        getTotalReservations()
    }

    fun getUserFromDatabase() {
        if (userCurrent.getIDUser() != null && userCurrent.getIDUser()!!.isNotEmpty()) {
            Log.d("User Firestore", "Contact db to get user " + userCurrent.getIDUser() + " ...")
            var idUser: String = userCurrent.getIDUser().orEmpty();
            database.collection("Users").document(userCurrent.getIDUser()!!).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.data != null) {
                        userCurrent = document.toObject<User>()!!
                        userCurrent.setIDUser(idUser)
                    } else
                        Log.d(
                            "User Firestore",
                            "Using Default User (nickName : " + userCurrent.getIDUser() + ") not found ..."
                        )
                }
                .addOnFailureListener { exception ->
                    Log.d(
                        "User Firestore",
                        "Using Default User (nickName : " + userCurrent.getIDUser() + ") not found ... error : " + exception
                    )
                }
        } else
            Log.d("User Firestore", "Using Default User (no id found for the user)")
    }

    fun getReservationsForUserFromDatabase(userID: String? = null) {
        if (userID != null && userID.isNotEmpty()) {
            Log.d(
                "Reservation Firestore",
                "Contact db to get reservation for user " + userCurrent.getIDUser() + " ..."
            )

            database.collection("Reservations").whereEqualTo("userID", userID).addSnapshotListener{snapshot,e ->
                if(e!=null)
                    Log.d("Reservation Firestore",e.toString())

                var tmp = snapshot?.documents?.map { it ->
                    var reservation = it.toObject<Reservation>()!!; reservation.id = it.id; reservation
                }?.toMutableList()
                userCurrent.setReservations(tmp)
                reservationsCurrentUser.value = tmp
            }
        } else
            Log.d("Reservation Firestore", "No valid userID to get reservations")
    }

    fun getTotalReservations() {
            Log.d("Reservation Firestore", "Contact db to get reservations ...")

            database.collection("Reservations").addSnapshotListener{snapshot,e ->
                if(e!=null)
                    Log.d("Reservation Firestore",e.toString())

                var tmp = snapshot?.documents?.map { it ->
                    var reservation = it.toObject<Reservation>()!!; reservation.id = it.id; reservation }?.toMutableList()
                listTotalReservation.value = tmp
                showLoader.value = false;
            }
    }

    fun getRatingForUserFromDatabase(userID: String? = null) {
        if (userID != null && userID.isNotEmpty()) {
            Log.d(
                "Rating Firestore",
                "Contact db to get rating for user " + userCurrent.getIDUser() + " ..."
            )

            database.collection("Reservations").whereEqualTo("userID", userID).addSnapshotListener{snapshot,e ->
                if(e!=null)
                    Log.d("Reservation Firestore",e.toString())

                var tmp = snapshot?.documents?.map { it ->
                    var reservation = it.toObject<Reservation>()!!; reservation.id = it.id; reservation
                }?.toMutableList()
                userCurrent.setReservations(tmp)
                reservationsCurrentUser.value = tmp
                showLoader.value = false;


            }
        } else
            Log.d("Reservation Firestore", "No valid userID to get reservations")
    }

    fun getSportFromDatabase() {
        Log.d("Sports Firestore", "Contact db to get list sport ...")
        database.collection("Sports").get().addOnSuccessListener { documents ->
            sports.value = documents.documents.map { it ->
                var sport = it.toObject<Sport>()!!; sport.referenceDocID = it.id; sport
            }.toMutableList()
        }
            .addOnFailureListener { exception ->
                Log.d("Sports Firestore", "Error Get list Sports... error : " + exception)
            }
    }

    fun getTimeSlotsFromDatabase() {
        Log.d("TimeSlots Firestore", "Contact db to get list timeslots ...")
        database.collection("TimeSlots").get().addOnSuccessListener { documents ->
            timeslots.value = documents.documents.map { it ->
                var timeSlot = it.toObject<TimeSlot>()!!; timeSlot.referenceDocID = it.id; timeSlot
            }.sortedBy { it.id }.toMutableList()
        }
            .addOnFailureListener { exception ->
                Log.d("TimeSlots Firestore", "Error Get list TimeSlots... error : " + exception)
            }
    }

    fun getCourtsFromDatabase() {
        Log.d("Courts Firestore", "Contact db to get list courts ...")
        database.collection("Courts").get().addOnSuccessListener { documents ->
            courts.value = documents.documents.map { it ->
                var court = it.toObject<Court>()!!; court.referenceDocID = it.id; court
            }.toMutableList()
        }
            .addOnFailureListener { exception ->
                Log.d("Courts Firestore", "Error Get list TimeSlots... error : " + exception)
            }
    }
    fun saveReservation(data: Reservation) {
        Log.d("Reservation Firestore", "Try to store the reservation to db ...")
        var idReservation : String? = database.collection("Reservations").document().id
        if(userCurrent.isDefaultUser() || userCurrent.getIDUser()?.isEmpty()==true && !data.isUserValid()){
            Toasty.error(context, "Error User Not logged...", Toast.LENGTH_SHORT, true).show()
            Log.d(
                "Reservation Firestore",
                "Error User Not Recognize ..."
            )
            return
            //log in
        }
        if(idReservation?.isNotEmpty()!! && data.isValid()){
            database.collection("Reservations").document(idReservation)
                .set(data.toDatabase())
                .addOnCompleteListener{
                    Toasty.success(context, "Reservation Saved !!!", Toast.LENGTH_SHORT, true).show()
                    data.id = idReservation
                    reservationsCurrentUser.value?.add(data)
                }
                .addOnFailureListener { exception ->
                    Log.d(
                        "Reservation Firestore",
                        "Error Saving the Reservation ... error : " + exception
                    )
                    Toasty.error(context, "Error saving the reservation !!!", Toast.LENGTH_SHORT, true).show()
                }
        }
        else{
            Log.d(
                "Reservation Firestore",
                "Error Generating Id for the Reservation ..."
            )
            Toasty.error(context, "Error saving the reservation !!!", Toast.LENGTH_SHORT, true).show()
        }
    }

    fun deleteBooking(idReservation: String?){
        if(userCurrent.isDefaultUser() || userCurrent.getIDUser()?.isEmpty()==true){
            Toasty.error(context, "Error User Not logged...", Toast.LENGTH_SHORT, true).show()
            Log.d(
                "Reservation Firestore",
                "Error User Not Recognize ..."
            )
            return
            //log in
        }
        if(idReservation!=null && idReservation.isNotEmpty()){
            database.collection("Reservations").document(idReservation).delete().
            addOnSuccessListener {
                Toasty.success(context,"Booking Deleted !!!",Toast.LENGTH_LONG,true).show() }
        }
        else{
            Log.d(
                "Reservation Firestore",
                "Error Deleting the Reservation ..."
            )
            Toasty.error(context, "Error deleting the reservation !!!", Toast.LENGTH_SHORT, true).show()
        }
    }

    fun ratingCourt(ratingData : Rating){
        Log.d("Rating Firestore", "Try to rating the court to db ...")
        var idRating : String? = database.collection("RatingsCourts").document().id
        if(userCurrent.isDefaultUser() || userCurrent.getIDUser()?.isEmpty()==true || !ratingData.isUserValid()){
            Toasty.error(context, "Error User Not logged...", Toast.LENGTH_SHORT, true).show()
            Log.d(
                "Reservation Firestore",
                "Error User Not Recognize ..."
            )
            return
            //log in
        }
        if(ratingData.isValid()){
            database.collection("RatingsCourts").document(idRating!!).set(ratingData.toDatabase()).addOnCompleteListener{
                Toasty.success(context, "Rating Saved !!!", Toast.LENGTH_SHORT, true).show()
            }
            .addOnFailureListener { exception ->
                Log.d("Rating Firestore", "Error Saving the Rating ... error : " + exception)
                    Toasty.error(context, "Error saving the rating !!!", Toast.LENGTH_SHORT, true).show()
                }
        }
        else{
            Log.d("Rating Firestore","Error Rating  ... error : rating NOT valid")
            Toasty.error(context, "Rating NOT saved!!!", Toast.LENGTH_SHORT, true).show()
        }
    }

    fun updatingReservation(data: Reservation? = null, newListTimeSlot: MutableList<TimeSlot>? = mutableListOf(), newDescription:String? = "" ){
        if(userCurrent.isDefaultUser() || userCurrent.getIDUser()?.isEmpty()==true || data?.isUserValid()==false){
            Toasty.error(context, "Error User Not logged...", Toast.LENGTH_SHORT, true).show()
            Log.d(
                "Reservation Firestore",
                "Error User Not Recognize ..."
            )
            return
            //log in
        }
        if(data?.isValid()==true && (newListTimeSlot!!.isNotEmpty() || newDescription!!.isNotEmpty())){
            var tmpNewReservation:Reservation = data.copy(date = data.date, court = data.court,sport = data.sport,listTimeSlot = newListTimeSlot,description = newDescription, userID = data.userID)
            if(tmpNewReservation.isValid()){
                database.collection("Reservations").document(data.id)
                    .set(tmpNewReservation.toDatabase())
                    .addOnCompleteListener{
                        Toasty.success(context, "Reservation Updated !!!", Toast.LENGTH_SHORT, true).show()
                    }
                    .addOnFailureListener { exception ->
                        Log.d(
                            "Reservation Firestore",
                            "Error Updating the Reservation ... error : " + exception
                        )
                        Toasty.error(context, "Error saving the reservation !!!", Toast.LENGTH_SHORT, true).show()
                    }
            }
        }
        else{
            Log.d("Reservation Firestore","Error Updating Reservation  ... error :  NOT valid")
            Toasty.error(context, "Update Reservation NOT saved!!!", Toast.LENGTH_SHORT, true).show()
        }
    }

    fun unavailableSlotForCourtIDAndDate(courtID: Int?, dateReservation: String?): List<TimeSlot> {
          val tmpSelectedReservations : List<Reservation> =  listTotalReservation.value?.filter { it.court?.id == courtID && it.date == dateReservation} ?: listOf()
          var tmpListTimeSlotUnavaible : MutableList<TimeSlot> = mutableListOf()

          tmpSelectedReservations.forEach { it.listTimeSlot?.forEach { tmpListTimeSlotUnavaible.add(it) } }

          return tmpListTimeSlotUnavaible
    }

}

