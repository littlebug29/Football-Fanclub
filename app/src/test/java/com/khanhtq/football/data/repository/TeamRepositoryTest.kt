package com.khanhtq.football.data.repository

import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.khanhtq.football.common.ApiResult
import com.khanhtq.football.common.ErrorType
import com.khanhtq.football.data.local.TeamDao
import com.khanhtq.football.data.local.TeamEntity
import com.khanhtq.football.data.remote.api.TeamApi
import com.khanhtq.football.data.remote.dto.MatchResp
import com.khanhtq.football.data.remote.dto.Team
import com.khanhtq.football.data.remote.dto.TeamResp
import com.khanhtq.football.data.repository.TeamRepository.Companion.KEY_LAST_REQUEST_TIME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
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
class TeamRepositoryTest {
    @get:Rule
    val mockRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var mockTeamApi: TeamApi

    @Mock
    private lateinit var mockTeamDao: TeamDao

    @Mock
    private lateinit var mockSharePreference: SharedPreferences

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    private val testTeamList = listOf(
        Team(
            "testId1",
            "Team1",
            "Logo1"
        ),
        Team(
            "testId2",
            "Team2",
            "Logo2"
        ),
        Team(
            "testId3",
            "Team3",
            "Logo3"
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
    fun getTeamList_FirstTime_Success() = runTest {
        val mockResp = TeamResp(testTeamList)
        val mockEditor = mock<SharedPreferences.Editor>()
        whenever(mockTeamApi.getTeams()).thenReturn(Calls.response(mockResp))
        whenever(mockSharePreference.getLong(KEY_LAST_REQUEST_TIME, 0L)).thenReturn(0L)
        whenever(mockSharePreference.edit()).thenReturn(mockEditor)
        whenever(mockEditor.putLong(anyString(), anyLong())).thenReturn(mockEditor)

        val repo = getRepository()
        val result = repo.getTeamList()

        verify(mockTeamApi).getTeams()
        verify(mockTeamDao).importTeamData(anyList())
        verify(mockEditor).putLong(anyString(), anyLong())
        assertThat(result, instanceOf(ApiResult.Success::class.java))
        assertEquals(testTeamList.size, (result as ApiResult.Success).data.size)
    }

    @Test
    fun getTeamList_FirstTime_NetworkError() = runTest {
        val mockEditor = mock<SharedPreferences.Editor>()
        whenever(mockTeamApi.getTeams()).thenReturn(Calls.failure(IOException()))
        whenever(mockSharePreference.getLong(KEY_LAST_REQUEST_TIME, 0L)).thenReturn(0L)
        whenever(mockSharePreference.edit()).thenReturn(mockEditor)

        val repo = getRepository()
        val result = repo.getTeamList()

        verify(mockTeamApi).getTeams()
        assertThat(result, instanceOf(ApiResult.Error::class.java))
        assertEquals(ErrorType.NETWORK_ERROR, (result as ApiResult.Error).errorType)
    }

    @Test
    fun getTeamList_FirstTime_ApiError() = runTest {
        val errorBody = "{\"errors\": [\"Unexpected parameter\"]}".toResponseBody()
        val serverError = HttpException(Response.error<MatchResp>(422, errorBody))
        val mockEditor = mock<SharedPreferences.Editor>()
        whenever(mockTeamApi.getTeams()).thenReturn(Calls.failure(serverError))
        whenever(mockSharePreference.getLong(KEY_LAST_REQUEST_TIME, 0L)).thenReturn(0L)
        whenever(mockSharePreference.edit()).thenReturn(mockEditor)

        val repo = getRepository()
        val result = repo.getTeamList()

        verify(mockTeamApi).getTeams()
        assertThat(result, instanceOf(ApiResult.Error::class.java))
        assertEquals(ErrorType.SERVER_ERROR, (result as ApiResult.Error).errorType)
    }

    @Test
    fun getTeamList_SecondTime_Success() = runTest {
        val testTempEntity = testTeamList.map { TeamEntity(it.id, it.name, it.logo) }
        whenever(mockTeamDao.getAllTeams()).thenReturn(testTempEntity)
        whenever(
            mockSharePreference.getLong(
                KEY_LAST_REQUEST_TIME,
                0L
            )
        ).thenReturn(System.currentTimeMillis() - 6000)
        val mockEditor = mock<SharedPreferences.Editor>()
        whenever(mockSharePreference.edit()).thenReturn(mockEditor)
        whenever(mockEditor.putLong(anyString(), anyLong())).thenReturn(mockEditor)

        val repo = getRepository()
        val result = repo.getTeamList()

        verify(mockTeamDao).getAllTeams()
        assertThat(result, instanceOf(ApiResult.Success::class.java))
        assertEquals(testTempEntity, (result as ApiResult.Success).data)
    }

    private fun getRepository(): TeamRepository =
        TeamRepository(mockTeamApi, mockTeamDao, mockSharePreference, testDispatcher)
}