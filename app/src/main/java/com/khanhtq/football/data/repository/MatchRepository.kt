package com.khanhtq.football.data.repository

import com.khanhtq.football.common.ApiResult
import com.khanhtq.football.common.toError
import com.khanhtq.football.data.local.MatchReminderEntity
import com.khanhtq.football.data.local.ReminderDao
import com.khanhtq.football.data.remote.api.MatchApi
import com.khanhtq.football.data.remote.dto.Match
import com.khanhtq.football.data.remote.dto.MatchList
import com.khanhtq.football.ui.match.MatchEntity
import com.khanhtq.football.ui.match.MatchSectionModel
import com.khanhtq.football.ui.match.Section
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MatchRepository @Inject constructor(
    private val matchApi: MatchApi,
    private val reminderDao: ReminderDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getMatches(teamId: String? = null): ApiResult<List<MatchSectionModel>> =
        withContext(dispatcher) {
            try {
                val matchList = if (teamId == null) {
                    matchApi.getAllMatches().await().matches
                } else {
                    matchApi.getMatchesOfTeam(teamId).await().matches
                }
                val previousMatches = matchList.previous.map { Match.toMatchEntity(it) }
                val upcomingMatches = matchList.upcoming.map { Match.toMatchEntity(it) }
                val matchSections = mutableListOf<MatchSectionModel>()
                matchSections.add(Section("Previous"))
                matchSections.addAll(previousMatches)
                matchSections.add(Section("Upcoming"))
                matchSections.addAll(upcomingMatches)
                return@withContext ApiResult.Success(matchSections)
            } catch (e: Exception) {
                return@withContext e.toError()
            }
        }

    suspend fun saveMatchReminder(matchId: String, requestId: UUID) = withContext(dispatcher) {
        reminderDao.addReminder(MatchReminderEntity(matchId, requestId))
    }

    suspend fun getReminderRequestId(matchId: String): UUID? = withContext(dispatcher) {
        reminderDao.getReminder(matchId).firstOrNull()?.requestId
    }
}