package com.example.dailydose.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydose.R
import com.example.dailydose.model.QuickAction

class QuickActionsAdapter(
    private val quickActions: List<QuickAction>,
    private val onActionClick: (QuickAction) -> Unit
) : RecyclerView.Adapter<QuickActionsAdapter.QuickActionViewHolder>() {

    class QuickActionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardQuickAction)
        val iconImageView: ImageView = itemView.findViewById(R.id.ivQuickActionIcon)
        val titleTextView: TextView = itemView.findViewById(R.id.tvQuickActionTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickActionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quick_action, parent, false)
        return QuickActionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuickActionViewHolder, position: Int) {
        val action = quickActions[position]
        
        holder.titleTextView.text = action.title
        holder.iconImageView.setImageResource(action.iconRes)
        
        // Set card background color
        val color = ContextCompat.getColor(holder.itemView.context, action.colorRes)
        holder.cardView.setCardBackgroundColor(color)
        
        holder.cardView.setOnClickListener {
            onActionClick(action)
        }
    }

    override fun getItemCount(): Int = quickActions.size
}
