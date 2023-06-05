package com.example.courtreservationapp.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "court")
data class CourtEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "sportId")
    val sportId: Int,
    @ColumnInfo(name = "name")
    val name: String
)
