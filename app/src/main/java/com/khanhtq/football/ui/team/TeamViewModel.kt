package com.khanhtq.football.ui.team

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.khanhtq.football.common.ApiResult
import com.khanhtq.football.common.UiState
import com.khanhtq.football.data.local.TeamEntity
import com.khanhtq.football.data.repository.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val teamRepository: TeamRepository,
    application: Application
) : AndroidViewModel(application) {
    private val mutableTeamsLiveData = MutableLiveData<UiState<List<TeamEntity>>>()
    val teamsLiveData: LiveData<UiState<List<TeamEntity>>> = mutableTeamsLiveData

    suspend fun getTeam() {
        mutableTeamsLiveData.value = UiState.Loading
        mutableTeamsLiveData.value = when (val result = teamRepository.getTeamList()) {
            is ApiResult.Success -> UiState.Succeeded(result.data)
            is ApiResult.Error -> UiState.Failed(result.errorMessage)
        }
    }
}