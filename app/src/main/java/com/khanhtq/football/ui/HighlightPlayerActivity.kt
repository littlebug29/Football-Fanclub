package com.khanhtq.football.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.khanhtq.football.databinding.ActivityHighlightBinding

class HighlightPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHighlightBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpVideoView(binding.videoView)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val url = intent.getStringExtra(KEY_URL)
        binding.videoView.setVideoURI(Uri.parse(url))
        binding.videoView.start()
    }

    private fun setUpVideoView(videoView: VideoView) {
        val mediaController = MediaController(this)
        mediaController.setMediaPlayer(videoView)
        videoView.setMediaController(mediaController)
        videoView.setOnCompletionListener { finish() }
        videoView.setOnErrorListener { mp, what, extra ->
            Toast.makeText(this, "Error playing video", Toast.LENGTH_SHORT).show()
            false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val KEY_URL = "KEY_URL"

        fun startVideoPlayer(context: Context, videoUrl: String) {
            val intent = Intent(context, HighlightPlayerActivity::class.java)
                .putExtra(KEY_URL, videoUrl)
            context.startActivity(intent)
        }
    }
}