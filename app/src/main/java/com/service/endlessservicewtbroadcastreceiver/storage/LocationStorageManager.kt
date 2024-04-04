package com.service.endlessservicewtbroadcastreceiver.storage

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocationStorageManager(context: Context) {
    private val sharedPreferences: SharedPreferences
    private val gson: Gson

    init {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        gson = Gson()
    }

    fun saveLocation(location: Location) {
        var locations = getLocations()
        if (locations == null) {
            locations = mutableListOf()
        }
        locations.add(LocationData(location.latitude, location.longitude))
        val jsonLocations = gson.toJson(locations)
        sharedPreferences.edit().putString(KEY_LOCATIONS, jsonLocations).apply()
    }

    private fun getLocations(): MutableList<LocationData>? {
        val locationsJson = sharedPreferences.getString(KEY_LOCATIONS, null)
        return if (locationsJson != null) {
            gson.fromJson(locationsJson, object : TypeToken<List<LocationData>>() {}.type)
        } else {
            null
        }
    }

    class LocationData(val latitude: Double, val longitude: Double)
    companion object {
        private const val PREF_NAME = "LocationData"
        private const val KEY_LOCATIONS = "locations"
    }
}
