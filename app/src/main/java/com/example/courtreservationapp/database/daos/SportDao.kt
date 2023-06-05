package com.example.courtreservationapp.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.courtreservationapp.database.entities.SportEntity

@Dao
interface SportDao {
    @Query("SELECT * FROM sport")
    fun getSports(): LiveData<List<SportEntity>>
}