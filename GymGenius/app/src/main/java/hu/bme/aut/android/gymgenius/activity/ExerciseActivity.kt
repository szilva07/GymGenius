package hu.bme.aut.android.gymgenius.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.gymgenius.R
import hu.bme.aut.android.gymgenius.dialogfragment.newd.NewExerciseDialogFragment
import hu.bme.aut.android.gymgenius.adapter.ExerciseAdapter
import hu.bme.aut.android.gymgenius.data.WorkoutDatabase
import hu.bme.aut.android.gymgenius.data.table.Connect
import hu.bme.aut.android.gymgenius.data.table.Exercise
import hu.bme.aut.android.gymgenius.databinding.ActivityExerciseBinding
import hu.bme.aut.android.gymgenius.dialogfragment.DeleteExerciseDialogFragment
import hu.bme.aut.android.gymgenius.dialogfragment.edit.EditExerciseDialogFragment
import kotlin.concurrent.thread

class ExerciseActivity : AppCompatActivity(), ExerciseAdapter.OnExerciseSelectedListener, ExerciseAdapter.ExerciseClickListener,
    NewExerciseDialogFragment.NewExerciseDialogListener, EditExerciseDialogFragment.EditExerciseDialogListener, DeleteExerciseDialogFragment.DeleteExerciseDialogListener {
    private lateinit var binding: ActivityExerciseBinding
    private lateinit var database: WorkoutDatabase
    private lateinit var adapter: ExerciseAdapter

    private var day: String? = null

    companion object {
        private const val TAG = "ExerciseActivity"
        const val EXTRA_WORKOUT_DAY = "extra.workout_day"
        lateinit var exercise: Exercise
        lateinit var exercises: List<Exercise>
        var orderedExercises = WorkoutDayActivity.orderedExercises
        var numberOfExercise = 1
        lateinit var recyclerView: RecyclerView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        database = WorkoutDatabase.getDatabase(applicationContext)

        day = intent.getStringExtra(EXTRA_WORKOUT_DAY)

        binding.fab.setOnClickListener{
            NewExerciseDialogFragment().show(
                supportFragmentManager,
                NewExerciseDialogFragment.TAG
            )
        }

        supportActionBar?.title = getString(R.string.day, WorkoutDayActivity.workoutDay.name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRecyclerView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        recyclerView = binding.rvMain
        adapter = ExerciseAdapter(this, this)
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        binding.rvMain.adapter = adapter
        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        thread {
            exercises = database.exerciseDao().getAll()
        }

        var sortedExercises = ArrayList<Exercise>()
        var sortedOrderedExercises = orderedExercises
        sortedOrderedExercises.sortBy { (it.getNumberOfExercise()) }
        for(ex in sortedOrderedExercises) {
            sortedExercises.add(ex.getExercise())
        }

        runOnUiThread {
            adapter.update(sortedExercises)
        }
    }

    override fun onItemChanged(item: Exercise) {
        thread {
            exercise = item

            var connections = database.connectDao().getAll()
            connections = connections.filter { (it.exerciseId == exercise.id) && it.workoutDayId == WorkoutDayActivity.workoutDay.id }
            numberOfExercise = connections[0].exerciseNumber!!

            EditExerciseDialogFragment().show(
                supportFragmentManager,
                EditExerciseDialogFragment.TAG
            )
        }
    }

    override fun onExerciseEdited(exerciseNumber: Int) {
        thread {
            database.exerciseDao().update(exercise)

            var connections = database.connectDao().getAll()
            connections = connections.filter { (it.exerciseId == exercise.id && it.workoutDayId == WorkoutDayActivity.workoutDay.id) }
            connections[0].exerciseNumber = exerciseNumber
            database.connectDao().update(connections[0])

            saveExerciseNumber()
        }
    }

    override fun onExerciseCreated(newItem: Exercise, exerciseNumber: Int) {
        thread {
            val insertId = database.exerciseDao().insert(newItem)
            newItem.id = insertId
            WorkoutDayActivity.exercises.add(newItem)
            database.connectDao().insert(
                Connect(
                    exerciseId = newItem.id,
                    workoutDayId = day?.toLong(),
                    exerciseNumber = exerciseNumber
                )
            )

            runOnUiThread {
                adapter.addItem(newItem)
            }

            saveExerciseNumber()
        }
    }

    override fun onExerciseChosen(item: Exercise, exerciseNumber: Int) {
        thread {
            WorkoutDayActivity.exercises.add(item)
            database.connectDao().insert(
                Connect(
                    exerciseId = item.id,
                    workoutDayId = day?.toLong(),
                    exerciseNumber = exerciseNumber
                )
            )

            runOnUiThread {
                adapter.addItem(item)
            }

            saveExerciseNumber()
        }
    }

    private fun saveExerciseNumber() {
        thread {
            WorkoutDayActivity.orderedExercises.clear()
            for (ex in WorkoutDayActivity.exercises) {
                var connections = database.connectDao().getAll()
                connections = connections.filter { (it.workoutDayId == WorkoutDayActivity.workoutDay.id && it.exerciseId == ex.id) }
                WorkoutDayActivity.orderedExercises.add(WorkoutDayActivity.OrderedExercise(ex, connections[0].exerciseNumber))

                orderedExercises = WorkoutDayActivity.orderedExercises
                loadItemsInBackground()
            }
        }
    }

    override fun onItemDeleted(item: Exercise) {
        thread {
            exercise = item
            DeleteExerciseDialogFragment().show(
                supportFragmentManager,
                DeleteExerciseDialogFragment.TAG
            )
        }
    }

    override fun onItemDeletedFromWorkout(item: Exercise){
        var index = 0
        for(ex in orderedExercises){
            if(ex.getExercise().id == item.id)
                index = orderedExercises.indexOf(ex)
        }
        orderedExercises.removeAt(index)
        thread {
            WorkoutDayActivity.exercises.remove(item)
            var connections = database.connectDao().getAll()
            connections = connections.filter { (it.exerciseId == item.id && it.workoutDayId.toString() == day) }
            for(c in connections)
                database.connectDao().deleteItem(c)
            loadItemsInBackground()
        }
    }

    override fun onItemDeletedFromDatabase(item: Exercise){
        var index = 0
        for(ex in orderedExercises){
            if(ex.getExercise().id == item.id)
                index = orderedExercises.indexOf(ex)
        }
        orderedExercises.removeAt(index)
        thread {
            WorkoutDayActivity.exercises.remove(item)
            var connections = database.connectDao().getAll()
            connections = connections.filter { (it.exerciseId == item.id) }
            for(c in connections)
                database.connectDao().deleteItem(c)

            var performances = database.exercisePerformanceDao().getAll()
            performances = performances.filter { (it.exerciseId == item.id) }
            for(ep in performances)
                database.exercisePerformanceDao().deleteItem(ep)

            database.exerciseDao().deleteItem(item)
            exercises = database.exerciseDao().getAll()
            loadItemsInBackground()
        }
    }

    override fun onExerciseSelected(item: Exercise?) {
        exercise = item!!
        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@ExerciseActivity, ExercisePerformanceActivity::class.java)
        showDetailsIntent.putExtra(ExercisePerformanceActivity.EXTRA_EXERCISE_NAME, exercise?.id.toString())
        startActivity(showDetailsIntent)
    }
}