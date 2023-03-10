package com.khanhtq.football.ui.match

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.khanhtq.football.common.ApiResult
import com.khanhtq.football.common.IdGenerator
import com.khanhtq.football.common.UiState
import com.khanhtq.football.data.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    application: Application
): AndroidViewModel(application) {
    private val workManager: WorkManager by lazy { WorkManager.getInstance(application) }

    private val mutableMatchListLiveData = MutableLiveData<UiState<List<MatchSectionModel>>>()
    val matchListLiveData: LiveData<UiState<List<MatchSectionModel>>> = mutableMatchListLiveData

    suspend fun getMatches(teamId: String? = null) {
        mutableMatchListLiveData.value = UiState.Loading
        mutableMatchListLiveData.value = when(val result = matchRepository.getMatches(teamId)) {
            is ApiResult.Success -> UiState.Succeeded(result.data)
            is ApiResult.Error -> UiState.Failed(result.errorMessage)
        }
    }

    suspend fun scheduleMatchReminder(matchTime: Long, title: String, description: String) {
        val delay = calculateDelayTime(matchTime)
        val data = Data.Builder()
            .putString(MatchReminderWorker.KEY_MATCH_TITLE, title)
            .putString(MatchReminderWorker.KEY_MATCH_DESC, description)
            .build()
        val matchReminderRequest = OneTimeWorkRequest.Builder(MatchReminderWorker::class.java)
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        val requestId = matchReminderRequest.id
        workManager.enqueue(matchReminderRequest)
        val matchId = IdGenerator.generateMatchId(matchTime, description) ?: return
        matchRepository.saveMatchReminder(matchId, requestId)
    }

    suspend fun cancelReminder(matchTime: Long, description: String) {
        val matchId = IdGenerator.generateMatchId(matchTime, description) ?: return
        val requestId = matchRepository.getReminderRequestId(matchId) ?: return
        workManager.cancelWorkById(requestId)
    }

    private fun calculateDelayTime(matchTime: Long): Long {
        val currentTime = System.currentTimeMillis()
        val timeUntilMatchStart = matchTime - currentTime
        val delay = timeUntilMatchStart - PERIOD_OF_NOTIFY_TIME_TO_START_TIME
        return if (delay > 0) delay else 0
    }

    companion object {
        const val PERIOD_OF_NOTIFY_TIME_TO_START_TIME = 15 * 60 * 1000
    }
}