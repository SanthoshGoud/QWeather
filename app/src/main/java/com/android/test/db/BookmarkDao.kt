package com.android.test.db

import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import com.android.test.models.Bookmark

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM Bookmark ORDER BY id")
    suspend fun loadAll(): List<Bookmark>
    @Query("SELECT * FROM Bookmark WHERE id = :bookmarkId")
    suspend fun loadBookmark(bookmarkId: Long): Bookmark
    @Query("SELECT * FROM Bookmark WHERE id = :bookmarkId")
    suspend fun loadLiveBookmark(bookmarkId: Long): Bookmark
    @Insert(onConflict = IGNORE)
    suspend fun insertBookmark(bookmark: Bookmark): Long
    @Update(onConflict = REPLACE)
    suspend fun updateBookmark(bookmark: Bookmark)
    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)
}