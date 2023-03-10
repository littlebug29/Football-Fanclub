package com.khanhtq.football.ui.match

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.khanhtq.football.R
import com.khanhtq.football.data.local.TeamEntity
import com.khanhtq.football.databinding.ItemTeamBinding
import com.khanhtq.football.ui.TeamMatchActivity

class SelectableTeamAdapter(
    private val filterMatchOfTeamBy: (String) -> Unit,
    private val clearFilter: () -> Unit
) : ListAdapter<TeamEntity, SelectableTeamAdapter.TeamViewHolder>(DIFF_CALLBACK) {
    private var selectedPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TeamViewHolder(ItemTeamBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun onViewRecycled(holder: TeamViewHolder) {
        super.onViewRecycled(holder)
        holder.recycle()
    }

    inner class TeamViewHolder(
        binding: ItemTeamBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val itemLayout: ConstraintLayout = binding.itemLayout
        private val logoImageView: ImageView = binding.logoImageView
        private val nameTextView: TextView = binding.nameTextView
        private var team: TeamEntity? = null
        private var position: Int? = null

        init {
            logoImageView.transitionName = TeamMatchActivity.TRANSITION_NAME
            nameTextView.isVisible = false
            itemView.setOnClickListener {
                val temp = selectedPosition
                if (selectedPosition == position) {
                    selectedPosition = null
                    clearFilter()
                } else {
                    selectedPosition = position
                    team?.let { filterMatchOfTeamBy(it.id) }
                }
                notifyItemChanged(position ?: return@setOnClickListener)
                notifyItemChanged(temp ?: return@setOnClickListener)
            }
        }

        fun bind(teamEntity: TeamEntity, position: Int) {
            Glide.with(itemView.context)
                .load(teamEntity.logo)
                .transform(CircleCrop(), FitCenter())
                .into(logoImageView)
            team = teamEntity
            this.position = position
            if (selectedPosition == position) {
                itemLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.purple_200
                    )
                )
            } else {
                itemLayout.setBackgroundColor(Color.WHITE)
            }
        }

        fun recycle() {
            team = null
            position = null
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