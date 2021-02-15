package com.android.test.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.test.models.Bookmark
import com.android.test.network.response.LocationResult
import com.android.test.network.response.WeatherDetails
import com.android.test.network.response.WeatherReport
import com.android.test.repository.LocationsRepository

class WeeklyReportModel : ViewModel() {

    var mWeekLocationsList: MutableLiveData<WeatherDetails> = MutableLiveData()


    fun getLocationDataForThisWeek(selectedCity: Bookmark): MutableLiveData<WeatherDetails> {
        mWeekLocationsList = LocationsRepository.getLocationDataForThisWeek(selectedCity)
        return mWeekLocationsList
    }

}