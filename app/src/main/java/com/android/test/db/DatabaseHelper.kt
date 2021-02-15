package com.android.test.db

import com.android.test.models.Bookmark

interface DatabaseHelper {

    suspend fun getBookmarks(): List<Bookmark>
    suspend fun deleteBookmark(bookmark: Bookmark)


}