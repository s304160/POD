package com.example.courtreservationapp.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "booking")
data class ReservationEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "courtId")
    val courtId: Int,
    @ColumnInfo(name = "timeSlotId")
    val timeSlotId: Int,
    @ColumnInfo(name = "description")
    val description: String = "description",
    @ColumnInfo(name = "userId")
    val userId: Int
) : java.io.Serializable
