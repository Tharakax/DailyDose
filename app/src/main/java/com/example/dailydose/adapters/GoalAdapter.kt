package com.example.dailydose.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydose.R
import com.example.dailydose.model.HealthGoal

class GoalAdapter(
    private var goals: List<HealthGoal> = emptyList(),
    private val onGoalClick: (HealthGoal) -> Unit = {}
) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIcon: TextView = itemView.findViewById(R.id.tv_icon)
        val tvGoalType: TextView = itemView.findViewById(R.id.tv_goal_type)
        val tvGoalTarget: TextView = itemView.findViewById(R.id.tv_goal_target)
        val tvProgressPercentage: TextView = itemView.findViewById(R.id.tv_progress_percentage)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        val tvProgressText: TextView = itemView.findViewById(R.id.tv_progress_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goal, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        
        holder.tvIcon.text = goal.type.icon
        holder.tvGoalType.text = "${goal.type.displayName} Goal"
        holder.tvGoalTarget.text = "Target: ${goal.targetValue} ${goal.type.unit}"
        
        val progress = if (goal.targetValue > 0) {
            ((goal.currentValue / goal.targetValue) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
        
        holder.tvProgressPercentage.text = "$progress%"
        holder.progressBar.progress = progress
        holder.tvProgressText.text = "Current: ${goal.currentValue} ${goal.type.unit}"
        
        holder.itemView.setOnClickListener {
            onGoalClick(goal)
        }
    }

    override fun getItemCount(): Int = goals.size

    fun updateGoals(newGoals: List<HealthGoal>) {
        goals = newGoals
        notifyDataSetChanged()
    }
}
