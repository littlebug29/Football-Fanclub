package com.khanhtq.football.data.remote.api

import com.khanhtq.football.data.remote.dto.TeamResp
import retrofit2.Call
import retrofit2.http.GET

interface TeamApi {
    @GET("/teams")
    suspend fun getTeams(): Call<TeamResp>
}