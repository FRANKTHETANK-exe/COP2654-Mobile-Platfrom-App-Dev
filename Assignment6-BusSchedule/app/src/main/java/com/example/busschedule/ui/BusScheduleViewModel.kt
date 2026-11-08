package com.example.busschedule.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.busschedule.data.BusScheduleDao
import com.example.busschedule.data.BusScheduleDatabase

class BusScheduleViewModel(private val dao: BusScheduleDao) : ViewModel() {
    fun getFullSchedule() = dao.getAll()
    fun getScheduleFor(stopName: String) = dao.getByStopName(stopName)

    companion object {
        val factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[APPLICATION_KEY])
                BusScheduleViewModel(BusScheduleDatabase.getDatabase(application).busScheduleDao())
            }
        }
    }
}
