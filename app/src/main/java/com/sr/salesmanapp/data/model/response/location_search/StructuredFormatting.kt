package com.mhv.lymouser.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class StructuredFormatting:Serializable {

    @SerializedName("main_text")
    @Expose
    var mainText: String? = null


}
