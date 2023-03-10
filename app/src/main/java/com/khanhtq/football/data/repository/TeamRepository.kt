package com.khanhtq.football.data.repository

import android.content.SharedPreferences
import com.khanhtq.football.common.ApiResult
import com.khanhtq.football.common.toError
import com.khanhtq.football.data.local.TeamDao
import com.khanhtq.football.data.local.TeamEntity
import com.khanhtq.football.data.remote.api.TeamApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import javax.inject.Inject

class TeamRepository @Inject constructor(
    private val teamApi: TeamApi,
    private val teamDao: TeamDao,
    private val sharedPreferences: SharedPreferences,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getTeamList(): ApiResult<List<TeamEntity>> = withContext(dispatcher) {
        val lastRequestTime =
            sharedPreferences.getLong(KEY_LAST_REQUEST_TIME, 0L)
        val shouldUpdateFromServer =
            (System.currentTimeMillis() - lastRequestTime) >= TEAM_CACHE_PERIOD
        try {
            val teams = if (shouldUpdateFromServer) {
                teamApi.getTeams()
                    .await()
                    .teams
                    .map { TeamEntity(it.id, it.name, it.logo) }
                    .also {
                        teamDao.importTeamData(it)
                        with(sharedPreferences.edit()) {
                            putLong(KEY_LAST_REQUEST_TIME, System.currentTimeMillis())
                            apply()
                        }
                    }
            } else {
                teamDao.getAllTeams()
            }
            return@withContext ApiResult.Success(teams)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext e.toError()
        }
    }

    companion object {
        const val KEY_LAST_REQUEST_TIME = "KEY_LAST_REQUEST_TIME"
        const val TEAM_CACHE_PERIOD = 30 * 24 * 60 * 60 * 1000L
    }
}