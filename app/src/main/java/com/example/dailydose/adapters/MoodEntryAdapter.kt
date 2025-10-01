package com.example.dailydose.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydose.R
import com.example.dailydose.model.MoodEntry
import java.text.SimpleDateFormat
import java.util.*

class MoodEntryAdapter(
    private val onItemClick: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodEntryAdapter.MoodEntryViewHolder>() {

    private var moodEntries = listOf<MoodEntry>()
    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun updateEntries(entries: List<MoodEntry>) {
        moodEntries = entries.sortedByDescending { it.timestamp }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood_entry, parent, false)
        return MoodEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodEntryViewHolder, position: Int) {
        holder.bind(moodEntries[position])
    }

    override fun getItemCount(): Int = moodEntries.size

    inner class MoodEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMoodEmoji: TextView = itemView.findViewById(R.id.tvMoodEmoji)
        private val tvMoodName: TextView = itemView.findViewById(R.id.tvMoodName)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvIntensity: TextView = itemView.findViewById(R.id.tvIntensity)

        fun bind(moodEntry: MoodEntry) {
            tvMoodEmoji.text = moodEntry.mood.emoji
            tvMoodName.text = moodEntry.mood.displayName
            tvTime.text = dateFormat.format(moodEntry.date)
            tvIntensity.text = "${moodEntry.intensity}/10"
            
            // Set mood color
            val color = android.graphics.Color.parseColor(moodEntry.mood.color)
            tvMoodName.setTextColor(color)
            tvIntensity.setTextColor(color)
            
            itemView.setOnClickListener {
                onItemClick(moodEntry)
            }
        }
    }
}

