package com.example.cooljetpackweatherapp.data

import android.content.Context
import androidx.core.content.edit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteLocation(
    val name: String,
    val latitude: Float,
    val longitude: Float
)

object FavoritesManager {

    private const val PREFS_NAME = "favorites_prefs"
    private const val KEY_FAVORITES = "favorites_list"

    fun getFavorites(context: Context): List<FavoriteLocation> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_FAVORITES, null) ?: return emptyList()
        return try {
            Json.decodeFromString(json)
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addFavorite(context: Context, location: FavoriteLocation) {
        val list = getFavorites(context).toMutableList()
        if (list.none { it.name == location.name }) {
            list.add(location)
            save(context, list)
        }
    }

    private fun save(context: Context, list: List<FavoriteLocation>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_FAVORITES, Json.encodeToString(list)) }
    }
}