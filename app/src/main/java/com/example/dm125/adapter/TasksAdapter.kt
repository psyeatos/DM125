package com.example.dm125.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dm125.R
import com.example.dm125.databinding.TaskListItemBinding
import com.example.dm125.entity.Task
import com.example.dm125.listener.TaskItemClickListener

class TasksAdapter(private val context: Context,private val messageView: TextView, private val listener: TaskItemClickListener) : RecyclerView.Adapter<TaskViewHolder>() {

    private val  tasks = ArrayList<Task>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        Log.e("adapter", "criando um taskViewHolder")

        val binding = TaskListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return TaskViewHolder(context, binding, listener)
    }

    override fun getItemCount()= tasks.size

    override fun onBindViewHolder(taskViewHolder: TaskViewHolder, position: Int) {
        Log.e("adapter", "populando um taskViewHolder")

        val task = tasks[position]
        taskViewHolder.setValues(task)
    }

    @SuppressLint("NotifyDateSetChanged")
    fun setItems(items: List<Task>){
        tasks.clear()
        tasks.addAll(items)

        notifyDataSetChanged()
        checkEmptyTasks()
    }

    fun getItem(position: Int): Task {
        return tasks[position]
    }

    fun refreshItem(position: Int){
        notifyItemChanged(position)
    }

    fun deleteItem(position: Int){
        tasks.removeAt(position)
        notifyItemRemoved(position)

        checkEmptyTasks()
    }

    fun updateItem(position: Int, item: Task) {
     tasks[position] = item
     notifyItemChanged(position)
    }

    private fun checkEmptyTasks(){
        if (tasks.isEmpty()){
            messageView.text = ContextCompat.getString(context, R.string.no_tasks)
        } else {
            messageView.text = null
        }
    }
}