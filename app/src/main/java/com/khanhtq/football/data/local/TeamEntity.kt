package com.khanhtq.football.data.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "team")
@Parcelize
data class TeamEntity(
    @PrimaryKey val id: String,
    val name: String,
    val logo: String
): Parcelable
