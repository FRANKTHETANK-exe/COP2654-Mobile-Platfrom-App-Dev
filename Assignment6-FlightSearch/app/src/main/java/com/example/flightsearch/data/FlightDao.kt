package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {
    @Query("SELECT * FROM airport WHERE iata_code LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%' ORDER BY passengers DESC")
    fun searchAirports(query: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code != :departureCode ORDER BY passengers DESC")
    fun getDestinations(departureCode: String): Flow<List<Airport>>

    @Query("SELECT * FROM favorite ORDER BY id DESC")
    fun getFavorites(): Flow<List<Favorite>>

    @Query("SELECT * FROM favorite WHERE departure_code = :departure AND destination_code = :destination LIMIT 1")
    suspend fun findFavorite(departure: String, destination: String): Favorite?

    @Insert
    suspend fun insertFavorite(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE departure_code = :departure AND destination_code = :destination")
    suspend fun deleteFavorite(departure: String, destination: String)

    @Transaction
    suspend fun toggleFavorite(departure: String, destination: String) {
        if (findFavorite(departure, destination) == null) {
            insertFavorite(Favorite(departureCode = departure, destinationCode = destination))
        } else {
            deleteFavorite(departure, destination)
        }
    }
}
