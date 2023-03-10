package com.khanhtq.football.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TeamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun importTeamData(teams: List<TeamEntity>)

    @Query("SELECT * FROM team")
    suspend fun getAllTeams(): List<TeamEntity>
}