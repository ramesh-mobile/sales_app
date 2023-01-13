package com.mhv.lymouser.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MatchedSubstring {

    @SerializedName("length")
    @Expose
    var length: Int = 0
    @SerializedName("offset")
    @Expose
    var offset: Int = 0

}
