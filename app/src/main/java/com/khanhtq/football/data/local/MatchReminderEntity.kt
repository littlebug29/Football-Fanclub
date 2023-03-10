package com.khanhtq.football.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "reminder")
data class MatchReminderEntity(
    @PrimaryKey val matchId: String,
    val requestId: UUID
)