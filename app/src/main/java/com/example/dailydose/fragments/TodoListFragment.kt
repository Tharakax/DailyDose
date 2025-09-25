package com.example.dailydose.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailydose.R
import com.example.dailydose.adapters.TodoAdapter
import com.example.dailydose.data.TodoRepository
import com.example.dailydose.databinding.FragmentTodoListBinding
import com.example.dailydose.model.Priority
import com.example.dailydose.model.TodoItem
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

    private fun setupClickListeners() {
        binding.fabAddTodo.setOnClickListener {
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

    private fun showAddTodoDialog() {
        // TODO: Implement add todo dialog
        // For now, create a sample todo
        val sampleTodo = TodoItem(
            id = UUID.randomUUID().toString(),
            title = "Sample Todo",
            description = "This is a sample todo item",
            priority = Priority.MEDIUM,
            category = "Health"
        )
        todoRepository.saveTodoItem(sampleTodo)
        loadData()
    }

    private fun showEditTodoDialog(todo: TodoItem) {
        // TODO: Implement edit todo dialog
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
