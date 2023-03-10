package com.khanhtq.football.ui.match

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.khanhtq.football.databinding.ItemMatchBinding
import com.khanhtq.football.databinding.ItemSectionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MatchAdapter(
    private val openHighlight: (String) -> Unit,
    private val setReminder: (Date, String, String, Boolean) -> Unit
) : ListAdapter<MatchSectionModel, ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == TYPE_SECTION) {
            SectionViewHolder(ItemSectionBinding.inflate(inflater, parent, false))
        } else {
            MatchViewHolder(ItemMatchBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is SectionViewHolder -> holder.bind(getItem(position) as Section)
            is MatchViewHolder -> holder.bind(getItem(position) as MatchEntity, position)
            else -> Unit
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is Section) {
            TYPE_SECTION
        } else {
            TYPE_MATCH
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is MatchViewHolder) {
            holder.recycle()
        }
    }

    class SectionViewHolder(binding: ItemSectionBinding) : ViewHolder(binding.root) {
        fun bind(section: Section) {
            (itemView as TextView).text = section.name
        }
    }

    inner class MatchViewHolder(binding: ItemMatchBinding) : ViewHolder(binding.root) {
        private val timeTextView = binding.timeTextView
        private val homeTextView = binding.homeTextView
        private val awayTextView = binding.awayTextView
        private val homeWinLabel = binding.homeWinLabel
        private val awayWinLabel = binding.awayWinLabel
        private val highlightButton = binding.highlightButton
        private val reminderSwitch = binding.reminderSwitch

        private var matchEntity: MatchEntity? = null
        private var position: Int? = null

        private val timeFormatter = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())

        init {
            highlightButton.setOnClickListener {
                openHighlight(matchEntity?.highlights ?: return@setOnClickListener)
            }
            reminderSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                val match = matchEntity ?: return@setOnCheckedChangeListener
                match.isReminderSet = isChecked
                val matchTitle = "${match.home} vs. ${match.away}"
                setReminder(match.date, matchTitle, match.description, isChecked)
            }
        }

        fun bind(match: MatchEntity, position: Int) {
            this.position = position
            matchEntity = match
            val timeString = timeFormatter.format(match.date)
            timeTextView.text = timeString
            homeTextView.text = match.home
            awayTextView.text = match.away
            homeWinLabel.isVisible = match.winner == match.home
            awayWinLabel.isVisible = match.winner == match.away
            highlightButton.isVisible = match.highlights != null
            reminderSwitch.isVisible = match.highlights == null
            reminderSwitch.isChecked = match.isReminderSet.nullToFalse()
        }

        private fun Boolean?.nullToFalse(): Boolean = this == true

        fun recycle() {
            matchEntity = null
            position = null
        }
    }

    companion object {
        private const val TYPE_SECTION = 1001
        private const val TYPE_MATCH = 1002

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MatchSectionModel>() {
            override fun areItemsTheSame(
                oldItem: MatchSectionModel,
                newItem: MatchSectionModel
            ): Boolean {
                return when (oldItem) {
                    is Section -> newItem is Section && oldItem.name == newItem.name
                    is MatchEntity -> newItem is MatchEntity &&
                            oldItem.date == newItem.date &&
                            oldItem.home == newItem.home &&
                            oldItem.away == newItem.away

                }
            }

            override fun areContentsTheSame(
                oldItem: MatchSectionModel,
                newItem: MatchSectionModel
            ): Boolean {
                return when (oldItem) {
                    is Section -> newItem is Section && oldItem.name == newItem.name
                    is MatchEntity -> newItem is MatchEntity &&
                            oldItem.description == newItem.description &&
                            oldItem.winner == newItem.winner &&
                            oldItem.highlights == newItem.highlights

                }
            }
        }
    }
}