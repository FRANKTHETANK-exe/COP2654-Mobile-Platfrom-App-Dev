package com.example.flightsearch

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.flightsearch.data.FlightDatabase
import com.example.flightsearch.data.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FlightDatabaseTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var database: FlightDatabase

    private fun openDatabase() = Room.databaseBuilder(context,
        FlightDatabase::class.java, "flight_test")
        .createFromAsset("database/flight_search.db").build()

    @Before fun setUp() {
        context.deleteDatabase("flight_test")
        database = openDatabase()
    }

    @After fun tearDown() { database.close() }

    @Test fun searchMatchesCodeAndNameInPassengerOrder() = runBlocking {
        val dao = database.flightDao()
        assertEquals("AMS", dao.searchAirports("ams").first().single().iataCode)
        assertEquals("AMS", dao.searchAirports("Amsterdam").first().single().iataCode)
        val airports = dao.searchAirports("a").first()
        assertTrue(airports.size > 1)
        assertEquals(airports.sortedByDescending { it.passengers }, airports)
        assertTrue(dao.searchAirports("zzzzzz").first().isEmpty())
    }

    @Test fun destinationsExcludeDeparture() = runBlocking {
        val destinations = database.flightDao().getDestinations("AMS").first()
        assertEquals(32, destinations.size)
        assertTrue(destinations.none { it.iataCode == "AMS" })
        assertEquals(destinations.sortedByDescending { it.passengers }, destinations)
    }

    @Test fun favoriteSurvivesDatabaseReopenAndCanBeRemoved() = runBlocking {
        database.flightDao().toggleFavorite("AMS", "CDG")
        database.close()
        database = openDatabase()
        val favorite = database.flightDao().getFavorites().first().single()
        assertEquals("AMS", favorite.departureCode)
        assertEquals("CDG", favorite.destinationCode)
        database.flightDao().toggleFavorite("AMS", "CDG")
        assertTrue(database.flightDao().getFavorites().first().isEmpty())
    }

    @Test fun dataStoreRestoresSearchAndEmptyQuery() = runBlocking {
        val preferences = UserPreferencesRepository(context)
        val previous = preferences.searchQuery.first()
        try {
            preferences.saveSearchQuery("Amsterdam")
            assertEquals("Amsterdam", UserPreferencesRepository(context).searchQuery.first())
            preferences.saveSearchQuery("")
            assertEquals("", UserPreferencesRepository(context).searchQuery.first())
        } finally {
            preferences.saveSearchQuery(previous)
        }
    }
}
