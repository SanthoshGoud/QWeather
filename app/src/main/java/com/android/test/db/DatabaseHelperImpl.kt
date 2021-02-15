package com.android.test.db

import com.android.test.models.Bookmark

class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DatabaseHelper {

    override suspend fun getBookmarks(): List<Bookmark> = appDatabase.bookmarkDao().loadAll()

    override suspend fun deleteBookmark(bookmark: Bookmark) = appDatabase.bookmarkDao().deleteBookmark(bookmark)



}