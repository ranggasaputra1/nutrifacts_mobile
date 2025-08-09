package com.nutrifacts.app.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nutrifacts.app.data.local.entity.History
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert
    fun insert(history: History)

    @Insert
    fun insertAll(vararg history: History)

    @Delete
    fun delete(history: History)

    @Query("SELECT * from history WHERE user_id = :user_id ORDER BY dateAdded ASC")
    fun getAllHistory(user_id:Int): Flow<List<History>>
}