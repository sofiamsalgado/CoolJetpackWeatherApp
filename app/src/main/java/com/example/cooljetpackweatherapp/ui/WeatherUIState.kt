package com.example.cooljetpackweatherapp.ui

import com.example.cooljetpackweatherapp.data.FavoriteLocation

data class WeatherUIState(
    val latitude: Float = 38.7223f,
    val longitude: Float = 9.1393f,
    val temperature: Float = 0f,
    val windspeed: Float = 0f,
    val winddirection: Int = 0,
    val weathercode: Int = 0,
    val seaLevelPressure: Float = 0f,
    val humidity: Int = 0,
    val precipitationProbability: Int = 0,
    val time: String = "",
    val locationName: String = "",
    val favorites: List<FavoriteLocation> = emptyList(),
    val day: Boolean = true
)