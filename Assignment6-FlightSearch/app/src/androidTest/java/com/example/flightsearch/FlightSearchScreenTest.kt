package com.example.flightsearch

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class FlightSearchScreenTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun typingSelectingAndClearingSearch() {
        compose.waitUntil(10_000) {
            compose.onAllNodes(hasSetTextAction() and isEnabled()).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNode(hasSetTextAction()).performTextClearance()
        compose.onNode(hasSetTextAction()).performTextInput("Am")
        compose.onNode(hasSetTextAction()).assertTextContains("Am")
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Amsterdam Airport Schiphol").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Amsterdam Airport Schiphol").performClick()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Flights from AMS").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithContentDescription("Save AMS to SVO favorite").performClick()
        compose.onNodeWithContentDescription("Clear search").performClick()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithContentDescription("Remove AMS to SVO favorite").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Favorite routes").assertIsDisplayed()
        compose.onNodeWithContentDescription("Remove AMS to SVO favorite").performClick()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Search for an airport and save a route to see it here.").fetchSemanticsNodes().isNotEmpty()
        }
    }
}
