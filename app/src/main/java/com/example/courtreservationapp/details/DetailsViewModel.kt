package com.example.courtreservationapp.details

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.courtreservationapp.data.Reservation
import com.example.courtreservationapp.data.TimeSlot

class DetailsViewModel : ViewModel() {

    var userID: String = ""
    var timeSlots: MutableLiveData<List<TimeSlot>> = MutableLiveData(listOf())
    var description: MutableLiveData<String> = MutableLiveData("")
    var selectedTimeSlots: MutableLiveData<MutableList<TimeSlot>> =
        MutableLiveData<MutableList<TimeSlot>>(mutableListOf())
    val unavailableTimeSlots: MutableLiveData<List<TimeSlot>> = MutableLiveData(listOf())
    val reservationSelected: MutableLiveData<Reservation?> = MutableLiveData(null)

    fun selectTimeSlot(timeSlotId: Int) {
        val tmpTimeSlotSelected: TimeSlot? = timeSlots.value?.find { it.id == timeSlotId } ?: null
        if (tmpTimeSlotSelected != null) {
            var tmpList: MutableList<TimeSlot> = mutableListOf()
            tmpList.addAll(selectedTimeSlots.value!!)
            if (tmpList.find { it.id == timeSlotId } != null) tmpList =
                tmpList.filter { it.id != timeSlotId }.toMutableList()
            else tmpList.add(tmpTimeSlotSelected)

            selectedTimeSlots.value = mutableListOf()
            selectedTimeSlots.value = tmpList
        }
    }


    fun prepareViewModel(
        reservationSelected: Reservation,
        unavailableTimeSlot: List<TimeSlot>,
        timeSlot: List<TimeSlot>,
        userID: String
    ) {
        this.reservationSelected.value = reservationSelected
        this.description.value = ""
        this.userID = userID
        this.timeSlots.value = timeSlot
        this.unavailableTimeSlots.value = unavailableTimeSlot
        selectedTimeSlots.value = mutableListOf()
        selectedTimeSlots.value!!.addAll(reservationSelected.listTimeSlot!!)
    }

    fun defineButtonColor(buttonID: Int): Color {
        if (selectedTimeSlots.value!!.find { it.id === buttonID } != null) return Color.Red
        if (unavailableTimeSlots.value?.find { it.id == buttonID } != null && reservationSelected.value?.listTimeSlot?.find { it.id == buttonID } == null) return Color.Gray

        return Color.Blue
    }
}
