package com.sr.salesmanapp.data.model.pojo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ShopModelResponse constructor(
  val key: String? = null,
  var shopModel: ShopModel? = null
) :Parcelable

@Parcelize
data class ShopModel(
  var shopId :String? = null,
  var shopName : String? = null,
  var ownerName : String? = null,
  var contact_one : String? = null,
  var contact_two : String? = null,
  var address : String? = null,
  var lat : String? = null,
  var lng : String? = null,
  var email : String? = null

) : Parcelable