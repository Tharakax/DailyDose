package com.example.dailydose.data

import android.content.Context
import android.content.SharedPreferences
import com.example.dailydose.model.TodoItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class TodoRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("todo_data", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveTodoItem(todoItem: TodoItem) {
        val todos = getAllTodoItems().toMutableList()
        todos.add(todoItem)
        saveTodos(todos)
    }

    fun getAllTodoItems(): List<TodoItem> {
        val json = sharedPreferences.getString("todo_items", "[]")
        val type = object : TypeToken<List<TodoItem>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun getActiveTodos(): List<TodoItem> {
        return getAllTodoItems().filter { !it.isCompleted }
    }

    fun getCompletedTodos(): List<TodoItem> {
        return getAllTodoItems().filter { it.isCompleted }
    }

    fun updateTodoItem(todoItem: TodoItem) {
        val todos = getAllTodoItems().toMutableList()
        val index = todos.indexOfFirst { it.id == todoItem.id }
        if (index != -1) {
            todos[index] = todoItem
            saveTodos(todos)
        }
    }

    fun deleteTodoItem(todoId: String) {
        val todos = getAllTodoItems().toMutableList()
        todos.removeAll { it.id == todoId }
        saveTodos(todos)
    }

    fun toggleTodoCompletion(todoId: String) {
        val todos = getAllTodoItems().toMutableList()
        val index = todos.indexOfFirst { it.id == todoId }
        if (index != -1) {
            todos[index] = todos[index].copy(isCompleted = !todos[index].isCompleted)
            saveTodos(todos)
        }
    }

    fun getTodosByCategory(category: String): List<TodoItem> {
        return getAllTodoItems().filter { it.category == category }
    }

    fun getTodosByPriority(priority: com.example.dailydose.model.Priority): List<TodoItem> {
        return getAllTodoItems().filter { it.priority == priority }
    }

    private fun saveTodos(todos: List<TodoItem>) {
        val json = gson.toJson(todos)
        sharedPreferences.edit().putString("todo_items", json).apply()
    }
}

