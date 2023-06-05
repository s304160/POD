package com.example.courtreservationapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.courtreservationapp.database.daos.CourtDao
import com.example.courtreservationapp.database.daos.ReservationDao
import com.example.courtreservationapp.database.daos.SportDao
import com.example.courtreservationapp.database.daos.TimeSlotDao
import com.example.courtreservationapp.database.entities.CourtEntity
import com.example.courtreservationapp.database.entities.ReservationEntity
import com.example.courtreservationapp.database.entities.SportEntity
import com.example.courtreservationapp.database.entities.TimeSlotEntity

@Database(
    entities = [
        SportEntity::class,
        CourtEntity::class,
        TimeSlotEntity::class,
        ReservationEntity::class],
    version = 1
)
abstract class ReservationsDB : RoomDatabase() {
    abstract fun sportDao(): SportDao
    abstract fun courtDao(): CourtDao
    abstract fun timeSlotDao(): TimeSlotDao
    abstract fun reservationDao(): ReservationDao

    companion object {
        @Volatile
        private var INSTANCE: ReservationsDB? = null

        fun getDatabase(context: Context): ReservationsDB {
            return INSTANCE ?: synchronized(this) {
                val i = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ReservationsDB::class.java,
                    "my_database"
                ).createFromAsset(
                    "databases/reservations.db"
                ).build()

                INSTANCE = i
                INSTANCE
            }!!
        }
    }
}