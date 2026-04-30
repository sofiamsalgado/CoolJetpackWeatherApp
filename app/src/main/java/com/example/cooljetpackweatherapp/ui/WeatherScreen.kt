package com.example.cooljetpackweatherapp.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cooljetpackweatherapp.R
import com.example.cooljetpackweatherapp.data.FavoriteLocation
import com.example.cooljetpackweatherapp.data.WMO_WeatherCode
import com.example.cooljetpackweatherapp.data.getWeatherCodeMap
import com.example.cooljetpackweatherapp.viewmodel.WeatherViewModel

@Composable
fun WeatherUI(weatherViewModel: WeatherViewModel = viewModel()) {
    val weatherUIState by weatherViewModel.uiState.collectAsState()

    val latitude = weatherUIState.latitude
    val longitude = weatherUIState.longitude
    val temperature = weatherUIState.temperature
    val windSpeed = weatherUIState.windspeed
    val windDirection = weatherUIState.winddirection
    val weathercode = weatherUIState.weathercode
    val seaLevelPressure = weatherUIState.seaLevelPressure
    val humidity = weatherUIState.humidity
    val precipitationProbability = weatherUIState.precipitationProbability
    val time = weatherUIState.time
    val favorites = weatherUIState.favorites
    val day = weatherUIState.day

    val configuration = LocalConfiguration.current
    val context = LocalContext.current

    val bgRes = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            if (day) R.drawable.sunny_bg_land else R.drawable.night_bg_land
        }
        else -> {
            if (day) R.drawable.sunny_bg else R.drawable.night_bg
        }
    }

    val mapt = getWeatherCodeMap()
    val wImage = when (val wCode = mapt[weathercode]) {
        WMO_WeatherCode.CLEAR_SKY,
        WMO_WeatherCode.MAINLY_CLEAR,
        WMO_WeatherCode.PARTLY_CLOUDY -> if (day) wCode.image + "day"
        else wCode.image + "night"
        else -> wCode?.image
    }

    val wIcon = context.resources.getIdentifier(wImage, "drawable", context.packageName)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LandscapeWeatherUI(
                wIcon,
                latitude,
                longitude,
                temperature,
                windSpeed,
                windDirection,
                seaLevelPressure,
                humidity,
                precipitationProbability,
                time,
                favorites = favorites,
                onLatitudeChange = {
                        newValue -> newValue.toFloatOrNull()?.let {
                    weatherViewModel.updateLatitude(it) }
                },
                onLongitudeChange = {
                        newValue -> newValue.toFloatOrNull()?.let {
                    weatherViewModel.updateLongitude(it) }
                },
                onUpdateButtonClick = {
                    weatherViewModel.fetchWeather()
                },
                onLocationPicked = { lat, lon ->
                    weatherViewModel.updateLatitude(lat)
                    weatherViewModel.updateLongitude(lon)
                    weatherViewModel.fetchWeather()
                },
                onFavoriteSelected = { weatherViewModel.selectFavorite(it) },
                onAddFavorite = { weatherViewModel.addFavorite(it) }
            )
        } else {
            PortraitWeatherUI(
                wIcon,
                latitude,
                longitude,
                temperature,
                windSpeed,
                windDirection,
                seaLevelPressure,
                humidity,
                precipitationProbability,
                time,
                favorites = favorites,
                onLatitudeChange = {
                        newValue ->
                    newValue.toFloatOrNull()?.let {
                        weatherViewModel.updateLatitude(it) }
                },
                onLongitudeChange = {
                        newValue ->
                    newValue.toFloatOrNull()?.let {
                        weatherViewModel.updateLongitude(it) }
                },
                onUpdateButtonClick = {
                    weatherViewModel.fetchWeather()
                },
                onLocationPicked = { lat, lon ->
                    weatherViewModel.updateLatitude(lat)
                    weatherViewModel.updateLongitude(lon)
                    weatherViewModel.fetchWeather()
                },
                onFavoriteSelected = { weatherViewModel.selectFavorite(it) },
                onAddFavorite = { weatherViewModel.addFavorite(it) }
            )
        }
    }
}

@Composable
fun PortraitWeatherUI(
    wIcon: Int,
    latitude: Float,
    longitude: Float,
    temperature: Float,
    windSpeed: Float,
    windDirection: Int,
    seaLevelPressure: Float,
    humidity: Int,
    precipitationProbability: Int,
    time: String,
    favorites: List<FavoriteLocation>,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit,
    onLocationPicked: (Float, Float) -> Unit,
    onFavoriteSelected: (FavoriteLocation) -> Unit,
    onAddFavorite: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (wIcon != 0) {
            Image(
                painter = painterResource(id = wIcon),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .padding(bottom = 16.dp)
            )
        }

        FavoritesBar(
            favorites = favorites,
            onFavoriteSelected = onFavoriteSelected,
            onAddFavorite = onAddFavorite
        )

        Spacer(modifier = Modifier.height(8.dp))

        CoordinatesCard(
            latitude = latitude,
            longitude = longitude,
            onLatitudeChange = onLatitudeChange,
            onLongitudeChange = onLongitudeChange,
            onLocationPicked = onLocationPicked
        )

        Spacer(modifier = Modifier.height(16.dp))

        WeatherCard(
            seaLevelPressure = seaLevelPressure,
            windDirection = windDirection,
            windSpeed = windSpeed,
            temperature = temperature,
            humidity = humidity,
            precipitationProbability = precipitationProbability,
            time = time
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onUpdateButtonClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.update_weather))
        }
    }
}

// ToDo
@Composable
fun LandscapeWeatherUI(
    wIcon: Int,
    latitude: Float,
    longitude: Float,
    temperature: Float,
    windSpeed: Float,
    windDirection: Int,
    seaLevelPressure: Float,
    humidity: Int,
    precipitationProbability: Int,
    time: String,
    favorites: List<FavoriteLocation>,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit,
    onLocationPicked: (Float, Float) -> Unit,
    onFavoriteSelected: (FavoriteLocation) -> Unit,
    onAddFavorite: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (wIcon != 0) {
            Image(
                painter = painterResource(id = wIcon),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(0.8f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FavoritesBar(
                favorites = favorites,
                onFavoriteSelected = onFavoriteSelected,
                onAddFavorite = onAddFavorite
            )

            CoordinatesCard(
                latitude = latitude,
                longitude = longitude,
                onLatitudeChange = onLatitudeChange,
                onLongitudeChange = onLongitudeChange,
                onLocationPicked = onLocationPicked
            )

            Button(
                onClick = onUpdateButtonClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.update_weather))
            }
        }

        WeatherCard(
            seaLevelPressure = seaLevelPressure,
            windDirection = windDirection,
            windSpeed = windSpeed,
            temperature = temperature,
            humidity = humidity,
            precipitationProbability = precipitationProbability,
            time = time,
            modifier = Modifier.weight(1.2f)
        )
    }
}