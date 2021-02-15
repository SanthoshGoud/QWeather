package com.android.test.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.test.models.Bookmark
import com.android.test.network.IMQAPIService
import com.android.test.network.MQHTTPClient
import com.android.test.network.response.LocationResult
import com.android.test.network.response.WeatherDetails
import com.android.test.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object LocationsRepository {


    fun getLocationData(selectedCity: Bookmark): MutableLiveData<LocationResult> {
        val locationsList : MutableLiveData<LocationResult> = MutableLiveData()
        val result = MQHTTPClient.getHttpClient()!!.create(IMQAPIService::class.java)
            .getTodayLocationData(selectedCity.latitude.toString(), selectedCity.longitude.toString(), Constants.appId)

        result.enqueue(object : Callback<LocationResult> {
            override fun onResponse(call: Call<LocationResult>, response: Response<LocationResult>) {
                if (response.body() != null && (response.code() == 200 || response.code() == 201)) {
                    val dataResult = response.body()
                    locationsList.postValue(dataResult)
                }
            }

            override fun onFailure(call: Call<LocationResult>, t: Throwable) {
                Log.e("Error","Error :: ${t.localizedMessage}" )
            }
        })

        return locationsList

    }


    fun getLocationDataForThisWeek(selectedCity: Bookmark): MutableLiveData<WeatherDetails> {
        val locationsList : MutableLiveData<WeatherDetails> = MutableLiveData()
        val result = MQHTTPClient.getHttpClient()!!.create(IMQAPIService::class.java)
                .getThisWeekLocationData(selectedCity.latitude.toString(), selectedCity.longitude.toString(), Constants.appId)

        result.enqueue(object : Callback<WeatherDetails> {
            override fun onResponse(call: Call<WeatherDetails>, response: Response<WeatherDetails>) {
                if (response.body() != null && (response.code() == 200 || response.code() == 201)) {
                    val dataResult = response.body()
                    locationsList.postValue(dataResult)
                }
            }

            override fun onFailure(call: Call<WeatherDetails>, t: Throwable) {
                Log.e("Error","Error :: ${t.localizedMessage}" )
            }
        })

        return locationsList

    }

}