package com.khanhtq.football.ui.match

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.khanhtq.football.common.UiState
import com.khanhtq.football.databinding.FragmentMatchBinding
import com.khanhtq.football.ui.HighlightPlayerActivity
import com.khanhtq.football.ui.team.TeamViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MatchListFragment : Fragment() {
    private lateinit var binding: FragmentMatchBinding
    private val teamViewModel: TeamViewModel by viewModels()
    private val matchViewModel: MatchViewModel by viewModels()
    private val teamAdapter = SelectableTeamAdapter(
        filterMatchOfTeamBy = { teamId ->
            lifecycleScope.launch {
                matchViewModel.getMatches(teamId)
            }
        },
        clearFilter = {
            lifecycleScope.launch {
                matchViewModel.getMatches()
            }
        }
    )
    private val matchAdapter = MatchAdapter(
        openHighlight = { highlightUrl ->
            HighlightPlayerActivity.startVideoPlayer(requireContext(), highlightUrl)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()

        lifecycleScope.launch {
            teamViewModel.getTeam()
            matchViewModel.getMatches()
        }
    }

    private fun initViews() {
        binding.teamListView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.teamListView.adapter = teamAdapter

        binding.matchListView.layoutManager =
            LinearLayoutManager(context)
        binding.matchListView.adapter = matchAdapter
    }

    private fun initObservers() {
        teamViewModel.teamsLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Loading -> {
                    binding.progressCircular.isVisible = true
                    binding.teamListView.isVisible = false
                }
                is UiState.Failed -> {
                    binding.progressCircular.isVisible = false
                    binding.teamListView.isVisible = false
                    Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
                }
                is UiState.Succeeded -> {
                    binding.progressCircular.isVisible = false
                    binding.teamListView.isVisible = true
                    teamAdapter.submitList(state.data)
                }
            }
        }

        matchViewModel.matchListLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Loading -> {
                    binding.matchProgressCircular.isVisible = true
                    binding.matchListView.isVisible = false
                }
                is UiState.Failed -> {
                    binding.matchProgressCircular.isVisible = false
                    binding.matchListView.isVisible = false
                    Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
                }
                is UiState.Succeeded -> {
                    binding.matchProgressCircular.isVisible = false
                    binding.matchListView.isVisible = true
                    matchAdapter.submitList(state.data)
                }
            }
        }
    }
}