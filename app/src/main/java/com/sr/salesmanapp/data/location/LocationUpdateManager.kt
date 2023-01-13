package com.sr.salesmanapp.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*

object LocationUpdateManager {
    private var locationRequest: LocationRequest? = null
    private val UPDATE_INTERVAL = (10 * 1000).toLong()
    private val FASTEST_INTERVAL: Long = 2000
    var locationUpdateListener: LocationUpdateListener? = null
    private var latestLocation: Location? = null


    @SuppressLint("MissingPermission")
    fun getLocation(context: Context, locationUpdateListener: LocationUpdateListener?) {
        this.locationUpdateListener = locationUpdateListener
        locationRequest = LocationRequest()
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest?.interval = UPDATE_INTERVAL
        locationRequest?.fastestInterval = FASTEST_INTERVAL
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest!!)
        val locationSettingsRequest = builder.build()
        val settingsClient = LocationServices.getSettingsClient(context)
        settingsClient.checkLocationSettings(locationSettingsRequest)
        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(
            locationRequest, locationCallBack,
            Looper.myLooper()
        )
    }



    @SuppressLint("MissingPermission")
    fun getLastLocation(context: Context, locationUpdateListener: LocationUpdateListener?) {
        val locationClient = LocationServices.getFusedLocationProviderClient(context)
        locationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    latestLocation = location
                    latestLocation?.let { latestLocation ->
                        //                            val locationString = Gson().toJson(latestLocation)
//                            DPPreferences.saveLocation(context, locationString)
                        locationUpdateListener?.let {
                            it.locationUpdate(latestLocation)
                        }

                    }
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    fun getUpdatedLocation(): Location? {
        return latestLocation
    }

    private val locationCallBack = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            latestLocation = locationResult.lastLocation
            locationUpdateListener?.locationUpdate(locationResult.lastLocation)
        }
    }

    fun removeLocationUpdate(context: Context) {
        LocationServices.getFusedLocationProviderClient(context)
            .removeLocationUpdates(locationCallBack)
    }

    interface LocationUpdateListener {
        fun locationUpdate(location: Location)
    }
}