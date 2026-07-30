package com.jackson.blebridge.core.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test

class NeveraResultTest {
    @Test
    fun `map은 success와 failure를 각각 변환한다`() {
        val success = NeveraResult.Success(2).map(
            transformSuccess = { it * 3 },
            transformFailure = { "error-$it" },
        )
        val failure = NeveraResult.Failure(404).map(
            transformSuccess = { value: Int -> value * 3 },
            transformFailure = { "error-$it" },
        )

        val mappedSuccess = assertInstanceOf(NeveraResult.Success::class.java, success)
        val mappedFailure = assertInstanceOf(NeveraResult.Failure::class.java, failure)
        assertEquals(6, mappedSuccess.data)
        assertEquals("error-404", mappedFailure.error)
    }
}
