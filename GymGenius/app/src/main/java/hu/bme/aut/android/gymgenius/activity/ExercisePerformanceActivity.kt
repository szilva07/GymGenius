package hu.bme.aut.android.gymgenius.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.gymgenius.R
import hu.bme.aut.android.gymgenius.adapter.ExercisePerformanceAdapter
import hu.bme.aut.android.gymgenius.data.WorkoutDatabase
import hu.bme.aut.android.gymgenius.data.table.ExercisePerformance
import hu.bme.aut.android.gymgenius.databinding.ActivityExercisePerformanceBinding
import kotlin.concurrent.thread

class ExercisePerformanceActivity : AppCompatActivity(), ExercisePerformanceAdapter.ExercisePerformanceClickListener {
    private lateinit var binding: ActivityExercisePerformanceBinding
    private lateinit var database: WorkoutDatabase
    private lateinit var adapter: ExercisePerformanceAdapter

    private var exercise: String? = null

    companion object {
        private const val TAG = "ExercisePerformanceActivity"
        const val EXTRA_EXERCISE_NAME = "extra.exercise"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExercisePerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        database = WorkoutDatabase.getDatabase(applicationContext)

        exercise = intent.getStringExtra(EXTRA_EXERCISE_NAME)

        supportActionBar?.title = getString(R.string.exercise, ExerciseActivity.exercise.name)
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
        adapter = ExercisePerformanceAdapter(this)
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        binding.rvMain.adapter = adapter
        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        thread {
            var items = database.exercisePerformanceDao().getAll()
            items = items.filter { (it.exerciseId.toString() == exercise) }
            runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onItemChanged(item: ExercisePerformance) {
        thread {
            database.exercisePerformanceDao().update(item)
            Log.d("ExercisePerformanceActivity", "ExercisePerformance update was successful")
        }
    }

    override fun onItemDeleted(item: ExercisePerformance) {
        thread {
            database.exercisePerformanceDao().deleteItem(item)
        }
    }
}