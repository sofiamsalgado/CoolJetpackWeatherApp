package com.example.cooljetpackweatherapp.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.cooljetpackweatherapp.data.FavoriteLocation

@Composable
fun FavoritesBar(
    favorites: List<FavoriteLocation>,
    onFavoriteSelected: (FavoriteLocation) -> Unit,
    onAddFavorite: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var locationName by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = { showDialog = true }) {
            Text(text = "+ Guardar")
        }

        favorites.forEach { location ->
            OutlinedButton(onClick = { onFavoriteSelected(location) }) {
                Text(text = location.name)
            }
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Nome da localização",
                        style = MaterialTheme.typography.titleMedium
                    )
                    OutlinedTextField(
                        value = locationName,
                        onValueChange = { locationName = it },
                        label = { Text("Nome") }
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showDialog = false }) {
                            Text("Cancelar")
                        }
                        Button(onClick = {
                            if (locationName.isNotBlank()) {
                                onAddFavorite(locationName)
                                locationName = ""
                                showDialog = false
                            }
                        }) {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}