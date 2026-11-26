package com.example.panalsuite

import com.example.panalsuite.network.HolidayDTO
import com.example.panalsuite.repository.HolidayRepository
import com.example.panalsuite.viewmodel.HolidayViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class HolidayViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<HolidayRepository>()

    @Test
    fun showsTheNextHolidayWithTheDateInChileanFormat() {
        coEvery { repository.getNextHoliday() } returns Result.success(
            HolidayDTO("2026-12-08", "Inmaculada Concepción", "Immaculate Conception", true)
        )

        val viewModel = HolidayViewModel(repository)

        assertEquals(
            "Próximo feriado: Inmaculada Concepción (08-12-2026)",
            viewModel.holidayText.value
        )
    }

    @Test
    fun keepsTheTextNullWhenTheExternalApiFails() {
        coEvery { repository.getNextHoliday() } returns Result.failure(Exception("sin internet"))

        val viewModel = HolidayViewModel(repository)

        assertNull(viewModel.holidayText.value)
    }
}