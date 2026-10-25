package com.example.dessertclicker

import com.example.dessertclicker.ui.DessertViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

class DessertViewModelTest {
    @Test
    fun newGameStartsWithCupcakeAndZeroSales() {
        val state = DessertViewModel().dessertUiState.value
        assertEquals(0, state.dessertsSold)
        assertEquals(0, state.revenue)
        assertEquals(5, state.currentDessertPrice)
        assertEquals(R.drawable.cupcake, state.currentDessertImageId)
    }

    @Test
    fun fifthSaleChargesCupcakePriceThenUnlocksDonut() {
        val viewModel = DessertViewModel()
        repeat(5) { viewModel.onDessertClicked() }
        val state = viewModel.dessertUiState.value
        assertEquals(5, state.dessertsSold)
        assertEquals(25, state.revenue)
        assertEquals(10, state.currentDessertPrice)
        assertEquals(R.drawable.donut, state.currentDessertImageId)
        viewModel.onDessertClicked()
        assertEquals(35, viewModel.dessertUiState.value.revenue)
    }

    @Test
    fun twentiethSaleUnlocksEclairWithCorrectRevenue() {
        val viewModel = DessertViewModel()
        repeat(20) { viewModel.onDessertClicked() }
        val state = viewModel.dessertUiState.value
        assertEquals(20, state.dessertsSold)
        assertEquals(175, state.revenue)
        assertEquals(15, state.currentDessertPrice)
        assertEquals(R.drawable.eclair, state.currentDessertImageId)
        viewModel.onDessertClicked()
        assertEquals(190, viewModel.dessertUiState.value.revenue)
    }

    @Test
    fun finalDessertContinuesSellingAfterLastUnlock() {
        val viewModel = DessertViewModel()
        repeat(20000) { viewModel.onDessertClicked() }
        val before = viewModel.dessertUiState.value
        assertEquals(R.drawable.oreo, before.currentDessertImageId)
        assertEquals(6000, before.currentDessertPrice)
        viewModel.onDessertClicked()
        val after = viewModel.dessertUiState.value
        assertEquals(20001, after.dessertsSold)
        assertEquals(before.revenue + 6000, after.revenue)
        assertEquals(R.drawable.oreo, after.currentDessertImageId)
    }
}
