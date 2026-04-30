package com.example.cooljetpackweatherapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.cooljetpackweatherapp.ui.theme.CoolJetpackWeatherAppTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

class LocationPickerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoolJetpackWeatherAppTheme {
                val lisbon = LatLng(38.7223, -9.1393)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(lisbon, 5f)
                }

                GoogleMap(
                    cameraPositionState = cameraPositionState,
                    onMapClick = { latLng ->
                        val result = Intent()
                        result.putExtra("latitude", latLng.latitude.toFloat())
                        result.putExtra("longitude", latLng.longitude.toFloat())
                        setResult(Activity.RESULT_OK, result)
                        finish()
                    }
                )
            }
        }
    }
}