package com.khanhtq.football.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addReminder(vararg matchReminderEntities: MatchReminderEntity)

    @Query("SELECT * FROM reminder WHERE matchId = :matchId")
    fun getReminder(matchId: String): List<MatchReminderEntity>
}