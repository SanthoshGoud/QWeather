package com.android.test.network.response

import com.android.test.models.*

class WeatherReport {

    var dt : String = ""
    var main: Main? = null
    var weather: List<WeatherItem>? = null
    var clouds: Clouds? = null
    var wind: Wind? = null
    var visitor : Int = 0
    var pop : Float = 0.0F
    var sys: Sys? = null
    var dt_txt : String = ""

}