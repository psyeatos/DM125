package com.example.dm125.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dm125.R
import com.example.dm125.databinding.TaskListItemBinding
import com.example.dm125.entity.Task
import com.example.dm125.listener.TaskItemClickListener

class TaskViewHolder(
    private val context: Context,
    private val binding: TaskListItemBinding,
    private val listener: TaskItemClickListener)
    : RecyclerView.ViewHolder(binding.root) {

    fun setValues(task: Task){
        binding.tvTitle.text = task.title

        if (task.completed) {
            binding.tvTitle.setBackgroundResource(R.color.purple_700)
        } else {
            binding.tvTitle.setBackgroundResource(R.color.teal_200)
        }

        binding.tvDate.text = task.date?.let{
            task.date.toString()
        } ?: run {
            "-"
        }

        binding.root.setOnClickListener {
            listener.onClick(task)
        }

        binding.root.setOnCreateContextMenuListener { menu, _, _ ->
            menu.add(ContextCompat.getString(context, R.string.mark_as_completed)).setOnMenuItemClickListener {
                listener.onMarkAsCompleteClick(adapterPosition, task)
                true
            }

            menu.add(ContextCompat.getString(context, R.string.share)).setOnMenuItemClickListener {
                listener.onShareClick(task)
                true
            }
        }
    }
}