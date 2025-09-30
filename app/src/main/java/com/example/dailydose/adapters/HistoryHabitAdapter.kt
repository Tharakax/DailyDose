package com.example.dailydose.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydose.R
import com.example.dailydose.model.HealthEntry
import java.text.SimpleDateFormat
import java.util.*

class HistoryHabitAdapter(
    private var habits: List<HealthEntry> = emptyList(),
    private val onHabitClick: (HealthEntry) -> Unit = {}
) : RecyclerView.Adapter<HistoryHabitAdapter.HistoryHabitViewHolder>() {

    class HistoryHabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHabitIcon: TextView = itemView.findViewById(R.id.tv_habit_icon)
        val tvHabitTitle: TextView = itemView.findViewById(R.id.tv_habit_title)
        val tvHabitDescription: TextView = itemView.findViewById(R.id.tv_habit_description)
        val tvHabitDate: TextView = itemView.findViewById(R.id.tv_habit_date)
        val tvHabitCategory: TextView = itemView.findViewById(R.id.tv_habit_category)
        val tvHabitStatus: TextView = itemView.findViewById(R.id.tv_habit_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_habit, parent, false)
        return HistoryHabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryHabitViewHolder, position: Int) {
        val habit = habits[position]
        
        // Extract habit information from notes
        val habitInfo = habit.notes.substringAfter("HABIT: ").trim()
        val habitTitle = habitInfo.substringBefore(" - ").trim()
        val habitDescription = if (habitInfo.contains(" - ")) {
            habitInfo.substringAfter(" - ").substringBefore(" (").trim()
        } else {
            ""
        }
        val habitCategory = if (habitInfo.contains(" (")) {
            habitInfo.substringAfter(" (").substringBefore(")").trim()
        } else {
            "General"
        }

        // Set habit icon based on category
        val icon = when (habitCategory.lowercase()) {
            "health" -> "💪"
            "fitness" -> "🏃"
            "nutrition" -> "🥗"
            "mindfulness" -> "🧘"
            "productivity" -> "⚡"
            "sleep" -> "😴"
            "social" -> "👥"
            "learning" -> "📚"
            else -> "⭐"
        }

        // Format date
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(habit.date)

        // Update views
        holder.tvHabitIcon.text = icon
        holder.tvHabitTitle.text = habitTitle
        holder.tvHabitDescription.text = habitDescription
        holder.tvHabitDate.text = formattedDate
        holder.tvHabitCategory.text = habitCategory
        holder.tvHabitStatus.text = "✓"

        // Set click listener
        holder.itemView.setOnClickListener {
            onHabitClick(habit)
        }
    }

    override fun getItemCount(): Int = habits.size

    fun updateHabits(newHabits: List<HealthEntry>) {
        habits = newHabits
        notifyDataSetChanged()
    }

    fun filterHabits(filter: String) {
        val filteredHabits = when (filter) {
            "All" -> habits
            "Today" -> habits.filter { isToday(it.date) }
            "This Week" -> habits.filter { isThisWeek(it.date) }
            "This Month" -> habits.filter { isThisMonth(it.date) }
            else -> habits
        }
        updateHabits(filteredHabits)
    }

    private fun isToday(date: Date): Boolean {
        val today = Calendar.getInstance()
        val habitDate = Calendar.getInstance().apply { time = date }
        return today.get(Calendar.YEAR) == habitDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == habitDate.get(Calendar.DAY_OF_YEAR)
    }

    private fun isThisWeek(date: Date): Boolean {
        val today = Calendar.getInstance()
        val habitDate = Calendar.getInstance().apply { time = date }
        val weekAgo = Calendar.getInstance().apply {
            add(Calendar.WEEK_OF_YEAR, -1)
        }
        return habitDate.after(weekAgo.time) && habitDate.before(today.time)
    }

    private fun isThisMonth(date: Date): Boolean {
        val today = Calendar.getInstance()
        val habitDate = Calendar.getInstance().apply { time = date }
        return today.get(Calendar.YEAR) == habitDate.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == habitDate.get(Calendar.MONTH)
    }
}

