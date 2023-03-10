package com.khanhtq.football.data.remote.dto

import com.khanhtq.football.ui.match.MatchEntity
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.MINUTE

data class MatchResp(val matches: MatchList)

data class MatchList(val previous: List<Match>, val upcoming: List<Match>)

data class Match(
    val date: String,
    val description: String,
    val home: String,
    val away: String,
    val winner: String?,
    val highlights: String?
) {
    companion object {
        private val timeFormatter =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        fun toMatchEntity(match: Match): MatchEntity {
            val parsedDate = checkNotNull(timeFormatter.parse(match.date))
            return MatchEntity(
                parsedDate,
                match.description,
                match.home,
                match.away,
                match.winner,
                match.highlights,
            )
        }
    }
}