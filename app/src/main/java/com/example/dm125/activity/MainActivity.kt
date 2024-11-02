package com.example.dm125.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dm125.R
import com.example.dm125.adapter.TaskItemTouchCallback
import com.example.dm125.adapter.TasksAdapter
import com.example.dm125.databinding.ActivityMainBinding
import com.example.dm125.entity.Task
import com.example.dm125.fragment.PreferenceFragment
import com.example.dm125.helper.NotificationHelper
import com.example.dm125.listener.TaskItemClickListener
import com.example.dm125.listener.TaskItemSwipeListener
import com.example.dm125.service.TaskService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var tasksAdapter: TasksAdapter
    
    private val taskService: TaskService by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initComponents()

        askNotificationPermission()

        if(Firebase.auth.currentUser == null){
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }

         val helper = NotificationHelper(this)
             //helper.showNotification("title", "text")
    }

    override fun onResume() {
        super.onResume()

        val pref = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PreferenceFragment.DAILY_NOTIFICATION_KEY, false)

        Log.e("pref", "O valor da configuração é: $pref")

        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(PreferenceFragment.DAILY_NOTIFICATION_KEY, false).apply()

        readTasks()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_settings) {
            startActivity(Intent(this, PreferenceActivity::class.java))
        }

        if(item.itemId == R.id.action_logout) {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initComponents(){
        tasksAdapter = TasksAdapter(this, binding.tvMessage,object : TaskItemClickListener {
            override fun onClick(task: Task) {
                val intent = Intent(this@MainActivity, TaskFormActivity::class.java)
                intent.putExtra("task", task)
                startActivity(intent)
            }

            override fun onMarkAsCompleteClick(position: Int,task: Task) {
                taskService.markAsCompleted(task).observe(this@MainActivity) { responseDto ->
                    if(responseDto.isError) {
                        Toast.makeText(this@MainActivity, "Erro", Toast.LENGTH_SHORT).show()
                    } else {
                        responseDto.value?.let{
                            tasksAdapter.updateItem(position, responseDto.value)
                        }

                    }
                }
            }

            override fun onShareClick(task: Task) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT, task.title)
                intent.setType("text/plain")

                startActivity(Intent.createChooser(intent,ContextCompat.getString(this@MainActivity, R.string.share_using)))
            }


        })

        binding.rvTask.adapter = tasksAdapter
        binding.rvTask.layoutManager = LinearLayoutManager(this)

        ItemTouchHelper(TaskItemTouchCallback(object : TaskItemSwipeListener{
            override fun onSwipe(position: Int) {
                val task = tasksAdapter.getItem(position)
                taskService.delete(task).observe(this@MainActivity) { responseDto ->
                    if(responseDto.isError) {
                        tasksAdapter.refreshItem(position)
                    } else {
                        tasksAdapter.deleteItem(position)
                    }
                }
            }
        })).attachToRecyclerView(binding.rvTask)

        binding.srlTasks.setOnRefreshListener {
            readTasks()
        }

        binding.fabNewTask.setOnClickListener {
            startActivity(Intent(this, TaskFormActivity::class.java))
        }
    }

    private fun readTasks() {
        taskService.readAll().observe(this) { responseDto ->
            binding.srlTasks.isRefreshing = false
            if (responseDto.isError) {
                Toast.makeText(this, "Erro com o servidor", Toast.LENGTH_SHORT).show()
                binding.tvMessage.text = ContextCompat.getString(this, R.string.get_tasks_error)
            } else {
                responseDto.value?.let { tasks ->
                    tasksAdapter.setItems(tasks)
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_DENIED
            ) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    AlertDialog.Builder(this)
                        .setMessage(R.string.notification_permission_rationale)
                        .setPositiveButton(
                            R.string.accept
                        ) { _, _ ->
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }.setNegativeButton(R.string.not_accept, null)
                        .show()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Log.e("permission", "Permission dada: $isGranted")
        }
}