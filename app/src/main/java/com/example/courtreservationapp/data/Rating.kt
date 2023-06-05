package com.example.courtreservationapp.data


data class Rating(
    var id : String = "",
    val userID: String = "",
    val dateRating: String ="",
    val courtID : Int = 0,
    val pointRating : Int = -1,
    val descriptionRating:String = "")
{
    fun isValid(): Boolean {
        userID.isNotEmpty() ?: return false
        dateRating ?: return false
        courtID!=0 ?: return false
        pointRating!=-1 ?:return false

        return true
    }

    fun isUserValid():Boolean{
        if(userID?.isNotEmpty()==true) return true

        return false
    }

    fun toDatabase():RatingToDatabase{
        return RatingToDatabase(id,userID,dateRating,courtID,pointRating,descriptionRating)
    }

    data class RatingToDatabase(var id:String="", val userID: String = "", val dateRating: String ="", val courtID : Int = 0,val pointRating : Int = -1,
                                val descriptionRating:String = "")
}