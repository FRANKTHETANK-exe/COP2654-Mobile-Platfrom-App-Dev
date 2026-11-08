package com.example.flightsearch.ui

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FlightSearchUiState(
    val query: String = "",
    val suggestions: List<Airport> = emptyList(),
    val departure: Airport? = null,
    val destinations: List<Airport> = emptyList(),
    val favorites: List<Favorite> = emptyList(),
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
class FlightSearchViewModel(
    private val dao: FlightDao,
    private val preferences: UserPreferencesRepository
) : ViewModel() {
    var searchQuery by mutableStateOf("")
        private set
    private val query = MutableStateFlow("")
    private val departure = MutableStateFlow<Airport?>(null)
    private val ready = MutableStateFlow(false)
    private val searchChanges = Channel<String>(Channel.CONFLATED)

    init {
        viewModelScope.launch {
            searchQuery = preferences.searchQuery.first()
            query.value = searchQuery
            ready.value = true
            // Save changes in order, including clearing the field.
            for (text in searchChanges) preferences.saveSearchQuery(text)
        }
    }

    private val results = combine(query, departure) { text, airport -> text to airport }
        .flatMapLatest { (text, airport) ->
            when {
                text.isBlank() -> flowOf(FlightSearchUiState(query = text))
                airport == null -> dao.searchAirports(text.trim()).map {
                    FlightSearchUiState(query = text, suggestions = it)
                }
                else -> dao.getDestinations(airport.iataCode).map {
                    FlightSearchUiState(query = text, departure = airport, destinations = it)
                }
            }
        }

    val uiState = combine(results, dao.getFavorites(), ready) { result, favorites, loaded ->
        result.copy(favorites = favorites, isLoading = !loaded)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FlightSearchUiState())

    fun updateQuery(text: String) {
        searchQuery = text
        departure.value = null
        query.value = text
        searchChanges.trySend(text)
    }

    fun selectAirport(airport: Airport) {
        searchQuery = airport.iataCode
        query.value = airport.iataCode
        departure.value = airport
        searchChanges.trySend(airport.iataCode)
    }

    fun toggleFavorite(departureCode: String, destinationCode: String) {
        viewModelScope.launch { dao.toggleFavorite(departureCode, destinationCode) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[APPLICATION_KEY])
                FlightSearchViewModel(FlightDatabase.getDatabase(application).flightDao(),
                    UserPreferencesRepository(application))
            }
        }
    }
}
