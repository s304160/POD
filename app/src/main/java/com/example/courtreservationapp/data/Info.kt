package com.example.courtreservationapp.data

data class Info(
    val id: String,
    val text: String,
    var available: Boolean
) {
    constructor(court: Court, available: Boolean) :
            this(court.id, court.name, available)

    constructor(timeSlot: TimeSlot, available: Boolean) :
            this(timeSlot.id, timeSlot.timeslot, available)
}
