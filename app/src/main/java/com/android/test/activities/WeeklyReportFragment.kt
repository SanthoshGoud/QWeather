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
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.test.R
import com.android.test.adapter.WeeklyReportAdapter
import com.android.test.models.Bookmark
import com.android.test.network.response.WeatherDetails
import com.android.test.utils.TopSpacingItemDecoration
import com.android.test.viewmodels.HomeViewModel
import com.android.test.viewmodels.WeeklyReportModel
import kotlinx.android.synthetic.main.weekly_report_layout.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TodayReportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WeeklyReportFragment : Fragment() {

     var selectedCity : Bookmark? = null
    private val weeklyRepo = WeeklyReportAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedCity = arguments?.getSerializable("selectedCity") as Bookmark

        Log.d("Tag ", "selectedCity $selectedCity")

    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val homeViewModel = ViewModelProviders.of(this).get(WeeklyReportModel::class.java)


        homeViewModel.getLocationDataForThisWeek(selectedCity!!).observe(this, Observer {
            val locationResult = it
            Log.i(tag, "Location Result Weekly : ${locationResult.city?.name}")
            setUpUI(locationResult)
        })
        return inflater.inflate(R.layout.weekly_report_layout, container, false)
    }



    private fun initRv() {

        weeklyReportRv.apply {
            layoutManager = LinearLayoutManager(this@WeeklyReportFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecorator)
            adapter = weeklyRepo
        }


    }


    private fun setUpUI(locationResult : WeatherDetails) {
        initRv()
        locationResult.let {
            cityNameTv.text = it.city?.name
            weeklyRepo.setBookMarks(locationResult.list!!)
            weeklyRepo.notifyDataSetChanged()
        }

    }
}