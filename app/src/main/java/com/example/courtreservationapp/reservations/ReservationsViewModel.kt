package com.example.courtreservationapp.reservations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.courtreservationapp.data.Court
import com.example.courtreservationapp.data.Reservation
import com.example.courtreservationapp.data.Sport
import com.example.courtreservationapp.data.TimeSlot
import com.example.courtreservationapp.data.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReservationsViewModel : ViewModel() {

    var userCurrent: MutableLiveData<User> = MutableLiveData()
    var reservations: MutableLiveData<List<Reservation>> = MutableLiveData()
    var timeSlots: MutableLiveData<List<TimeSlot>> = MutableLiveData()
    var courts: MutableLiveData<List<Court>> = MutableLiveData()
    var sports: MutableLiveData<List<Sport>> = MutableLiveData()

    fun prepareViewModel(
        userCurrent: User? = User(),
        reservations: MutableList<Reservation> = mutableListOf(),
        courts: List<Court> = listOf(),
        sports: List<Sport> = listOf(),
        timeslots: List<TimeSlot> = listOf()
    ) {

        this.reservations.value = reservations
        this.userCurrent.value = userCurrent
        this.timeSlots.value = timeslots
        this.courts.value = courts
        this.sports.value = sports
    }

    fun getReservations(): LiveData<List<Reservation>> {
        return reservations
    }

    fun getReservationsByDate(date: LocalDate): List<Reservation> {

        val reservationsByDate = reservations.value!!.filter {
            it.date == date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        }

        return reservationsByDate
    }

    fun getCourts(): LiveData<List<Court>> {
        return courts
    }
}