package com.android.test.adapter

import android.view.View

interface ItemClickListener {

    fun onItemClick(view: View?, position: Int)
}