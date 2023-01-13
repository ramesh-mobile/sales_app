package com.sr.salesmanapp.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.ae.blazeapp.network.response.locationSearch.GeoCoderResponse
import com.sr.salesmanapp.data.model.response.location_search.LocationModel
import java.util.*

object LocationUtils {

    fun getAddressFromLatLong(latitude: Double, longitude: Double, context: Context): String {
        var address = ""
        try {
            val addresses: List<Address>
            val geoCoder = Geocoder(context, Locale.getDefault())
            addresses = geoCoder.getFromLocation(latitude, longitude, 1)
            address = addresses[0].getAddressLine(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return address
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun getLocationMode(context: Context): Int {
        return Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)

    }

    fun getLocationFromGeoCoder(result: GeoCoderResponse.Result?): LocationModel {
        val location = LocationModel()
        result?.let {
            location.address = it.formattedAddress
            if (it.formattedAddress?.contains(",") == true) {
                location.name = it.formattedAddress!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            }
            location.latitude = it.geometry!!.location!!.lat
            location.longitude = it.geometry!!.location!!.lng
            for (i in 0 until (it.addressComponents?.size ?: 0)) {
                for (str in it.addressComponents!![i].types!!) {
                    if (str.trim { it <= ' ' }.contains("country")) {
                        location.countryCode = it.addressComponents!![i].shortName
                    } else if (str.trim { it <= ' ' }.contains("locality") || str.trim { it <= ' ' }.contains("administrative_area_level_2")) {
                        location.city = it.addressComponents!![i].shortName
                    }
                }

            }
        }

        return location
    }
}