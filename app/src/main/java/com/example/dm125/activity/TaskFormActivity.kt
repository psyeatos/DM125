package com.example.dm125.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dm125.databinding.ActivityTaskFormBinding
import com.example.dm125.entity.Task
import com.example.dm125.service.TaskService

class TaskFormActivity : AppCompatActivity() {

    private lateinit var  binding: ActivityTaskFormBinding

    private val taskService : TaskService by viewModels()

    private var taskId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTaskFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent.extras?.getString(Intent.EXTRA_TEXT)?.let { text ->
            binding.etTitle.setText(text)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        initComponents()
        setValues()
    }

    private fun initComponents(){
        binding.btSave.setOnClickListener{
            val task = Task(title = binding.etTitle.text.toString(), id = taskId)
            taskService.save(task).observe(this){ responseDto ->
                if(responseDto.isError){
                    Toast.makeText(this, "Erro com o servidor", Toast.LENGTH_SHORT).show()
                } else {
                    finish()
                }
            }
        }
    }

    @Suppress("deprecation")
    private fun setValues() {
        (intent.extras?.getSerializable("task") as Task?)?.let { task ->
            taskId = task.id
            binding.etTitle.setText(task.title)

            if (task.completed){
                binding.btSave.visibility = View.INVISIBLE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}