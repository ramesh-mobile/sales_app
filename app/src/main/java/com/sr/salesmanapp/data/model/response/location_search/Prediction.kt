package com.mhv.lymouser.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Prediction:Serializable {

    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("main_text")
    @Expose
    var main_text: String? = null
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("matched_substrings")
    var matchedSubstrings: List<MatchedSubstring>? = null
    @SerializedName("place_id")
    @Expose
    var placeId: String? = null
    @SerializedName("reference")
    @Expose
    var reference: String? = null
    @SerializedName("structured_formatting")
    var structuredFormatting: StructuredFormatting? = null
    @SerializedName("terms")
    @Expose
    var terms: List<Term>? = null
    @SerializedName("types")
    @Expose
    var types: List<String>? = null
}
