package com.example.dailydose.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydose.R
import com.example.dailydose.model.Priority
import com.example.dailydose.model.TodoItem
import java.text.SimpleDateFormat
import java.util.*

class TodoAdapter(
    private var todos: List<TodoItem> = emptyList(),
    private val onTodoClick: (TodoItem) -> Unit = {},
    private val onTodoToggle: (TodoItem) -> Unit = {},
    private val onTodoEdit: (TodoItem) -> Unit = {},
    private val onTodoDelete: (TodoItem) -> Unit = {}
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewPriority: View = itemView.findViewById(R.id.view_priority)
        val cbCompleted: CheckBox = itemView.findViewById(R.id.cb_completed)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        val tvPriority: TextView = itemView.findViewById(R.id.tv_priority)
        val tvDueDate: TextView = itemView.findViewById(R.id.tv_due_date)
        val ivEdit: ImageView = itemView.findViewById(R.id.iv_edit)
        val ivDelete: ImageView = itemView.findViewById(R.id.iv_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todos[position]
        
        // Set priority color
        holder.viewPriority.setBackgroundColor(android.graphics.Color.parseColor(todo.priority.color))
        
        // Set completion state
        holder.cbCompleted.isChecked = todo.isCompleted
        
        // Set text content
        holder.tvTitle.text = todo.title
        holder.tvDescription.text = todo.description
        holder.tvCategory.text = todo.category
        holder.tvPriority.text = todo.priority.displayName
        
        // Set due date
        todo.dueDate?.let { dueDate ->
            val isOverdue = dueDate.before(Date()) && !todo.isCompleted
            holder.tvDueDate.text = if (isOverdue) {
                "Overdue: ${dateFormat.format(dueDate)}"
            } else {
                "Due: ${dateFormat.format(dueDate)}"
            }
            holder.tvDueDate.setTextColor(
                if (isOverdue) android.graphics.Color.parseColor("#F44336")
                else android.graphics.Color.parseColor("#757575")
            )
        } ?: run {
            holder.tvDueDate.visibility = View.GONE
        }
        
        // Set completion styling
        if (todo.isCompleted) {
            holder.tvTitle.alpha = 0.6f
            holder.tvDescription.alpha = 0.6f
            holder.itemView.alpha = 0.7f
        } else {
            holder.tvTitle.alpha = 1.0f
            holder.tvDescription.alpha = 1.0f
            holder.itemView.alpha = 1.0f
        }
        
        // Set click listeners
        holder.cbCompleted.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != todo.isCompleted) {
                onTodoToggle(todo)
            }
        }
        
        holder.ivEdit.setOnClickListener {
            onTodoEdit(todo)
        }
        
        holder.ivDelete.setOnClickListener {
            onTodoDelete(todo)
        }
        
        holder.itemView.setOnClickListener {
            onTodoClick(todo)
        }
    }

    override fun getItemCount(): Int = todos.size

    fun updateTodos(newTodos: List<TodoItem>) {
        todos = newTodos
        notifyDataSetChanged()
    }
}
