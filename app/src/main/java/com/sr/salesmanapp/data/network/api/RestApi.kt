package com.sr.salesmanapp.data.network.api

import com.ae.blazeapp.network.response.locationSearch.GeoCoderResponse
import com.mhv.lymouser.api.response.PlacesResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface RestApi {
    @GET("maps/api/place/queryautocomplete/json")
    fun getPlacesSortByLocation(
        @Query("input") input: String,
        @Query("location") location: String,
        @Query("radius") radius: String,
        @Query("key") key: String,
        @Query("language") language: String
    ): Call<PlacesResponse>

    @GET("maps/api/geocode/json")
    fun getGeoCodeFromAddress(
        @Query("address") address: String?,
        @Query("sensor") sensor: Boolean,
        @Query("key") key: String
    ): Call<GeoCoderResponse>

}