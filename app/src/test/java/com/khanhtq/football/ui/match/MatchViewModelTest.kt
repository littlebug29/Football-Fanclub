package com.khanhtq.football.ui.match

import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.khanhtq.football.common.ApiResult
import com.khanhtq.football.common.UiState
import com.khanhtq.football.data.remote.dto.Match
import com.khanhtq.football.data.repository.MatchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Calendar

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MatchViewModelTest {

    @get:Rule
    val mockRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var mockObserver: Observer<UiState<List<MatchSectionModel>>>

    private val mockMatchRepo: MatchRepository = mock()
    private val matchViewModel =
        MatchViewModel(mockMatchRepo, ApplicationProvider.getApplicationContext())

    private val testPreviousMatchList = listOf(
        MatchEntity(
            Calendar.getInstance().time,
            "description1",
            "Home1",
            "Away1",
            "Home1",
            "Highlight1"
        ),
        MatchEntity(
            Calendar.getInstance().time,
            "description2",
            "Home2",
            "Away2",
            "Home2",
            "Highlight1"
        ),
        MatchEntity(
            Calendar.getInstance().time,
            "description3",
            "Home3",
            "Away3",
            "Away3",
            "Highlight1"
        )
    )

    private val testUpcomingMatchList = listOf(
        MatchEntity(
            Calendar.getInstance().time,
            "description1",
            "Home1",
            "Away1",
            null,
            null
        ),
        MatchEntity(
            Calendar.getInstance().time,
            "description2",
            "Home2",
            "Away2",
            null,
            null
        ),
        MatchEntity(
            Calendar.getInstance().time,
            "description3",
            "Home3",
            "Away3",
            null,
            null
        )
    )

    @Before
    fun setUp() {
        matchViewModel.matchListLiveData.observeForever(mockObserver)
    }

    @Test
    fun getAllMatches_Success() = runTest {
        val mockMatchSection =
            listOf<MatchSectionModel>(Section("Previous")) + testPreviousMatchList + Section("Upcoming") + testUpcomingMatchList
        whenever(mockMatchRepo.getMatches(null)).thenReturn(
            ApiResult.Success(
                mockMatchSection
            )
        )

        matchViewModel.getMatches(null)
        verify(mockObserver).onChanged(UiState.Succeeded(mockMatchSection))
    }

    @Test
    fun getTeamMatches_Success() = runTest {
        val mockMatchSection =
            listOf<MatchSectionModel>(Section("Previous")) + testPreviousMatchList + Section("Upcoming") + testUpcomingMatchList
        whenever(mockMatchRepo.getMatches("teamId")).thenReturn(
            ApiResult.Success(
                mockMatchSection
            )
        )

        matchViewModel.getMatches("teamId")
        verify(mockObserver).onChanged(UiState.Succeeded(mockMatchSection))
    }
}