package com.example.cooljetpackweatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cooljetpackweatherapp.data.WeatherApiClient
import com.example.cooljetpackweatherapp.ui.WeatherUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUIState())
    val uiState: StateFlow<WeatherUIState> = _uiState.asStateFlow()

    fun updateLatitude(lat: Float) {
        _uiState.update { it.copy(latitude = lat) }
    }

    fun updateLongitude(lon: Float) {
        _uiState.update { it.copy(longitude = lon) }
    }

    fun fetchWeather() {
        viewModelScope.launch {
            val data = WeatherApiClient.getWeather(
                _uiState.value.latitude,
                _uiState.value.longitude
            )
            data?.let {
                _uiState.update { state ->
                    state.copy(
                        temperature = it.current_weather.temperature,
                        windspeed = it.current_weather.windspeed,
                        winddirection = it.current_weather.winddirection,
                        weathercode = it.current_weather.weathercode,
                        time = it.current_weather.time,
                        seaLevelPressure = it.hourly.pressure_msl.firstOrNull()?.toFloat() ?: 0f,
                        humidity = it.hourly.relativehumidity_2m.firstOrNull() ?: 0,
                        precipitationProbability = it.hourly.precipitation_probability.firstOrNull() ?: 0
                    )
                }
            }
        }
    }
}