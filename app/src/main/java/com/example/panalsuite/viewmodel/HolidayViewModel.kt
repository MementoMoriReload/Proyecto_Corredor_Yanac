package com.example.panalsuite.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panalsuite.network.userMessage
import com.example.panalsuite.repository.HolidayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HolidayViewModel : ViewModel() {
    private val repository = HolidayRepository()

    // null = todavía no hay dato (o falló). La pantalla no muestra nada en ese caso.
    private val _holidayText = MutableStateFlow<String?>(null)
    val holidayText: StateFlow<String?> = _holidayText.asStateFlow()

    init {
        loadNextHoliday()
    }

    fun loadNextHoliday() {
        viewModelScope.launch {
            repository.getNextHoliday()
                .onSuccess { holiday ->
                    val date = holiday.date.split("-").reversed().joinToString("-")
                    _holidayText.value = "Próximo feriado: ${holiday.localName} ($date)"
                }
                .onFailure {
                    Log.e("HolidayVM", "No se pudo cargar el feriado: ${it.userMessage()}")
                }
        }
    }
}