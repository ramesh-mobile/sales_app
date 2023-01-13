package com.mhv.lymouser.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Term {

    @SerializedName("offset")
    @Expose
    var offset: Int = 0
    @SerializedName("value")
    @Expose
    var value: String? = null

}
