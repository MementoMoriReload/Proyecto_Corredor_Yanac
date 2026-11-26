package com.example.panalsuite

import com.example.panalsuite.network.HolidayApiService
import com.example.panalsuite.network.HolidayDTO
import com.example.panalsuite.repository.HolidayRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HolidayRepositoryTest {

    private val api = mockk<HolidayApiService>()
    private val repository = HolidayRepository(api)

    @Test
    fun returnsTheFirstNationalHolidaySkippingRegionalOnes() = runTest {
        coEvery { api.getNextHolidays("CL") } returns listOf(
            HolidayDTO("2027-06-07", "Asalto y Toma del Morro de Arica", "Battle of Arica", false),
            HolidayDTO("2027-06-21", "Día Nacional de los Pueblos Indígenas", "Indigenous Peoples", true)
        )

        val result = repository.getNextHoliday()

        assertTrue(result.isSuccess)
        assertEquals("Día Nacional de los Pueblos Indígenas", result.getOrNull()?.localName)
    }

    @Test
    fun failsWhenThereAreNoNationalHolidays() = runTest {
        coEvery { api.getNextHolidays("CL") } returns listOf(
            HolidayDTO("2027-06-07", "Asalto y Toma del Morro de Arica", "Battle of Arica", false)
        )

        val result = repository.getNextHoliday()

        assertTrue(result.isFailure)
        assertEquals("No hay feriados próximos", result.exceptionOrNull()?.message)
    }

    @Test
    fun failsWhenTheExternalApiFails() = runTest {
        coEvery { api.getNextHolidays("CL") } throws java.io.IOException("sin internet")

        val result = repository.getNextHoliday()

        assertTrue(result.isFailure)
    }
}