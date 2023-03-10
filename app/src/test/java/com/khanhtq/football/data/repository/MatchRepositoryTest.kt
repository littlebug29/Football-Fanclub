package com.khanhtq.football.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.khanhtq.football.common.ApiResult
import com.khanhtq.football.common.ErrorType
import com.khanhtq.football.data.local.ReminderDao
import com.khanhtq.football.data.remote.api.MatchApi
import com.khanhtq.football.data.remote.dto.Match
import com.khanhtq.football.data.remote.dto.MatchList
import com.khanhtq.football.data.remote.dto.MatchResp
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.mock.Calls
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MatchRepositoryTest {
    @get:Rule
    val mockRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var mockMatchApi: MatchApi

    @Mock
    private lateinit var mockReminderDao: ReminderDao

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    private val testPreviousMatchList = listOf(
        Match(
            "2022-04-23T18:00:00.000Z",
            "description1",
            "Home1",
            "Away1",
            "Home1",
            "Highlight1"
        ),
        Match(
            "2022-04-24T18:00:00.000Z",
            "description1",
            "Home2",
            "Away2",
            "Home2",
            "Highlight1"
        ),
        Match(
            "2022-04-25T18:00:00.000Z",
            "description1",
            "Home3",
            "Away3",
            "Away3",
            "Highlight1"
        )
    )

    private val testUpcomingMatchList = listOf(
        Match(
            "2023-05-23T18:00:00.000Z",
            "description1",
            "Home1",
            "Away1",
            null,
            null
        ),
        Match(
            "2023-05-24T18:00:00.000Z",
            "description1",
            "Home2",
            "Away2",
            null,
            null
        ),
        Match(
            "2023-05-25T18:00:00.000Z",
            "description1",
            "Home3",
            "Away3",
            null,
            null
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getAllMatches_Succeeded() = runTest {
        val mockMatchResp = MatchResp(MatchList(testPreviousMatchList, testUpcomingMatchList))
        whenever(mockMatchApi.getAllMatches()).thenReturn(Calls.response(mockMatchResp))
        val repo = getRepository()
        val result = repo.getMatches()

        verify(mockMatchApi).getAllMatches()
        assertThat(result, instanceOf(ApiResult.Success::class.java))
        val expectedResultSize = testPreviousMatchList.size + testUpcomingMatchList.size + 2
        assertEquals(expectedResultSize, (result as ApiResult.Success).data.size)
    }

    @Test
    fun getAllMatches_Failed_Network() = runTest {
        whenever(mockMatchApi.getAllMatches()).thenReturn(Calls.failure(IOException()))
        val repo = getRepository()
        val result = repo.getMatches()

        verify(mockMatchApi).getAllMatches()
        assertThat(result, instanceOf(ApiResult.Error::class.java))
        assertEquals(ErrorType.NETWORK_ERROR, (result as ApiResult.Error).errorType)
    }

    @Test
    fun getAllMatches_Failed_ServerNetwork() = runTest {
        val errorBody = "{\"errors\": [\"Unexpected parameter\"]}".toResponseBody()
        val serverError = HttpException(Response.error<MatchResp>(422, errorBody))
        whenever(mockMatchApi.getAllMatches()).thenReturn(Calls.failure(serverError))
        val repo = getRepository()
        val result = repo.getMatches()

        verify(mockMatchApi).getAllMatches()
        assertThat(result, instanceOf(ApiResult.Error::class.java))
        assertEquals(ErrorType.SERVER_ERROR, (result as ApiResult.Error).errorType)
    }

    @Test
    fun getMatchByTeam_Succeeded() = runTest {
        val mockMatchResp = MatchResp(MatchList(testPreviousMatchList, testUpcomingMatchList))
        val teamId = "testId"
        whenever(mockMatchApi.getMatchesOfTeam(teamId)).thenReturn(Calls.response(mockMatchResp))
        val repo = getRepository()
        val result = repo.getMatches(teamId)

        verify(mockMatchApi).getMatchesOfTeam(teamId)
        assertThat(result, instanceOf(ApiResult.Success::class.java))
        val expectedResultSize = testPreviousMatchList.size + testUpcomingMatchList.size + 2
        assertEquals(expectedResultSize, (result as ApiResult.Success).data.size)
    }


    @Test
    fun getMatchByTeam_Failed_Network() = runTest {
        val teamId = "testId"
        whenever(mockMatchApi.getMatchesOfTeam(teamId)).thenReturn(Calls.failure(IOException()))
        val repo = getRepository()
        val result = repo.getMatches(teamId)

        verify(mockMatchApi).getMatchesOfTeam(teamId)
        assertThat(result, instanceOf(ApiResult.Error::class.java))
        assertEquals(ErrorType.NETWORK_ERROR, (result as ApiResult.Error).errorType)
    }

    @Test
    fun getMatchByTeam_Failed_ServerNetwork() = runTest {
        val errorBody = "{\"errors\": [\"Unexpected parameter\"]}".toResponseBody()
        val serverError = HttpException(Response.error<MatchResp>(422, errorBody))
        val teamId = "testId"
        whenever(mockMatchApi.getMatchesOfTeam(teamId)).thenReturn(Calls.failure(serverError))
        val repo = getRepository()
        val result = repo.getMatches(teamId)

        verify(mockMatchApi).getMatchesOfTeam(teamId)
        assertThat(result, instanceOf(ApiResult.Error::class.java))
        assertEquals(ErrorType.SERVER_ERROR, (result as ApiResult.Error).errorType)
    }

    private fun getRepository(): MatchRepository =
        MatchRepository(mockMatchApi, mockReminderDao, testDispatcher)
}