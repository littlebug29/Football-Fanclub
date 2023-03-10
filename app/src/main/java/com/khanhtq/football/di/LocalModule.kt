package com.khanhtq.football.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.khanhtq.football.data.local.FootballDatabase
import com.khanhtq.football.data.local.ReminderDao
import com.khanhtq.football.data.local.TeamDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): FootballDatabase = Room.databaseBuilder(
        context,
        FootballDatabase::class.java,
        "football_database"
    ).build()

    @Singleton
    @Provides
    fun provideTeamDao(footballDatabase: FootballDatabase): TeamDao = footballDatabase.teamDao()

    @Singleton
    @Provides
    fun provideReminderDao(footballDatabase: FootballDatabase): ReminderDao =
        footballDatabase.reminderDao()

    @Singleton
    @Provides
    fun provideSharePreference(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("cached", Context.MODE_PRIVATE)
}