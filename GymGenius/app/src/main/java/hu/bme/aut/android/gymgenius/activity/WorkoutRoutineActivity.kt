package hu.bme.aut.android.gymgenius.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.gymgenius.adapter.WorkoutRoutineAdapter
import hu.bme.aut.android.gymgenius.data.WorkoutDatabase
import hu.bme.aut.android.gymgenius.data.table.WorkoutRoutine
import hu.bme.aut.android.gymgenius.databinding.ActivityWorkoutRoutineBinding
import hu.bme.aut.android.gymgenius.dialogfragment.edit.EditWorkoutRoutineDialogFragment
import hu.bme.aut.android.gymgenius.dialogfragment.newd.NewWorkoutRoutineDialogFragment
import kotlin.concurrent.thread

class WorkoutRoutineActivity : AppCompatActivity(), WorkoutRoutineAdapter.OnRoutineSelectedListener, WorkoutRoutineAdapter.WorkoutRoutineClickListener,
    NewWorkoutRoutineDialogFragment.NewWorkoutRoutineDialogListener, EditWorkoutRoutineDialogFragment.EditWorkoutRoutineDialogListener {
    private lateinit var binding: ActivityWorkoutRoutineBinding
    private lateinit var database: WorkoutDatabase
    private lateinit var adapter: WorkoutRoutineAdapter

    companion object{
        lateinit var workoutRoutine: WorkoutRoutine
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityWorkoutRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        database = WorkoutDatabase.getDatabase(applicationContext)

        binding.fab.setOnClickListener {
            NewWorkoutRoutineDialogFragment().show(
                supportFragmentManager,
                NewWorkoutRoutineDialogFragment.TAG
            )
        }

        supportActionBar?.title = ""

        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = WorkoutRoutineAdapter(this, this)
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        binding.rvMain.adapter = adapter
        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        thread {
            val items = database.workoutRoutineDao().getAll()
            runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onItemChanged(item: WorkoutRoutine) {
        thread {
            workoutRoutine = item
            EditWorkoutRoutineDialogFragment().show(
                supportFragmentManager,
                NewWorkoutRoutineDialogFragment.TAG
            )
        }
    }

    override fun onWorkoutRoutineEdited() {
        thread {
            database.workoutRoutineDao().update(workoutRoutine)
            loadItemsInBackground()
        }
    }

    override fun onWorkoutRoutineCreated(newItem: WorkoutRoutine) {
        thread {
            val insertId = database.workoutRoutineDao().insert(newItem)
            newItem.id = insertId
            runOnUiThread {
                adapter.addItem(newItem)
            }
        }
    }

    override fun onItemDeleted(item: WorkoutRoutine) {
        thread {
            var workoutDays = database.workoutDayDao().getAll()
            workoutDays = workoutDays.filter { (it.workoutRoutineId == item.id) }
            for(wd in workoutDays) {
                var connections = database.connectDao().getAll()
                connections = connections.filter { (it.workoutDayId == wd.id) }
                for (c in connections)
                    database.connectDao().deleteItem(c)
                database.workoutDayDao().deleteItem(wd)
            }
            database.workoutRoutineDao().deleteItem(item)
        }
    }

    override fun onRoutineSelected(item: WorkoutRoutine?) {
        workoutRoutine = item!!
        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@WorkoutRoutineActivity, WorkoutDayActivity::class.java)
        showDetailsIntent.putExtra(WorkoutDayActivity.EXTRA_WORKOUT_ROUTINE, workoutRoutine?.id.toString())
        startActivity(showDetailsIntent)
    }
}