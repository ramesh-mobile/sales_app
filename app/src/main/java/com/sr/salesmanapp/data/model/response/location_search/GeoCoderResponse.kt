package com.ae.blazeapp.network.response.locationSearch

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

/**
 * Created by Deepesh on 09-Nov-17.
 */

class GeoCoderResponse : Serializable {
    @SerializedName("results")
    @Expose
    var results: List<Result>? = null
    @SerializedName("status")
    @Expose
    var status: String? = null

     class AddressComponent: Serializable {

        @SerializedName("long_name")
        @Expose
        var longName: String? = null
        @SerializedName("short_name")
        @Expose
        var shortName: String? = null
        @SerializedName("types")
        @Expose
        var types: List<String>? = null

    }

     class Bounds: Serializable {

        @SerializedName("northeast")
        @Expose
        var northeast: Northeast? = null
        @SerializedName("southwest")
        @Expose
        var southwest: Southwest? = null

    }

     class Geometry: Serializable {

        @SerializedName("location")
        @Expose
        var location: Location? = null
        @SerializedName("location_type")
        @Expose
        var locationType: String? = null
        @SerializedName("viewport")
        @Expose
        var viewport: Viewport? = null
        @SerializedName("bounds")
        @Expose
        var bounds: Bounds? = null

    }

     class Location: Serializable {

        @SerializedName("lat")
        @Expose
        var lat: Double? = null
        @SerializedName("lng")
        @Expose
        var lng: Double? = null

    }

     class Northeast: Serializable {

        @SerializedName("lat")
        @Expose
        var lat: Double? = null
        @SerializedName("lng")
        @Expose
        var lng: Double? = null

    }

     class Result: Serializable {

        @SerializedName("address_components")
        @Expose
        var addressComponents: List<AddressComponent>? = null
        @SerializedName("formatted_address")
        @Expose
        var formattedAddress: String? = null
        @SerializedName("geometry")
        @Expose
        var geometry: Geometry? = null
        @SerializedName("place_id")
        @Expose
        var placeId: String? = null
        @SerializedName("types")
        @Expose
        var types: List<String>? = null

    }

     class Southwest: Serializable {

        @SerializedName("lat")
        @Expose
        var lat: Double? = null
        @SerializedName("lng")
        @Expose
        var lng: Double? = null

    }

     class Viewport: Serializable {

        @SerializedName("northeast")
        @Expose
        var northeast: Northeast? = null
        @SerializedName("southwest")
        @Expose
        var southwest: Southwest? = null

    }
}
