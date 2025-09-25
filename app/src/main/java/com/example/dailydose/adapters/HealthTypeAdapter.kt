package com.example.dailydose.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydose.R
import com.example.dailydose.model.HealthType

class HealthTypeAdapter(
    private var healthTypes: List<HealthType> = HealthType.values().toList(),
    private var selectedType: HealthType? = null,
    private val onTypeSelected: (HealthType) -> Unit = {}
) : RecyclerView.Adapter<HealthTypeAdapter.HealthTypeViewHolder>() {

    class HealthTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIcon: TextView = itemView.findViewById(R.id.tv_icon)
        val tvTypeName: TextView = itemView.findViewById(R.id.tv_type_name)
        val tvUnit: TextView = itemView.findViewById(R.id.tv_unit)
        val ivSelected: ImageView = itemView.findViewById(R.id.iv_selected)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthTypeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_health_type, parent, false)
        return HealthTypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HealthTypeViewHolder, position: Int) {
        val healthType = healthTypes[position]
        
        holder.tvIcon.text = healthType.icon
        holder.tvTypeName.text = healthType.displayName
        holder.tvUnit.text = healthType.unit
        
        val isSelected = selectedType == healthType
        holder.ivSelected.visibility = if (isSelected) View.VISIBLE else View.GONE
        
        holder.itemView.setOnClickListener {
            selectedType = healthType
            onTypeSelected(healthType)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = healthTypes.size

    fun updateSelectedType(type: HealthType?) {
        selectedType = type
        notifyDataSetChanged()
    }
}
