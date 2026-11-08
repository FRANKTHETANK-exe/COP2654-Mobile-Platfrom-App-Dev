package com.example.flightsearch.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "search_preferences")

class UserPreferencesRepository(context: Context) {
    private val dataStore = context.applicationContext.dataStore
    private val searchKey = stringPreferencesKey("search_query")

    val searchQuery = dataStore.data.catch { exception ->
        if (exception is IOException) emit(emptyPreferences()) else throw exception
    }.map { it[searchKey] ?: "" }

    suspend fun saveSearchQuery(query: String) {
        dataStore.edit { it[searchKey] = query }
    }
}
