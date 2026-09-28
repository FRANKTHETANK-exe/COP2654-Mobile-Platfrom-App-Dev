package com.example.tiptime

import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import java.text.NumberFormat
import org.junit.Rule
import org.junit.Test

class TipTimeUiTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun roundUpSwitchUpdatesTipAndCanBeTurnedOff() {
        composeTestRule.onNodeWithTag("bill_amount").performTextReplacement("55")
        composeTestRule.onNodeWithTag("tip_percentage").performTextReplacement("15")
        val tip = composeTestRule.onNodeWithTag("tip_amount")
        val toggle = composeTestRule.onNodeWithTag("round_up_toggle")
        val currency = NumberFormat.getCurrencyInstance()

        toggle.assertIsOff()
        tip.assertTextEquals("Tip Amount: ${currency.format(8.25)}")
        toggle.performClick().assertIsOn()
        tip.assertTextEquals("Tip Amount: ${currency.format(9.0)}")
        toggle.performClick().assertIsOff()
        tip.assertTextEquals("Tip Amount: ${currency.format(8.25)}")
    }
}
