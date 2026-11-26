package com.example.panalsuite.repository

import com.example.panalsuite.network.ApiException
import com.example.panalsuite.network.HolidayApiClient
import com.example.panalsuite.network.HolidayApiService
import com.example.panalsuite.network.HolidayDTO

class HolidayRepository(private val api: HolidayApiService = HolidayApiClient.service) {

    // Devuelve el próximo feriado que aplica en chile
    suspend fun getNextHoliday(): Result<HolidayDTO> = runCatching {
        api.getNextHolidays("CL").firstOrNull { it.global }
            ?: throw ApiException("No hay feriados próximos")
    }
}