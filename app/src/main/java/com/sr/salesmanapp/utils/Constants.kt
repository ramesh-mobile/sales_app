package com.sr.salesmanapp.utils

import com.sr.salesmanapp.data.model.pojo.UsersModel

object Constants {
    const val USER = "User"
    const val USER_MODEL = "UsersModel"
    const val SHOP_MODEL = "ShopModel"
    const val IS_LOGOUT = "IS_LOGOUT"
    const val PLACE_RADIUS = "50000"
    const val LOCATION_ACCURACY_CODE = 1007
    const val DATA = "data"
    const val PLACE_API_KEY = "AIzaSyDXW6G4m09svxv3i7sQ6oltPoTZgxt9okk"
    var IS_LOCATION_UPDATE = false

    ///LocationUpdate and sync to server
    const val secondsToSyncLocationToServer : Long = 90
    const val miliSecondsForLocationUpdate : Long = 2000

}