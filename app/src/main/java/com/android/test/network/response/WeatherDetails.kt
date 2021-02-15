package com.android.test.network.response

import com.android.test.models.City

class WeatherDetails {

    var cod : String = ""
    var message : Int = 0
    var cnt : Int = 0

    var list : List<WeatherReport>? = null

    var city : City? = null

}