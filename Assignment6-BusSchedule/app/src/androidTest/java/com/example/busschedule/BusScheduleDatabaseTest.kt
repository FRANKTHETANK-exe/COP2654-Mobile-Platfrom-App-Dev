package com.example.busschedule

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.busschedule.data.BusScheduleDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BusScheduleDatabaseTest {
    private lateinit var database: BusScheduleDatabase

    @Before fun openDatabase() {
        database = Room.databaseBuilder(ApplicationProvider.getApplicationContext(),
            BusScheduleDatabase::class.java, "bus_test")
            .createFromAsset("database/bus_schedule.db").build()
    }

    @After fun closeDatabase() { database.close() }

    @Test fun assetLoadsAllSchedulesInArrivalOrder() = runBlocking {
        val schedules = database.busScheduleDao().getAll().first()
        assertEquals(31, schedules.size)
        assertEquals(schedules.sortedBy { it.arrivalTimeInMillis }, schedules)
    }

    @Test fun selectedStopOnlyReturnsItsArrivals() = runBlocking {
        val schedules = database.busScheduleDao().getByStopName("Main Street").first()
        assertTrue(schedules.size > 1)
        assertTrue(schedules.all { it.stopName == "Main Street" })
        assertEquals(schedules.sortedBy { it.arrivalTimeInMillis }, schedules)
    }

    @Test fun unknownStopHasNoArrivals() = runBlocking {
        assertTrue(database.busScheduleDao().getByStopName("Missing stop").first().isEmpty())
    }
}
