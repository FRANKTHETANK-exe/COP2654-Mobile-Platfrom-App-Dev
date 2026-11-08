package com.example.flightsearch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Airport::class, Favorite::class], version = 1, exportSchema = true)
abstract class FlightDatabase : RoomDatabase() {
    abstract fun flightDao(): FlightDao

    companion object {
        @Volatile private var instance: FlightDatabase? = null

        fun getDatabase(context: Context): FlightDatabase =
            instance ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext,
                    FlightDatabase::class.java, "flight_search")
                    .createFromAsset("database/flight_search.db")
                    .build().also { instance = it }
            }
    }
}
