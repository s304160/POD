package com.example.courtreservationapp.data


data class Rating(
    var id : String = "",
    val reservationID: String = "",
    var userID: String = "",
    val date: String = "",
    val courtID : String = "",
    val pointRating : Int = -1,
    val descriptionRating: String = ""
)