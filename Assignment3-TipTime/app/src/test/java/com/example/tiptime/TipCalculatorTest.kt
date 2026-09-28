package com.example.tiptime

import org.junit.Assert.assertEquals
import org.junit.Test

class TipCalculatorTest {
    @Test
    fun calculatesCustomPercentage() {
        assertEquals(8.25, calculateTipAmount(55.0, 15.0), 0.00001)
        assertEquals(20.0, calculateTipAmount(100.0, 20.0), 0.00001)
    }

    @Test
    fun roundsUpToTheNextWholeDollar() {
        assertEquals(9.0, calculateTipAmount(55.0, 15.0, roundUp = true), 0.00001)
        assertEquals(1.0, calculateTipAmount(1.0, 1.0, roundUp = true), 0.00001)
    }

    @Test
    fun leavesWholeDollarAndZeroTipsUnchanged() {
        assertEquals(15.0, calculateTipAmount(100.0, 15.0, roundUp = true), 0.00001)
        assertEquals(0.0, calculateTipAmount(0.0, 15.0, roundUp = true), 0.00001)
    }

    @Test
    fun handlesEmptyInvalidAndDecimalInputs() {
        assertEquals(0.0, parseNumber(""), 0.0)
        assertEquals(0.0, parseNumber("abc"), 0.0)
        assertEquals(0.0, parseNumber("NaN"), 0.0)
        assertEquals(0.0, parseNumber("-10"), 0.0)
        assertEquals(55.5, parseNumber("55,5"), 0.0)
        assertEquals(0.0, calculateTipAmount(Double.POSITIVE_INFINITY), 0.0)
    }
}
