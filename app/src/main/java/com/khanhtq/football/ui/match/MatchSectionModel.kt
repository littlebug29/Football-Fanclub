package com.khanhtq.football.ui.match

import java.util.*

sealed class MatchSectionModel

data class Section(val name: String) : MatchSectionModel()

data class MatchEntity(
    val date: Date,
    val description: String,
    val home: String,
    val away: String,
    val winner: String?,
    val highlights: String?,
    var isReminderSet: Boolean? = null
) : MatchSectionModel()
