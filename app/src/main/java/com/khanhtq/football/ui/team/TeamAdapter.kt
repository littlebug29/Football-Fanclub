package com.khanhtq.football.ui.team

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.khanhtq.football.data.local.TeamEntity
import com.khanhtq.football.databinding.ItemTeamBinding
import com.khanhtq.football.ui.TeamMatchActivity

class TeamAdapter(
    private val onTeamClicked: (ImageView, TeamEntity) -> Unit
) : ListAdapter<TeamEntity, TeamAdapter.TeamViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TeamViewHolder(ItemTeamBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: TeamViewHolder) {
        super.onViewRecycled(holder)
        holder.recycle()
    }

    inner class TeamViewHolder(
        binding: ItemTeamBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val logoImageView: ImageView = binding.logoImageView
        private val nameTextView: TextView = binding.nameTextView
        private var team: TeamEntity? = null

        init {
            logoImageView.transitionName = TeamMatchActivity.TRANSITION_NAME
            itemView.setOnClickListener {
                team?.let { onTeamClicked(logoImageView, it) }
            }
        }

        fun bind(teamEntity: TeamEntity) {
            Glide.with(itemView.context)
                .load(teamEntity.logo)
                .transform(CircleCrop(), FitCenter())
                .into(logoImageView)
            nameTextView.text = teamEntity.name
            team = teamEntity
        }

        fun recycle() {
            team = null
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TeamEntity>() {
            override fun areItemsTheSame(oldItem: TeamEntity, newItem: TeamEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TeamEntity, newItem: TeamEntity): Boolean {
                return oldItem.name == newItem.name && oldItem.logo == newItem.logo
            }
        }
    }

}