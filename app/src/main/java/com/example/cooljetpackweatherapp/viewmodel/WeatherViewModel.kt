package com.example.cooljetpackweatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cooljetpackweatherapp.data.FavoriteLocation
import com.example.cooljetpackweatherapp.data.FavoritesManager
import com.example.cooljetpackweatherapp.data.WeatherApiClient
import com.example.cooljetpackweatherapp.ui.WeatherUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(WeatherUIState())
    val uiState: StateFlow<WeatherUIState> = _uiState.asStateFlow()

    init {
        loadFavorites()
        fetchWeather()
    }

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
                val timePart = it.current_weather.time.substring(11, 16)
                val sunrisePart = it.daily.sunrise[0].substring(11, 16)
                val sunsetPart = it.daily.sunset[0].substring(11, 16)
                val isDay = timePart in sunrisePart..<sunsetPart

                _uiState.update { state ->
                    state.copy(
                        temperature = it.current_weather.temperature,
                        windspeed = it.current_weather.windspeed,
                        winddirection = it.current_weather.winddirection,
                        weathercode = it.current_weather.weathercode,
                        time = it.current_weather.time,
                        seaLevelPressure = it.hourly.pressure_msl.firstOrNull() ?: 0f,
                        humidity = it.hourly.relativehumidity_2m.firstOrNull() ?: 0,
                        precipitationProbability = it.hourly.precipitation_probability.firstOrNull() ?: 0,
                        day = isDay
                    )
                }
            }
        }
    }

    fun addFavorite(name: String) {
        val location = FavoriteLocation(
            name = name,
            latitude = _uiState.value.latitude,
            longitude = _uiState.value.longitude
        )
        FavoritesManager.addFavorite(getApplication(), location)
        loadFavorites()
    }

    fun selectFavorite(location: FavoriteLocation) {
        _uiState.update {
            it.copy(
                latitude = location.latitude,
                longitude = location.longitude,
                locationName = location.name
            )
        }
        fetchWeather()
    }

    private fun loadFavorites() {
        val list = FavoritesManager.getFavorites(getApplication())
        _uiState.update { it.copy(favorites = list) }
    }
}