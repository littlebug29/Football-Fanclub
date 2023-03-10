package com.khanhtq.football

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.khanhtq.football.databinding.ActivityMainBinding
import com.khanhtq.football.ui.match.MatchListFragment
import com.khanhtq.football.ui.team.TeamFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.navigationBar.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.action_team -> TeamFragment()
                else -> MatchListFragment()
            }
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.content_layout, fragment)
            }
            return@setOnItemSelectedListener true
        }
        binding.navigationBar.selectedItemId = R.id.action_team
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.navigationBar.selectedItemId = R.id.action_team
    }
}