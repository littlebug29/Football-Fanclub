package com.khanhtq.football.data.remote.dto

data class TeamResp(val teams: List<Team>)

data class Team(
    val id: String,
    val name: String,
    val logo: String
)
