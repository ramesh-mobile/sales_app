package com.sr.salesmanapp.utils

import android.content.Context
import android.location.Location
import android.preference.PreferenceManager
import com.sr.salesmanapp.utils.GpsUtils
import com.sr.salesmanapp.R
import com.google.android.gms.maps.model.LatLng
import java.text.DateFormat
import java.util.*

/**
 * Created by ramesh on 09-09-20
 */
object GpsUtils {
    const val KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates"

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The [Context].
     */
    fun requestingLocationUpdates(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false)
    }

    /**
     * Stores the location updates state in SharedPreferences.
     * @param requestingLocationUpdates The location updates state.
     */
    fun setRequestingLocationUpdates(context: Context?, requestingLocationUpdates: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
            .apply()
    }

    /**
     * Returns the `location` object as a human readable string.
     * @param location  The [Location].
     */
    fun getLocationText(location: Location?): String {
        return if (location == null) "Unknown location" else "(" + location.latitude + ", " + location.longitude + ")"
    }

    fun getLocationTitle(context: Context): String {
        return context.getString(
            R.string.location_updated, DateFormat.getDateTimeInstance().format(
                Date()
            )
        )
    }

    fun isInRadius(location: Location, latLng: LatLng, geofenceRadius: Float): Boolean {
        val x = location.latitude
        val y = location.longitude
        // the lat and long of : Webel-It Park
        val X = latLng.latitude
        val Y = latLng.longitude

        // radius up to 200 m is checked
        return (getDistance(X, Y, x, y) <= geofenceRadius)
    }

    fun isInRadius(lat: Double, lng: Double, latLng: LatLng, geofenceRadius: Float): Boolean {
        // the lat and long of : Webel-It Park
        val X = latLng.latitude
        val Y = latLng.longitude

        // radius up to 200 m is checked
        return (getDistance(X, Y, lat, lng) <= geofenceRadius)
    }

    // function to find distance between two latitude and longitude
    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Radius of the earth
        val latDistance = Math.toRadians(Math.abs(lat2 - lat1))
        val lonDistance = Math.toRadians(Math.abs(lon2 - lon1))
        val a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + (Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)))
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        var distance = R * c * 1000 // distance in meter
        distance = Math.pow(distance, 2.0)
        return Math.sqrt(distance)
    }
}