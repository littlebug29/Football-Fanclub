package com.khanhtq.football.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.khanhtq.football.R
import com.khanhtq.football.common.UiState
import com.khanhtq.football.data.local.TeamEntity
import com.khanhtq.football.databinding.ActivityTeamMatchBinding
import com.khanhtq.football.ui.match.MatchAdapter
import com.khanhtq.football.ui.match.MatchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class TeamMatchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeamMatchBinding
    private val matchViewModel: MatchViewModel by viewModels()
    private val matchAdapter = MatchAdapter(
        openHighlight = { highlightUrl ->
            HighlightPlayerActivity.startVideoPlayer(this, highlightUrl)
        },
        setReminder = { matchDate, title, description, isReminderSet ->
            val matchTime = Calendar.getInstance().apply {
                time = matchDate
            }.timeInMillis
            if (isReminderSet) {
                lifecycleScope.launch {
                    matchViewModel.scheduleMatchReminder(matchTime, title, description)
                }
            } else {
                lifecycleScope.launch {
                    matchViewModel.cancelReminder(matchTime, description)
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamMatchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.sharedElementEnterTransition = TransitionInflater.from(this)
            .inflateTransition(R.transition.shared_element_transation)
        binding.logoImageView.transitionName = TRANSITION_NAME
        binding.buttonBack.setOnClickListener { finish() }
        binding.matchListView.layoutManager = LinearLayoutManager(this)
        binding.matchListView.adapter = matchAdapter
        @Suppress("DEPRECATION")
        val teamEntity = intent.getParcelableExtra<TeamEntity>(KEY_TEAM) ?: return
        loadContentIntoView(teamEntity)
        initObserver()
        lifecycleScope.launch {
            matchViewModel.getMatches(teamEntity.id)
        }
    }

    private fun initObserver() {
        matchViewModel.matchListLiveData.observe(this) { state ->
            when (state) {
                UiState.Loading -> {
                    binding.progressCircular.isVisible = true
                    binding.matchListView.isVisible = false
                }
                is UiState.Failed -> {
                    binding.progressCircular.isVisible = false
                    binding.matchListView.isVisible = false
                    Toast.makeText(this, state.errorMessage, Toast.LENGTH_SHORT).show()
                }
                is UiState.Succeeded -> {
                    binding.progressCircular.isVisible = false
                    binding.matchListView.isVisible = true
                    matchAdapter.submitList(state.data)
                }
            }
        }
    }

    private fun loadContentIntoView(team: TeamEntity) {
        Glide.with(this)
            .load(team.logo)
            .transform(CircleCrop(), FitCenter())
            .into(binding.logoImageView)
        binding.nameTextView.text= team.name
    }

    companion object {
        private const val KEY_TEAM = "KEY_TEAM"
        const val TRANSITION_NAME = "team_transition"

        fun createIntent(context: Context, team: TeamEntity): Intent =
            Intent(context, TeamMatchActivity::class.java)
                .putExtra(KEY_TEAM, team)
    }
}