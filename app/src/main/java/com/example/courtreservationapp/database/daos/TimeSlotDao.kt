package com.example.courtreservationapp.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.courtreservationapp.database.entities.TimeSlotEntity

@Dao
interface TimeSlotDao {
    @Query("SELECT * FROM timeSlot")
    fun getTimeSlots(): LiveData<List<TimeSlotEntity>>
}