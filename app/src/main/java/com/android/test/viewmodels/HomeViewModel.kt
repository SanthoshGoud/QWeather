package com.android.test.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.test.models.Bookmark
import com.android.test.network.response.LocationResult
import com.android.test.network.response.WeatherDetails
import com.android.test.network.response.WeatherReport
import com.android.test.repository.LocationsRepository

class HomeViewModel : ViewModel() {

    private var mLocationsList: MutableLiveData<LocationResult> = MutableLiveData()


    fun getLocationData(selectedCity: Bookmark): MutableLiveData<LocationResult> {
        mLocationsList = LocationsRepository.getLocationData(selectedCity)
        return mLocationsList
    }


}