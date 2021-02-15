package com.android.test.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.android.test.activities.TodayReportFragment
import com.android.test.activities.WeeklyReportFragment
import com.android.test.models.Bookmark

class ViewPagerAdapter (fm: FragmentManager,
                        val bookmark: Bookmark) : androidx.fragment.app.FragmentPagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 ->{
                fragment = TodayReportFragment()
                var bundle = Bundle()
                bundle.putSerializable("selectedCity",bookmark)
                fragment?.arguments = bundle
            }

            1 -> {
                fragment = WeeklyReportFragment()
                var bundle = Bundle()
                bundle.putSerializable("selectedCity",bookmark)
                fragment?.arguments = bundle
            }
        }


        return fragment!!
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "Tab " + (position + 1)
    }
}
