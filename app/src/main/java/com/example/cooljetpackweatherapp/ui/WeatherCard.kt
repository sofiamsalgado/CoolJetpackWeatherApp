package com.example.cooljetpackweatherapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.cooljetpackweatherapp.R

@Composable
fun WeatherCard(
    seaLevelPressure: Float,
    windDirection: Int,
    windSpeed: Float,
    temperature: Float,
    humidity: Int,
    precipitationProbability: Int,
    time: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            WeatherRow(stringResource(R.string.sea_level_pressure), "$seaLevelPressure hPa")
            WeatherRow(stringResource(R.string.wind_direction), "$windDirection°")
            WeatherRow(stringResource(R.string.wind_speed), "$windSpeed km/h")
            WeatherRow(stringResource(R.string.temperature), "$temperature°C")
            WeatherRow(stringResource(R.string.humidity), "$humidity%")
            WeatherRow(stringResource(R.string.precipitation_probability), "$precipitationProbability%")
            WeatherRow(stringResource(R.string.time), time)
        }
    }
}