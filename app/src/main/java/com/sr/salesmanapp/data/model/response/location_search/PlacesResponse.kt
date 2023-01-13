package com.mhv.lymouser.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PlacesResponse {

    @SerializedName("predictions")
    @Expose
    var predictions: List<Prediction>? = null
    @SerializedName("status")
    @Expose
    var status: String? = null

}
