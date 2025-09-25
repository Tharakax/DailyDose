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

class HealthEntryAdapter(
    private var entries: List<HealthEntry> = emptyList(),
    private val onItemClick: (HealthEntry) -> Unit = {}
) : RecyclerView.Adapter<HealthEntryAdapter.HealthEntryViewHolder>() {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    class HealthEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIcon: TextView = itemView.findViewById(R.id.tv_icon)
        val tvType: TextView = itemView.findViewById(R.id.tv_type)
        val tvValue: TextView = itemView.findViewById(R.id.tv_value)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvNotes: TextView = itemView.findViewById(R.id.tv_notes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_health_entry, parent, false)
        return HealthEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HealthEntryViewHolder, position: Int) {
        val entry = entries[position]
        
        holder.tvIcon.text = entry.type.icon
        holder.tvType.text = entry.type.displayName
        holder.tvValue.text = "${entry.value} ${entry.unit}"
        
        val dateText = if (isToday(entry.date)) {
            "Today, ${timeFormat.format(entry.date)}"
        } else {
            "${dateFormat.format(entry.date)}, ${timeFormat.format(entry.date)}"
        }
        holder.tvDate.text = dateText
        
        if (entry.notes.isNotEmpty()) {
            holder.tvNotes.text = entry.notes
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

    private fun isToday(date: Date): Boolean {
        val today = Calendar.getInstance()
        val entryDate = Calendar.getInstance().apply { time = date }
        
        return today.get(Calendar.YEAR) == entryDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == entryDate.get(Calendar.DAY_OF_YEAR)
    }
}
