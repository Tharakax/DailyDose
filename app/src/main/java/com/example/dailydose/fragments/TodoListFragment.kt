package com.example.dailydose.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.animation.AnimationUtils
import android.view.animation.Animation
import com.example.dailydose.R
import com.example.dailydose.adapters.TodoAdapter
import com.example.dailydose.data.TodoRepository
import com.example.dailydose.databinding.DialogAddEditTodoBinding
import com.example.dailydose.databinding.FragmentTodoListBinding
import com.example.dailydose.model.Priority
import com.example.dailydose.model.TodoItem
import java.text.SimpleDateFormat
import java.util.*

class TodoListFragment : Fragment() {

    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var todoRepository: TodoRepository
    private lateinit var todoAdapter: TodoAdapter
    private var currentFilter = FilterType.ALL

    enum class FilterType {
        ALL, ACTIVE, COMPLETED, PRIORITY
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRepository()
        setupRecyclerView()
        setupClickListeners()
        loadData()
        startAnimations()
    }

    private fun setupRepository() {
        todoRepository = TodoRepository(requireContext())
    }

    private fun setupRecyclerView() {
        todoAdapter = TodoAdapter(
            onTodoClick = { todo ->
                // Handle todo click - could show details
            },
            onTodoToggle = { todo ->
                todoRepository.toggleTodoCompletion(todo.id)
                loadData()
            },
            onTodoEdit = { todo ->
                showEditTodoDialog(todo)
            },
            onTodoDelete = { todo ->
                showDeleteConfirmation(todo)
            }
        )
        
        binding.rvTodos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }
    }

    private fun startAnimations() {
        // Animate header section
        binding.root.findViewById<View>(R.id.header_section)?.let { headerSection ->
            val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
            headerSection.startAnimation(fadeIn)
        }

        // Animate filter buttons
        binding.btnFilterAll?.let { btn ->
            val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_bottom)
            slideIn.startOffset = 100
            btn.startAnimation(slideIn)
        }

        binding.btnFilterActive?.let { btn ->
            val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_bottom)
            slideIn.startOffset = 150
            btn.startAnimation(slideIn)
        }

        binding.btnFilterCompleted?.let { btn ->
            val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_bottom)
            slideIn.startOffset = 200
            btn.startAnimation(slideIn)
        }

        binding.btnFilterPriority?.let { btn ->
            val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_bottom)
            slideIn.startOffset = 250
            btn.startAnimation(slideIn)
        }

        // Animate FAB
        binding.fabAddTodo?.let { fab ->
            val bounceIn = AnimationUtils.loadAnimation(context, R.anim.bounce_in)
            bounceIn.startOffset = 300
            fab.startAnimation(bounceIn)
        }

        // Animate RecyclerView
        binding.rvTodos?.let { rv ->
            val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right)
            slideIn.startOffset = 400
            rv.startAnimation(slideIn)
        }
    }

    private fun setupClickListeners() {
        binding.fabAddTodo.setOnClickListener { view ->
            animateFabClick(view)
            showAddTodoDialog()
        }
        
        // Add Your First Task button in empty state
        binding.btnAddFirstTask?.setOnClickListener { view ->
            animateFabClick(view)
            showAddTodoDialog()
        }
        
        binding.btnFilterAll.setOnClickListener {
            setFilter(FilterType.ALL)
        }
        
        binding.btnFilterActive.setOnClickListener {
            setFilter(FilterType.ACTIVE)
        }
        
        binding.btnFilterCompleted.setOnClickListener {
            setFilter(FilterType.COMPLETED)
        }
        
        binding.btnFilterPriority.setOnClickListener {
            setFilter(FilterType.PRIORITY)
        }
    }

    private fun setFilter(filterType: FilterType) {
        currentFilter = filterType
        updateFilterButtons()
        loadData()
    }

    private fun updateFilterButtons() {
        // Reset all buttons
        binding.btnFilterAll.isSelected = false
        binding.btnFilterActive.isSelected = false
        binding.btnFilterCompleted.isSelected = false
        binding.btnFilterPriority.isSelected = false
        
        // Select current filter
        when (currentFilter) {
            FilterType.ALL -> binding.btnFilterAll.isSelected = true
            FilterType.ACTIVE -> binding.btnFilterActive.isSelected = true
            FilterType.COMPLETED -> binding.btnFilterCompleted.isSelected = true
            FilterType.PRIORITY -> binding.btnFilterPriority.isSelected = true
        }
    }

    private fun loadData() {
        val todos = when (currentFilter) {
            FilterType.ALL -> todoRepository.getAllTodoItems()
            FilterType.ACTIVE -> todoRepository.getActiveTodos()
            FilterType.COMPLETED -> todoRepository.getCompletedTodos()
            FilterType.PRIORITY -> todoRepository.getAllTodoItems()
                .filter { it.priority == Priority.HIGH || it.priority == Priority.URGENT }
        }.sortedWith(compareBy<TodoItem> { !it.isCompleted }.thenBy { it.priority.ordinal })
        
        todoAdapter.updateTodos(todos)
        
        // Show/hide empty state
        if (todos.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvTodos.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvTodos.visibility = View.VISIBLE
        }
    }

    private fun animateFabClick(view: View) {
        val scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_in)
        scaleDown.duration = 100
        scaleDown.interpolator = android.view.animation.DecelerateInterpolator()
        
        view.startAnimation(scaleDown)
        
        scaleDown.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                val scaleUp = AnimationUtils.loadAnimation(context, R.anim.bounce_in)
                scaleUp.duration = 200
                view.startAnimation(scaleUp)
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun showAddTodoDialog() {
        showTodoDialog(null)
    }

    private fun showEditTodoDialog(todo: TodoItem) {
        showTodoDialog(todo)
    }

    private fun showTodoDialog(todo: TodoItem?) {
        val dialogBinding = DialogAddEditTodoBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        var selectedPriority = todo?.priority ?: Priority.MEDIUM
        var selectedDate = todo?.dueDate
        var selectedTime: Calendar? = null

        // Set initial values if editing
        if (todo != null) {
            dialogBinding.dialogTitle.text = getString(R.string.edit_todo)
            dialogBinding.etTodoTitle.setText(todo.title)
            dialogBinding.etTodoDescription.setText(todo.description)
            dialogBinding.etTodoCategory.setText(todo.category)
            selectedDate = todo.dueDate
            if (selectedDate != null) {
                selectedTime = Calendar.getInstance().apply { time = selectedDate }
            }
        }

        // Priority selection
        setupPriorityButtons(dialogBinding, selectedPriority) { priority ->
            selectedPriority = priority
        }

        // Date selection
        dialogBinding.btnSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            selectedDate?.let { calendar.time = it }
            
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                selectedDate = selectedCalendar.time
                
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                dialogBinding.btnSelectDate.text = dateFormat.format(selectedDate)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Time selection
        dialogBinding.btnSelectTime.setOnClickListener {
            val calendar = selectedTime ?: Calendar.getInstance()
            
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                selectedTime = Calendar.getInstance().apply {
                    time = selectedDate ?: Date()
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                selectedDate = selectedTime?.time
                
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                dialogBinding.btnSelectTime.text = timeFormat.format(selectedTime?.time)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        // Save button
        dialogBinding.btnSave.setOnClickListener {
            val title = dialogBinding.etTodoTitle.text.toString().trim()
            if (title.isEmpty()) {
                Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val todoItem = if (todo != null) {
                todo.copy(
                    title = title,
                    description = dialogBinding.etTodoDescription.text.toString().trim(),
                    category = dialogBinding.etTodoCategory.text.toString().trim().ifEmpty { "General" },
                    priority = selectedPriority,
                    dueDate = selectedDate
                )
            } else {
                TodoItem(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = dialogBinding.etTodoDescription.text.toString().trim(),
                    category = dialogBinding.etTodoCategory.text.toString().trim().ifEmpty { "General" },
                    priority = selectedPriority,
                    dueDate = selectedDate
                )
            }

            if (todo != null) {
                todoRepository.updateTodoItem(todoItem)
                Toast.makeText(context, "Todo updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                todoRepository.saveTodoItem(todoItem)
                Toast.makeText(context, "Todo added successfully", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
            loadData()
        }

        // Cancel button
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupPriorityButtons(
        binding: DialogAddEditTodoBinding,
        initialPriority: Priority,
        onPrioritySelected: (Priority) -> Unit
    ) {
        val priorityButtons = listOf(
            binding.btnPriorityLow to Priority.LOW,
            binding.btnPriorityMedium to Priority.MEDIUM,
            binding.btnPriorityHigh to Priority.HIGH,
            binding.btnPriorityUrgent to Priority.URGENT
        )

        // Set initial selection
        priorityButtons.forEach { (button, priority) ->
            button.isSelected = priority == initialPriority
            updatePriorityButtonStyle(button, priority, priority == initialPriority)
        }

        // Set click listeners
        priorityButtons.forEach { (button, priority) ->
            button.setOnClickListener {
                priorityButtons.forEach { (btn, _) ->
                    btn.isSelected = false
                    updatePriorityButtonStyle(btn, priority, false)
                }
                button.isSelected = true
                updatePriorityButtonStyle(button, priority, true)
                onPrioritySelected(priority)
            }
        }
    }

    private fun updatePriorityButtonStyle(button: com.google.android.material.button.MaterialButton, priority: Priority, isSelected: Boolean) {
        if (isSelected) {
            button.setBackgroundColor(getPriorityColor(priority))
            button.setTextColor(android.graphics.Color.WHITE)
        } else {
            button.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            button.setTextColor(getPriorityColor(priority))
        }
    }

    private fun getPriorityColor(priority: Priority): Int {
        return when (priority) {
            Priority.LOW -> resources.getColor(R.color.priority_low, null)
            Priority.MEDIUM -> resources.getColor(R.color.priority_medium, null)
            Priority.HIGH -> resources.getColor(R.color.priority_high, null)
            Priority.URGENT -> resources.getColor(R.color.priority_urgent, null)
        }
    }

    private fun showDeleteConfirmation(todo: TodoItem) {
        // TODO: Implement delete confirmation dialog
        todoRepository.deleteTodoItem(todo.id)
        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData() // Refresh data when returning to fragment
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

