package com.example.dailydose.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydose.R
import com.example.dailydose.model.HealthEntry
import java.text.SimpleDateFormat
import java.util.*

class HealthEntryAdapter(
    private var entries: List<HealthEntry> = emptyList(),
    private val onItemClick: (HealthEntry) -> Unit = {},
    private val onEditHabit: (HealthEntry) -> Unit = {},
    private val onDeleteHabit: (HealthEntry) -> Unit = {}
) : RecyclerView.Adapter<HealthEntryAdapter.HealthEntryViewHolder>() {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    class HealthEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIcon: TextView = itemView.findViewById(R.id.tv_icon)
        val tvType: TextView = itemView.findViewById(R.id.tv_type)
        val tvValue: TextView = itemView.findViewById(R.id.tv_value)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvNotes: TextView = itemView.findViewById(R.id.tv_notes)
        val btnHabitMenu: ImageButton = itemView.findViewById(R.id.btn_habit_menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_health_entry, parent, false)
        return HealthEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HealthEntryViewHolder, position: Int) {
        val entry = entries[position]
        
        // Check if this is a habit entry
        if (entry.notes.startsWith("HABIT:")) {
            // Display habit information
            holder.tvIcon.text = "⭐"
            holder.tvType.text = "Habit"
            
            // Extract habit title from notes (format: "HABIT: Title - Description (Category)")
            val habitInfo = entry.notes.substringAfter("HABIT: ").trim()
            val habitTitle = habitInfo.substringBefore(" - ").trim()
            holder.tvValue.text = habitTitle
            
            // Show menu button for habits
            holder.btnHabitMenu.visibility = View.VISIBLE
            setupHabitMenu(holder, entry)
        } else {
            // Handle regular health entries
            if (entry.type != null) {
                holder.tvIcon.text = entry.type.icon
                holder.tvType.text = entry.type.displayName
            } else {
                holder.tvIcon.text = "❓"
                holder.tvType.text = "Unknown"
            }
            holder.tvValue.text = "${entry.value} ${entry.unit ?: ""}"
            
            // Hide menu button for non-habits
            holder.btnHabitMenu.visibility = View.GONE
        }
        
        val dateText = if (entry.date != null) {
            if (isToday(entry.date)) {
                "Today, ${timeFormat.format(entry.date)}"
            } else {
                "${dateFormat.format(entry.date)}, ${timeFormat.format(entry.date)}"
            }
        } else {
            "Unknown date"
        }
        holder.tvDate.text = dateText
        
        if (entry.notes.isNotEmpty()) {
            if (entry.notes.startsWith("HABIT:")) {
                // For habits, show the description part
                val habitInfo = entry.notes.substringAfter("HABIT: ").trim()
                val habitDescription = if (habitInfo.contains(" - ")) {
                    habitInfo.substringAfter(" - ").substringBefore(" (").trim()
                } else {
                    habitInfo
                }
                holder.tvNotes.text = habitDescription
            } else {
                holder.tvNotes.text = entry.notes
            }
            holder.tvNotes.visibility = View.VISIBLE
        } else {
            holder.tvNotes.visibility = View.GONE
        }
        
        holder.itemView.setOnClickListener {
            onItemClick(entry)
        }
    }

    override fun getItemCount(): Int = entries.size

    fun updateEntries(newEntries: List<HealthEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }

    private fun isToday(date: Date?): Boolean {
        if (date == null) return false
        
        val today = Calendar.getInstance()
        val entryDate = Calendar.getInstance().apply { time = date }
        
        return today.get(Calendar.YEAR) == entryDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == entryDate.get(Calendar.DAY_OF_YEAR)
    }
    
    private fun setupHabitMenu(holder: HealthEntryViewHolder, entry: HealthEntry) {
        holder.btnHabitMenu.setOnClickListener { view ->
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.habit_menu, popupMenu.menu)
            
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit_habit -> {
                        onEditHabit(entry)
                        true
                    }
                    R.id.menu_delete_habit -> {
                        onDeleteHabit(entry)
                        true
                    }
                    else -> false
                }
            }
            
            popupMenu.show()
        }
    }
}

