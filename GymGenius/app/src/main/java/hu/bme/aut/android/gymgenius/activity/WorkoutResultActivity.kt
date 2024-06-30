package hu.bme.aut.android.gymgenius.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.gymgenius.R
import hu.bme.aut.android.gymgenius.adapter.WorkoutResultAdapter
import hu.bme.aut.android.gymgenius.data.WorkoutDatabase
import hu.bme.aut.android.gymgenius.data.table.Exercise
import hu.bme.aut.android.gymgenius.data.table.ExercisePerformance
import hu.bme.aut.android.gymgenius.databinding.ActivityWorkoutResultBinding
import kotlin.concurrent.thread

class WorkoutResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorkoutResultBinding
    private lateinit var database: WorkoutDatabase
    private lateinit var adapter: WorkoutResultAdapter
    private lateinit var recyclerView: RecyclerView

    private var day: String? = null

    companion object {
        private const val TAG = "WorkoutResultActivity"
        const val EXTRA_WORKOUT_DAY = "extra.workout_day"
        lateinit var performances: List<ExercisePerformance>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWorkoutResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        database = WorkoutDatabase.getDatabase(applicationContext)

        day = intent.getStringExtra(EXTRA_WORKOUT_DAY)

        binding.bOk.setOnClickListener{
            finish()
        }

        supportActionBar?.title = getString(R.string.day, WorkoutDayActivity.workoutDay.name)

        thread {
            performances = database.exercisePerformanceDao().getAll()
        }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView = binding.rvMain
        adapter = WorkoutResultAdapter()
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        binding.rvMain.adapter = adapter

        var sortedExercises = ArrayList<Exercise>()
        var sortedOrderedExercises = ExerciseActivity.orderedExercises
        sortedOrderedExercises.sortBy { (it.getNumberOfExercise()) }
        for(ex in sortedOrderedExercises) {
            sortedExercises.add(ex.getExercise())
        }
        var doneSortedExercises = ArrayList<Exercise>()
        doneSortedExercises.clear()
        doneSortedExercises.addAll(StartWorkoutActivity.doneExercises)
        for(ex in sortedExercises) {
            if (!(doneSortedExercises.contains(ex)))
                doneSortedExercises.add(ex)
        }
        doneSortedExercises.distinct()

        adapter.update(doneSortedExercises)
    }
}