package com.example.courtreservationapp.data

import com.example.courtreservationapp.database.entities.CourtEntity
import com.example.courtreservationapp.database.entities.TimeSlotEntity

data class Info(
    val id: Int,
    val text: String,
    var available: Boolean
) {
    constructor(court: Court, available: Boolean) :
            this(court.id, court.name, available)

    constructor(timeSlot: TimeSlot, available: Boolean) :
            this(timeSlot.id, timeSlot.timeslot, available)
}
