package com.example.flightsearch.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchApp(viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.Factory)) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    Scaffold(topBar = { TopAppBar(title = { Text("Flight Search") }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = viewModel::updateQuery,
                label = { Text("Departure airport") },
                placeholder = { Text("Airport name or IATA code") },
                singleLine = true,
                enabled = !state.isLoading,
                trailingIcon = {
                    if (state.query.isNotEmpty()) IconButton(onClick = { viewModel.updateQuery("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear search")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            val title = when {
                state.query.isBlank() -> "Favorite routes"
                state.departure != null -> "Flights from ${state.departure!!.iataCode}"
                else -> "Choose a departure airport"
            }
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)) {
                    when {
                        state.query.isBlank() -> {
                            if (state.favorites.isEmpty()) item {
                                Text("Search for an airport and save a route to see it here.")
                            }
                            items(state.favorites, key = { it.id }) { favorite ->
                                FlightCard(favorite.departureCode, null, favorite.destinationCode, null, true) {
                                    viewModel.toggleFavorite(favorite.departureCode, favorite.destinationCode)
                                }
                            }
                        }
                        state.departure == null -> {
                            if (state.suggestions.isEmpty()) item { Text("No airports found.") }
                            items(state.suggestions, key = { it.id }) { airport ->
                                Card(onClick = {
                                    viewModel.selectAirport(airport)
                                    focusManager.clearFocus()
                                }, modifier = Modifier.fillMaxWidth()) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text(airport.iataCode, fontWeight = FontWeight.Bold)
                                        Text(airport.name)
                                    }
                                }
                            }
                        }
                        else -> {
                            val departure = state.departure!!
                            items(state.destinations, key = { it.id }) { destination ->
                                val saved = state.favorites.any {
                                    it.departureCode == departure.iataCode && it.destinationCode == destination.iataCode
                                }
                                FlightCard(departure.iataCode, departure.name,
                                    destination.iataCode, destination.name, saved) {
                                    viewModel.toggleFavorite(departure.iataCode, destination.iataCode)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlightCard(departureCode: String, departureName: String?,
    destinationCode: String, destinationName: String?, saved: Boolean, onFavoriteClick: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("DEPART", style = MaterialTheme.typography.labelSmall)
                Text(departureCode, fontWeight = FontWeight.Bold)
                departureName?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                Spacer(Modifier.height(8.dp))
                Text("ARRIVE", style = MaterialTheme.typography.labelSmall)
                Text(destinationCode, fontWeight = FontWeight.Bold)
                destinationName?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
            }
            IconToggleButton(checked = saved, onCheckedChange = { onFavoriteClick() }) {
                Icon(Icons.Default.Star,
                    contentDescription = "${if (saved) "Remove" else "Save"} $departureCode to $destinationCode favorite",
                    tint = if (saved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
            }
        }
    }
}
