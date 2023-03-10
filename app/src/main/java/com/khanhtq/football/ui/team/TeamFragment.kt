package com.khanhtq.football.ui.team

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.khanhtq.football.R
import com.khanhtq.football.common.UiState
import com.khanhtq.football.databinding.FragmentTeamBinding
import com.khanhtq.football.ui.TeamMatchActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TeamFragment : Fragment() {
    private lateinit var binding: FragmentTeamBinding
    private val teamViewModel: TeamViewModel by viewModels()
    private val adapter = TeamAdapter { sharedImageView, team ->
        val optionCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            Pair(sharedImageView, TeamMatchActivity.TRANSITION_NAME)
        )
        val intent = TeamMatchActivity.createIntent(requireContext(), team)
        requireContext().startActivity(intent, optionCompat.toBundle())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
        lifecycleScope.launch {
            teamViewModel.getTeam()
        }
    }

    private fun initViews() {
        binding.listView.adapter = adapter
        @Suppress("DEPRECATION")
        val screenWidth = resources.displayMetrics.widthPixels
        val spanCount = screenWidth / resources.getDimensionPixelSize(R.dimen.logo_image_size) - 2
        binding.listView.layoutManager = GridLayoutManager(context, spanCount)
    }

    private fun initObservers() {
        teamViewModel.teamsLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Loading -> {
                    binding.listView.isVisible = false
                    binding.errorTextView.isVisible = false
                    binding.progressCircular.isVisible = true
                }
                is UiState.Failed -> {
                    binding.progressCircular.isVisible = false
                    binding.listView.isVisible = false
                    binding.errorTextView.isVisible = true
                    binding.errorTextView.text = state.errorMessage
                }
                is UiState.Succeeded -> {
                    binding.progressCircular.isVisible = false
                    binding.errorTextView.isVisible = false
                    binding.listView.isVisible = true
                    adapter.submitList(state.data)
                }
            }
        }
    }
}