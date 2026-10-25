package com.example.dessertclicker.data

import androidx.annotation.DrawableRes
import com.example.dessertclicker.data.Datasource.dessertList

data class DessertUiState(
    val currentDessertIndex: Int = 0,
    val dessertsSold: Int = 0,
    val revenue: Int = 0,
    val currentDessertPrice: Int = dessertList[currentDessertIndex].price,
    @param:DrawableRes val currentDessertImageId: Int = dessertList[currentDessertIndex].imageId
)
