package com.android.test.network

import android.location.Location
import com.android.test.network.response.LocationResult
import com.android.test.network.response.WeatherDetails
import retrofit2.Call
import retrofit2.http.*


interface IMQAPIService{


    @GET("/data/2.5/weather")
    fun getTodayLocationData(
        @Query("lat") lat:String,
        @Query("lon") long : String,
        @Query("appid") appid : String?): Call<LocationResult>

    @GET("/data/2.5/forecast")
    fun getThisWeekLocationData(
            @Query("lat") lat:String,
            @Query("lon") long : String,
            @Query("appid") appid : String?): Call<WeatherDetails>

}
