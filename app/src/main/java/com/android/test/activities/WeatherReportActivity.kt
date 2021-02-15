package com.android.test.activities

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.android.test.R
import com.android.test.adapter.ViewPagerAdapter
import com.android.test.models.Bookmark
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_weather_report.*
import kotlinx.android.synthetic.main.main_view_maps.*

class WeatherReportActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private  val tag = WeatherReportActivity::class.java.name
    lateinit var  bookmark:Bookmark

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_report)
        bookmark = intent.getSerializableExtra("selectedCity") as Bookmark
        setupToolbar()
        setupNavigationView()

    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        title = "Today"
    }

    private fun setupNavigationView() {
        viewpager.enableSwipe(false)
        viewpager.offscreenPageLimit = 1
        bottom_navigation.setOnNavigationItemSelectedListener(this@WeatherReportActivity)
        setViewPagerLisetners()
        setupViewPager()

    }


    private fun selectFragment(item: MenuItem) {

        item.isChecked = true

        when (item.itemId) {
            R.id.action_today -> {
                viewpager.currentItem = 0
            }

            R.id.action_week -> {
                viewpager.currentItem = 1
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        selectFragment(item)
       return false
    }


    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager,bookmark)
        viewpager.adapter = adapter
    }


    private fun setViewPagerLisetners() {
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
            @TargetApi(Build.VERSION_CODES.CUPCAKE)
            override fun onPageSelected(position: Int) {
                title = if (position == 0) {
                    "Today"
                } else {
                    "This Week"
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }


//    private fun initRecyclerView() {
//        location_bookmark_rv.apply {
//            layoutManager = LinearLayoutManager(this@HomeActivity)
//            val topSpacingDecorator = TopSpacingItemDecoration(30)
//            addItemDecoration(topSpacingDecorator)
//            val locationBookmarkAdapter = LocationBookmarkAdapter()
//            adapter = locationBookmarkAdapter
//        }
//
//    }
}