package com.example.courtreservationapp.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.courtreservationapp.database.entities.CourtEntity

@Dao
interface CourtDao {
    @Query("SELECT * FROM court")
    fun getCourts(): LiveData<List<CourtEntity>>
}