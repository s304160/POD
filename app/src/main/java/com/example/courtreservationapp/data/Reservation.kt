package com.example.courtreservationapp.data

data class Reservation(
    var id: String? = null,
    val date: String ="",
    val court: Court? = null,
    val sportID: String? = null,
    var listTimeSlot: MutableList<TimeSlot> = mutableListOf(),
    val description: String = "",
    val userID: String = "",
)
