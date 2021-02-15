package com.android.test.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.test.models.Bookmark

@Database(entities = [Bookmark::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao

}