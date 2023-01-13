package com.sr.salesmanapp

import android.app.Application
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.maps.model.LatLng
import com.pixplicity.easyprefs.library.Prefs
import com.sr.salesmanapp.data.model.response.location_search.LocationModel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SalesManApplication : Application(){

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        initializePref()
    }

    private fun initializePref() {
        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }

    companion object {
        var lastSelectedLocation: LocationModel? = null
        var lastAddressLatLog: LatLng? = null
    }
}