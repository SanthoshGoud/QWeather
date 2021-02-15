package com.android.test.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.test.R
import com.android.test.adapter.DeleteClickListener
import com.android.test.adapter.ItemClickListener
import com.android.test.adapter.PlacesBookmarkAdapter
import com.android.test.db.DatabaseBuilder
import com.android.test.db.DatabaseHelperImpl
import com.android.test.map.PlasePickerActivity
import com.android.test.map.MapHelper
import com.android.test.models.Bookmark
import com.android.test.utils.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_bookmark.bookmarks_rv
import kotlinx.android.synthetic.main.activity_bookmarks.*
import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class BookmarkActivity : AppCompatActivity(), ItemClickListener, DeleteClickListener {

    private val locationBookmarkAdapter = PlacesBookmarkAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Test)
        title =  getString(R.string.bookmark_places_title)
        setContentView(R.layout.activity_bookmarks)
        setupToolbar()
        initRecyclerView()

    }

    override fun onResume() {
        super.onResume()
        getBookmarks()
    }

    private fun getBookmarks() {
        CoroutineScope(IO).launch {
            val result = withContext(IO) {
                val dbHelper = DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))
                dbHelper.getBookmarks()
            }
            withContext(Main) {
                setData(result)
            }
        }
    }

    private fun initRecyclerView() {

        bookmarks_rv.apply {
            layoutManager = LinearLayoutManager(this@BookmarkActivity)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecorator)
            adapter = locationBookmarkAdapter
        }

        locationBookmarkAdapter.setItemClickListener(this)
        locationBookmarkAdapter.setDeleteClickListener(this)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun setData(results: List<Bookmark>){
        if(results.isEmpty()){
            noBookmarksLayout.visibility = View.VISIBLE
        }else{
            noBookmarksLayout.visibility = View.GONE
        }

        Collections.reverse(results)
        locationBookmarkAdapter.setBookMarks(results)
        locationBookmarkAdapter.notifyDataSetChanged()

    }

    fun navigateToMapActivity(view: View){
        MapHelper.apiKey = resources.getString(R.string.google_maps_key)
        startActivity(Intent(this@BookmarkActivity ,PlasePickerActivity::class.java))
    }

    override fun onItemClick(view: View?, position: Int) {
        val intent = Intent(this@BookmarkActivity ,WeatherReportActivity::class.java)
        intent.putExtra("selectedCity",locationBookmarkAdapter.getBookMarks()[position])
        startActivity(intent)
    }

    override fun onItemClick(bookmark: Bookmark) {
        val dbHelper = DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))
        CoroutineScope(IO).launch {
            dbHelper.deleteBookmark(bookmark)
            getBookmarks()
        }

    }
}