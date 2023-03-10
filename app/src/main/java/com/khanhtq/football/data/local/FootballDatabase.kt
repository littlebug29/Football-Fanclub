package com.khanhtq.football.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(entities = [TeamEntity::class, MatchReminderEntity::class], version = 1)
@TypeConverters(UUIDConverter::class)
abstract class FootballDatabase: RoomDatabase() {
    abstract fun teamDao(): TeamDao

    abstract fun reminderDao(): ReminderDao
}