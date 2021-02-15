package com.android.test.network.response

import com.android.test.models.*
import java.io.Serializable

class LocationResult : Serializable {

    var coord :Coord? = null

    var weather :List<Weather> = ArrayList()

    var base : String = ""

    var main : MainDetails? = null

    var visibility:String = ""

    var wind:Wind? = null

    var clouds : Clouds? = null

    var dt : Long = 0

    var sys:JSystem? = null

    var timezone : Int = 0

    var id : Int = 0

    var name : String = ""

    var code : Int = 0
}