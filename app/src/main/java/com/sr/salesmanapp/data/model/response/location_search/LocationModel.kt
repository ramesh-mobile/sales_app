package com.sr.salesmanapp.data.model.response.location_search

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


import java.io.Serializable

/**
 * Created by Abhishek on 10/8/2017.
 */
class LocationModel : Serializable {
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("latitude")
    @Expose
    var latitude: Double? = null
    @SerializedName("longitude")
    @Expose
    var longitude: Double? = null
    @SerializedName("address")
    @Expose
    var address: String? = null
    @SerializedName("altitude")
    @Expose
    var altitude: Double? = null
    @SerializedName("speed")
    @Expose
    var speed: Float? = null
    @SerializedName("bearing")
    @Expose
    var bearing: Float? = null
    @SerializedName("accuracy")
    @Expose
    var accuracy: Float? = null
    @SerializedName("order")
    @Expose
    var order: Int? = null
    @SerializedName("countryCode")
    @Expose
    var countryCode: String? = null
    @SerializedName("markerIcon")
    @Expose
    var markerIcon: Int? = null
    @SerializedName("eta")
    @Expose
    var eta: String? = null
    @SerializedName("city")
    @Expose
    var city: String? = null
    @SerializedName("pin")
    @Expose
    var pin: Int? = null
    @SerializedName("timeStamp")
    @Expose
    var timestamp: Long? = null
    @Expose
    var type: Int? = null
    var infoWindowItems: List<InfoWindowItem>? = null

    var isShowInfoWindow = false
    var isPickup: Boolean = false
    var isDropOff: Boolean = false

    inner class InfoWindowItem {

        var textViewId: Int? = null
        var text: String? = null
    }
}
