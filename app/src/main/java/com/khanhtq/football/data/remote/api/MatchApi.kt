package com.khanhtq.football.data.remote.api

import com.khanhtq.football.data.remote.dto.MatchResp
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MatchApi {

    @GET("/teams/matches")
    suspend fun getAllMatches(): Call<MatchResp>

    @GET("/teams/{id}/matches")
    suspend fun getMatchesOfTeam(@Path("id") id: String): Call<MatchResp>
}