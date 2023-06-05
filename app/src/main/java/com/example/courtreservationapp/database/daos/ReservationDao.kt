package com.example.courtreservationapp.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.courtreservationapp.database.entities.ReservationEntity

@Dao
interface ReservationDao {
    @Query("SELECT * FROM booking")
    fun getReservations(): LiveData<List<ReservationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun newReservation(reservation: ReservationEntity)

    @Query("SELECT * FROM booking WHERE userId = :userId")
    fun getUserReservation(userId: Int): LiveData<List<ReservationEntity>>

    @Query("UPDATE booking SET description=:description WHERE timeSlotId = :timeSlotId AND courtId = :courtId AND userId = :userId AND date=:date")
    fun updateBookings(
        timeSlotId: Int,
        courtId: Int,
        userId: Int,
        date: String,
        description: String
    ): Int

    @Query("DELETE FROM booking WHERE timeSlotId = :timeSlotId AND courtId = :courtId AND userId = :userId AND date=:date")
    fun deleteBookings(timeSlotId: Int, courtId: Int, userId: Int, date: String): Int

    @Query("DELETE FROM booking WHERE id = :bookingId")
    fun deleteBookingById(bookingId: Int): Int

    @Query("DELETE FROM booking WHERE id=:reservationId")
    fun deleteBookings(reservationId: Int): Int
}