package com.example.dm125.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dm125.R
import com.example.dm125.databinding.TaskListItemBinding
import com.example.dm125.entity.Task
import com.example.dm125.listener.TaskItemClickListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class TaskViewHolder(
    private val context: Context,
    private val binding: TaskListItemBinding,
    private val listener: TaskItemClickListener)
    : RecyclerView.ViewHolder(binding.root) {

    fun setValues(task: Task){
        binding.tvTitle.text = task.title

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val useExpandedFormat = sharedPreferences.getBoolean("expanded_date", false)

        if (task.completed) {
            binding.tvTitle.setBackgroundResource(R.color.teal_700)
        } else {
            binding.tvTitle.setBackgroundResource(R.color.teal_200)
        }

        binding.tvDate.text = task.date?.let { date ->
            if (useExpandedFormat) {
                date.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR")))
            } else {
                date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            }
        } ?: run {
            "-"
        }

        binding.tvTime.text = task.time?.let {
            task.time.toString()
        } ?: run {
            "-"
        }

        binding.root.setOnClickListener {
            listener.onClick(task)
        }

        val currentDate = LocalDate.now()

        task.date?.let { date ->
            when {
                task.completed -> binding.statusIndicator.setBackgroundResource(R.color.teal_700)
                date == null -> binding.statusIndicator.setBackgroundResource(R.color.blue_400)
                date < currentDate -> binding.statusIndicator.setBackgroundResource(R.color.red_400)
                date == currentDate -> binding.statusIndicator.setBackgroundResource(R.color.yellow_400)
                else -> binding.statusIndicator.setBackgroundResource(R.color.purple_200)
            }
        } ?: run {
            if (!task.completed)
                binding.statusIndicator.setBackgroundResource(R.color.white)
            else
                binding.statusIndicator.setBackgroundResource(R.color.teal_700)
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