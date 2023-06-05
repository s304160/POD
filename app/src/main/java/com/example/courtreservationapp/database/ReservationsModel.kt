package com.example.courtreservationapp.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.courtreservationapp.data.Reservation
import com.example.courtreservationapp.database.entities.ReservationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReservationsModel private constructor(private val db: ReservationsDB) {
    /*companion object {
        @Volatile
        private var INSTANCE: ReservationsModel? = null

        fun getModel(context: Context): ReservationsModel {
            return INSTANCE ?: synchronized(this) {
                val active = INSTANCE ?: ReservationsModel(ReservationsDB.getDatabase(context))

                active.start()

                INSTANCE = active
                INSTANCE
            }!!
        }
    }

    private var userId: Int? = null
    private var sportId: Int? = null

    val courts = db.courtDao().getCourts()
    val sports = db.sportDao().getSports()
    val timeSlots = db.timeSlotDao().getTimeSlots()

    private val reservationEntities = db.reservationDao().getReservations()
    val reservations: LiveData<List<Reservation>>
        get() = liveReservations

    private val liveReservations = MutableLiveData<List<Reservation>>()
    private val reservationsByUser = MutableLiveData<List<Reservation>>()
    private val reservationsBySport = MutableLiveData<List<Reservation>>()

    private val reservationEntitiesObserver = Observer<List<ReservationEntity>> { list ->
        update(list)
    }
    private val observer = Observer<List<Any>> {
        update(reservationEntities.value ?: listOf())
    }

    private fun update(list: List<ReservationEntity>) {
        val reservations = list.map { res ->
            val court = courts.value?.find { it.id == res.courtId }
            val sport = sports.value?.find { it.id == court?.sportId }
            val timeSlot = timeSlots.value?.find { it.id == res.timeSlotId }

            Reservation(
                res.id,
                res.date,
                court,
                sport,
                timeSlot,
                res.description,
                res.userId
            )
        }.filter { it.isValid() }

        liveReservations.value = reservations

        if (userId != null)
            reservationsByUser.value = reservations.filter { it.userId == userId }

        if (sportId != null)
            reservationsBySport.value = reservations.filter { it.sport?.id == sportId }
    }

    private fun start() {
        courts.observeForever(observer)
        reservationEntities.observeForever(reservationEntitiesObserver)
        sports.observeForever(observer)
        timeSlots.observeForever(observer)
    }

    fun getReservationsByUser(userId: Int): LiveData<List<Reservation>> {
        this.userId = userId

        return reservationsByUser.apply {
            value = liveReservations.value?.filter {
                it.userId == userId
            }
        }
    }

    fun getReservationsBySport(sportId: Int): LiveData<List<Reservation>> {
        this.sportId = sportId

        return reservationsBySport.apply {
            value = liveReservations.value?.filter {
                it.sport!!.id == sportId
            }
        }
    }

    fun newReservation(reservation: ReservationEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            db.reservationDao().newReservation(reservation)
        }
    }

    fun deleteBookingById(bookingId: Int) {
        CoroutineScope(Dispatchers.IO).launch() {
            db.reservationDao().deleteBookingById(bookingId)
        }
    }

    fun getTimeSlotsByDate(date: String): List<Reservation> {
        return reservations.value?.filter { it.date == date }.orEmpty()
    }

    fun getTimeSlotById(timeSlotId: Int): String? {
        return timeSlots.value?.find { it.id == timeSlotId }?.timeSlot
    }

    fun getCourtNameById(courtId: Int): String? {
        return courts.value?.find { it.id == courtId }?.name
    }

    fun getTimeSlotByTimeSlotId(timeSlotId: Int): String? {
        return timeSlots.value?.find { it.id == timeSlotId }?.timeSlot
    }

    fun getSportByCourtId(courtId: Int): String? {
        val sportId: Int? = courts.value?.find { it.id == courtId }?.sportId

        return sports.value?.find { it.id == sportId }?.name
    }

    fun deleteReservations(reservations: List<Reservation>) {
        reservations.forEach {
            CoroutineScope(Dispatchers.IO).launch {
                db.reservationDao().deleteBookings(it.id)
            }
        }
    }

    fun close() {
        courts.removeObserver(observer)
        reservationEntities.removeObserver(reservationEntitiesObserver)
        sports.removeObserver(observer)
        timeSlots.removeObserver(observer)
    }*/
}