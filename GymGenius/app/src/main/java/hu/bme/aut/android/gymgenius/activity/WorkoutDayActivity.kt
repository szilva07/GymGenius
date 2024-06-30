package hu.bme.aut.android.gymgenius.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.gymgenius.R
import hu.bme.aut.android.gymgenius.adapter.WorkoutDayAdapter
import hu.bme.aut.android.gymgenius.data.WorkoutDatabase
import hu.bme.aut.android.gymgenius.data.table.Exercise
import hu.bme.aut.android.gymgenius.data.table.WorkoutDay
import hu.bme.aut.android.gymgenius.databinding.ActivityWorkoutDayBinding
import hu.bme.aut.android.gymgenius.dialogfragment.edit.EditWorkoutDayDialogFragment
import hu.bme.aut.android.gymgenius.dialogfragment.newd.NewWorkoutDayDialogFragment
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class WorkoutDayActivity : AppCompatActivity(), WorkoutDayAdapter.OnDaySelectedListener, WorkoutDayAdapter.WorkoutDayClickListener,
    NewWorkoutDayDialogFragment.NewWorkoutDayDialogListener, EditWorkoutDayDialogFragment.EditWorkoutDayDialogListener {
    private lateinit var binding: ActivityWorkoutDayBinding
    private lateinit var database: WorkoutDatabase
    private lateinit var adapter: WorkoutDayAdapter

    private var routine: String? = null

    companion object {
        private const val TAG = "WorkoutDayActivity"
        const val EXTRA_WORKOUT_ROUTINE = "extra.workout_routine"
        private lateinit var db: WorkoutDatabase
        lateinit var workoutDay: WorkoutDay
        lateinit var exercises: MutableList<Exercise>
        var buttonValues: MutableList<Boolean> = ArrayList()
        var orderedExercises: MutableList<OrderedExercise> = ArrayList()

        fun onExerciseDescriptionChanged(ex: Exercise, description: String){
            thread {
                ex.description = description
                db.exerciseDao().update(ex)
            }
        }
    }

    class OrderedExercise{
        private var exercise: Exercise
        private var numberOfExercise = 1

        constructor(ex: Exercise, n: Int?){
            exercise = ex
            numberOfExercise = n!!
        }

        fun getExercise() = exercise
        fun getNumberOfExercise() = numberOfExercise
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutDayBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        database = WorkoutDatabase.getDatabase(applicationContext)
        db = database

        routine = intent.getStringExtra(EXTRA_WORKOUT_ROUTINE)

        binding.fab.setOnClickListener{
            NewWorkoutDayDialogFragment().show(
                supportFragmentManager,
                NewWorkoutDayDialogFragment.TAG
            )
        }

        supportActionBar?.title = getString(R.string.routine, WorkoutRoutineActivity.workoutRoutine.name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadItemsInBackground()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        adapter = WorkoutDayAdapter(this, this)
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        binding.rvMain.adapter = adapter
    }

    private fun loadItemsInBackground() {
        thread {
            var items = database.workoutDayDao().getAll()
            items = items.filter { (it.workoutRoutineId.toString()==routine) }
            buttonValues.clear()
            for(i in items){
                var connections = database.connectDao().getAll()
                connections = connections.filter { (it.workoutDayId == i.id) }
                buttonValues.add(connections.isNotEmpty())
            }
            runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onItemChanged(item: WorkoutDay) {
        thread {
            workoutDay = item
            EditWorkoutDayDialogFragment().show(
                supportFragmentManager,
                NewWorkoutDayDialogFragment.TAG
            )
        }
    }

    override fun onWorkoutDayEdited() {
        thread {
            database.workoutDayDao().update(workoutDay)
            loadItemsInBackground()
        }
    }

    override fun onWorkoutDayCreated(newItem: WorkoutDay) {
        thread {
            val insertId = database.workoutDayDao().insert(newItem)
            newItem.id = insertId
            newItem.workoutRoutineId = routine?.toLong()
            database.workoutDayDao().update(newItem)
            runOnUiThread {
                adapter.addItem(newItem)
            }
            loadItemsInBackground()
        }
    }

    override fun onItemDeleted(item: WorkoutDay) {
        thread {
            var connections = database.connectDao().getAll()
            connections = connections.filter { (it.workoutDayId == item.id) }
            for(c in connections)
                database.connectDao().deleteItem(c)
            database.workoutDayDao().deleteItem(item)
        }
    }

    private fun initializeExercises(){
        thread {
            var items  = database.exerciseDao().getAll()
            var items2 = items.filter { (false) }
            var connections = database.connectDao().getAll()
            connections = connections.filter { (it.workoutDayId == workoutDay.id) }
            for(c in connections) {
                items2 += items.filter { (it.id == c.exerciseId) }
            }
            exercises = items2.toMutableList()

            orderedExercises.clear()
            for(ex in exercises){
                var connections = database.connectDao().getAll()
                connections = connections.filter { (it.workoutDayId == workoutDay.id && it.exerciseId == ex.id) }
                orderedExercises.add(OrderedExercise(ex, connections[0].exerciseNumber))
            }
        }
    }

    override fun onItemStarted(item: WorkoutDay){
        workoutDay = item
        initializeExercises()
        Thread.sleep(100)
        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@WorkoutDayActivity, StartWorkoutActivity::class.java)
        showDetailsIntent.putExtra(ExerciseActivity.EXTRA_WORKOUT_DAY, workoutDay.name)
        startActivity(showDetailsIntent)
    }

    override fun onDaySelected(item: WorkoutDay?) {
        workoutDay = item!!
        initializeExercises()
        Thread.sleep(100)
        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@WorkoutDayActivity, ExerciseActivity::class.java)
        showDetailsIntent.putExtra(ExerciseActivity.EXTRA_WORKOUT_DAY, workoutDay?.id.toString())
        startActivity(showDetailsIntent)
    }
}