package com.android.test.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent


class UnswipeableViewPager: androidx.viewpager.widget.ViewPager {
    private var isSwipeEnabled: Boolean = false

    constructor(context: Context): super(context) {
        this.isSwipeEnabled = true
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.isSwipeEnabled = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (isSwipeEnabled) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (isSwipeEnabled) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun enableSwipe(enabled: Boolean) {
        isSwipeEnabled = enabled
    }

}