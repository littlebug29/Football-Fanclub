package com.khanhtq.football.ui.team

import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.khanhtq.football.common.ApiResult
import com.khanhtq.football.common.ErrorType
import com.khanhtq.football.common.UiState
import com.khanhtq.football.data.local.TeamEntity
import com.khanhtq.football.data.remote.dto.Team
import com.khanhtq.football.data.repository.TeamRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TeamViewModelTest {

    @get:Rule
    val mockRule: MockitoRule = MockitoJUnit.rule()

    private val mockTeamRepo: TeamRepository = mock()

    @Mock
    lateinit var mockObserver: Observer<UiState<List<TeamEntity>>>

    private val viewModel = TeamViewModel(mockTeamRepo, ApplicationProvider.getApplicationContext())

    private val testTeamList = listOf(
        TeamEntity(
            "testId1",
            "Team1",
            "Logo1"
        ),
        TeamEntity(
            "testId2",
            "Team2",
            "Logo2"
        ),
        TeamEntity(
            "testId3",
            "Team3",
            "Logo3"
        )
    )

    @Before
    fun setUp() {
        viewModel.teamsLiveData.observeForever(mockObserver)
    }

    @Test
    fun getTeam_Success() = runTest {
        whenever(mockTeamRepo.getTeamList()).thenReturn(ApiResult.Success(testTeamList))

        viewModel.getTeam()
        val expectedResult = UiState.Succeeded(testTeamList)
        verify(mockObserver).onChanged(expectedResult)
    }

    @Test
    fun getTeam_NetworkError() = runTest {
        whenever(mockTeamRepo.getTeamList()).thenReturn(
            ApiResult.Error(
                ErrorType.NETWORK_ERROR,
                ErrorType.NETWORK_ERROR.defaultMessage.orEmpty()
            )
        )

        viewModel.getTeam()
        val expectedResult = UiState.Failed(ErrorType.NETWORK_ERROR.defaultMessage!!)
        verify(mockObserver).onChanged(expectedResult)
    }

    @Test
    fun getTeam_ServerError() = runTest {
        whenever(mockTeamRepo.getTeamList()).thenReturn(
            ApiResult.Error(
                ErrorType.SERVER_ERROR,
                "Unexpected parameter"
            )
        )

        viewModel.getTeam()
        val expectedResult = UiState.Failed("Unexpected parameter")
        verify(mockObserver).onChanged(expectedResult)
    }
}