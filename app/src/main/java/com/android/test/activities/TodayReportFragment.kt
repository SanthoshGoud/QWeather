package com.android.test.activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.test.R
import com.android.test.models.Bookmark
import com.android.test.network.response.LocationResult
import com.android.test.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.fragment_today_report.*
import java.text.DecimalFormat


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TodayReportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TodayReportFragment : Fragment() {

     var selectedCity : Bookmark? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedCity = arguments?.getSerializable("selectedCity") as Bookmark

        Log.d("Tag ", "selectedCity $selectedCity")

    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        homeViewModel.getLocationData(selectedCity!!).observe(this, Observer {
            val locationResult = it
            Log.i(tag, "Location Result : ${locationResult.toString()}")
            setUpUI(locationResult)
        })
        return inflater.inflate(R.layout.fragment_today_report, container, false)
    }



    private fun setUpUI(locationResult : LocationResult) {
        locationResult.let {

            val kelvin: Float? = locationResult.main?.temp
            val celsius = kelvin?.minus(273.15f)
            val df = DecimalFormat("#.##")
            df.format(celsius)
            cityNameTv.text = locationResult.name
            todayTemp.text =  df.format(celsius).toString() +"\u2103"

            locationResult.weather.let {
                todayWeather.text = locationResult.weather?.get(0).main
            }


            locationResult.main?.let {
                humidityTv.text = it.humidity.toString() +" %"
                pressureTv.text = it.pressure.toString() +" "
            }

            locationResult.wind?.let {
                windSppedTv.text = it.speed.toString()
                windDirectionTv.text = it.deg.toString()
            }
        }

    }
}