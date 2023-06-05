package com.example.courtreservationapp.data

data class User(
    val fullName: String = "",
    val nickName: String = "",
    val age: Int = 0,
    val sport: String = "",
    val skillLevel: Map<String, String> = mapOf(),
    val achievements: String = "",
    val objective: String = "",
    val description: String = ""
)



