package hu.bme.aut.android.gymgenius.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import hu.bme.aut.android.gymgenius.R
import hu.bme.aut.android.gymgenius.data.WorkoutDatabase
import hu.bme.aut.android.gymgenius.data.table.Exercise
import hu.bme.aut.android.gymgenius.data.table.ExercisePerformance
import hu.bme.aut.android.gymgenius.databinding.ActivityStartWorkoutBinding
import hu.bme.aut.android.gymgenius.dialogfragment.SkipExerciseDialogFragment
import java.time.LocalDate
import kotlin.concurrent.thread

class StartWorkoutActivity : AppCompatActivity(), SkipExerciseDialogFragment.SkipExerciseDialogListener {
    private lateinit var binding: ActivityStartWorkoutBinding
    private lateinit var database: WorkoutDatabase

    private var orderedExercises = ArrayList<WorkoutDayActivity.OrderedExercise>()
    private lateinit var prevPerformance: ExercisePerformance
    private lateinit var newPerformance: ExercisePerformance
    private lateinit var pickerValues: MutableList<String>
    private var day: String? = null
    private var exerciseCount = 0
    private var currentExercise = 0
    private var setCount = 0
    private var currentSet = 1
    private var done = false
    private var soundPool: SoundPool? = null
    private val soundId = 1

    companion object {
        private const val TAG = "StartWorkoutActivity"
        const val EXTRA_WORKOUT_DAY = "extra.workout_day"
        var doneExercises = ArrayList<Exercise>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool!!.load(baseContext, R.raw.bing, 1)

        binding = ActivityStartWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        database = WorkoutDatabase.getDatabase(applicationContext)

        day = intent.getStringExtra(EXTRA_WORKOUT_DAY)

        supportActionBar?.title = getString(R.string.day, WorkoutDayActivity.workoutDay.name)

        binding.bSkip.setOnClickListener{
            SkipExerciseDialogFragment().show(
                supportFragmentManager,
                SkipExerciseDialogFragment.TAG
            )
        }

        pickerValues = mutableListOf<String>()
        for(i in 1..30)
            pickerValues.add(i.toString())

        binding.npReps.minValue = 1
        binding.npReps.maxValue = 30
        binding.npReps.displayedValues = pickerValues.toTypedArray()
        binding.npReps.textColor = Color.rgb(255, 215, 0)

        doneExercises.clear()
        orderedExercises.addAll( WorkoutDayActivity.orderedExercises)

        orderedExercises.sortBy { (it.getNumberOfExercise()) }
        setCount = orderedExercises[0].getExercise().set
        exerciseCount = orderedExercises.size - 1

        binding.bDone.setOnClickListener{
            if (isValid()) {
                binding.bDone.isEnabled = false
                binding.cTimer.text = "00:00"
                binding.cTimer.isCountDown = true
                if(!(currentExercise == exerciseCount && currentSet == setCount)){
                    binding.cTimer.base = SystemClock.elapsedRealtime() + 1000 * orderedExercises[currentExercise].getExercise().rest + 500
                    binding.cTimer.start()
                    binding.cTimer.setTextColor(Color.rgb(255, 215, 0))
                }

                done = true

                if(currentSet == 1)
                    createExercisePerformance()
                else
                    updateExercisePerformance()

                WorkoutDayActivity.onExerciseDescriptionChanged(orderedExercises[currentExercise].getExercise(), binding.tvDescription.text.toString())

                if(currentSet == setCount){
                    thread {
                        database.exercisePerformanceDao().insert(newPerformance)
                    }
                    if(currentExercise == exerciseCount) {
                        startWorkoutResults()
                        finish()
                    }
                    else{
                        currentExercise++
                        currentSet = 1
                        setCount = orderedExercises[currentExercise].getExercise().set
                        initializeTexts()
                    }
                }
                else
                    currentSet++
            }
        }

        binding.cTimer.setOnChronometerTickListener {
            if(binding.cTimer.text.toString().contains("−")){
                binding.cTimer.stop()
                binding.cTimer.text = "00:00"
            }
            if(binding.cTimer.text.toString() == "00:00" && done){
                soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
                done = false
                binding.bDone.isEnabled = true
                binding.cTimer.stop()
                binding.cTimer.text = "00:00"
                binding.cTimer.setTextColor(Color.rgb(0, 255, 0))
                WorkoutDayActivity.onExerciseDescriptionChanged(orderedExercises[currentExercise].getExercise(), binding.tvDescription.text.toString())
                initializeTexts()
            }
        }

        initializeTexts()
    }

    private fun startWorkoutResults(){
        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@StartWorkoutActivity, WorkoutResultActivity::class.java)
        showDetailsIntent.putExtra(WorkoutResultActivity.EXTRA_WORKOUT_DAY, EXTRA_WORKOUT_DAY)
        startActivity(showDetailsIntent)
    }

    private fun saveExercise(){
        if(::newPerformance.isInitialized) {
            var currentEx = orderedExercises[currentExercise]
            if (newPerformance.exerciseId == currentEx.getExercise().id)
                thread {
                    database.exercisePerformanceDao().insert(newPerformance)
                }
        }
    }

    override fun onRestTimeSkipped() {
        if (done){
            soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
            done = false
            binding.bDone.isEnabled = true
            binding.cTimer.stop()
            binding.cTimer.text = "00:00"
            WorkoutDayActivity.onExerciseDescriptionChanged(orderedExercises[currentExercise].getExercise(), binding.tvDescription.text.toString())
            initializeTexts()
        }
    }

    override fun onExercisePushedToEnd() {
        saveExercise()
        var currentEx = orderedExercises[currentExercise]
        orderedExercises.remove(currentEx)
        orderedExercises.add(currentEx)
        currentSet = 1
        initializeTexts()
    }

    override fun onExerciseSkipped() {
        saveExercise()
        if(currentExercise != exerciseCount) {
            orderedExercises.remove(orderedExercises[currentExercise])
            exerciseCount--
            currentSet = 1
            initializeTexts()
        }
        else if(currentExercise == exerciseCount) {
            startWorkoutResults()
            finish()
        }
    }

    override fun onWorkoutFinished() {
        saveExercise()
        startWorkoutResults()
        finish()
    }

    private fun isValid() = binding.etWeight.text.isNotEmpty()

    private fun createExercisePerformance(){
        doneExercises.add(orderedExercises[currentExercise].getExercise())
        newPerformance = ExercisePerformance(
            exerciseId = orderedExercises[currentExercise].getExercise().id,
            date = LocalDate.now(),
            set1RepCount = Integer.parseInt(binding.npReps.value.toString()),
            set1Weight  = binding.etWeight.text.toString().toDouble(),
            set2RepCount = 0,
            set2Weight = 0.0,
            set3RepCount = 0,
            set3Weight = 0.0,
            set4RepCount = 0,
            set4Weight = 0.0,
            set5RepCount = 0,
            set5Weight = 0.0,
            set6RepCount = 0,
            set6Weight = 0.0,
            set7RepCount = 0,
            set7Weight = 0.0
        )
    }

    private fun updateExercisePerformance(){
        if (currentSet == 2) {
            newPerformance.set2RepCount = Integer.parseInt(binding.npReps.value.toString())
            newPerformance.set2Weight = binding.etWeight.text.toString().toDouble()
        }
        if (currentSet == 3) {
            newPerformance.set3RepCount = Integer.parseInt(binding.npReps.value.toString())
            newPerformance.set3Weight = binding.etWeight.text.toString().toDouble()
        }
        if (currentSet == 4) {
            newPerformance.set4RepCount = Integer.parseInt(binding.npReps.value.toString())
            newPerformance.set4Weight = binding.etWeight.text.toString().toDouble()
        }
        if (currentSet == 5) {
            newPerformance.set5RepCount = Integer.parseInt(binding.npReps.value.toString())
            newPerformance.set5Weight = binding.etWeight.text.toString().toDouble()
        }
        if (currentSet == 6) {
            newPerformance.set6RepCount = Integer.parseInt(binding.npReps.value.toString())
            newPerformance.set6Weight = binding.etWeight.text.toString().toDouble()
        }
        if (currentSet == 7) {
            newPerformance.set7RepCount = Integer.parseInt(binding.npReps.value.toString())
            newPerformance.set7Weight = binding.etWeight.text.toString().toDouble()
        }
    }

    private fun initializeTexts(){
        thread {
            var performances = database.exercisePerformanceDao().getAll()
            performances = performances.filter { (it.exerciseId == orderedExercises[currentExercise].getExercise().id) }
            runOnUiThread {
                if(performances.isNotEmpty()){
                    prevPerformance = performances[performances.size - 1]
                    binding.tvDate.setText(getString(R.string.result_date, prevPerformance.date))

                    binding.tvSet1Rep.text = if(prevPerformance.set1RepCount != 0) prevPerformance.set1RepCount.toString() else ""
                    binding.tvSet1Weight.text = if(prevPerformance.set1RepCount != 0) prevPerformance.set1Weight.toString() else ""
                    binding.tvSet2Rep.text = if(prevPerformance.set2RepCount != 0) prevPerformance.set2RepCount.toString() else ""
                    binding.tvSet2Weight.text = if(prevPerformance.set2RepCount != 0) prevPerformance.set2Weight.toString() else ""
                    binding.tvSet3Rep.text = if(prevPerformance.set3RepCount != 0) prevPerformance.set3RepCount.toString() else ""
                    binding.tvSet3Weight.text = if(prevPerformance.set3RepCount != 0) prevPerformance.set3Weight.toString() else ""
                    binding.tvSet4Rep.text = if(prevPerformance.set4RepCount != 0) prevPerformance.set4RepCount.toString() else ""
                    binding.tvSet4Weight.text = if(prevPerformance.set4RepCount != 0) prevPerformance.set4Weight.toString() else ""
                    binding.tvSet5Rep.text = if(prevPerformance.set5RepCount != 0) prevPerformance.set5RepCount.toString() else ""
                    binding.tvSet5Weight.text = if(prevPerformance.set5RepCount != 0) prevPerformance.set5Weight.toString() else ""
                    binding.tvSet6Rep.text = if(prevPerformance.set6RepCount != 0) prevPerformance.set6RepCount.toString() else ""
                    binding.tvSet6Weight.text = if(prevPerformance.set6RepCount != 0) prevPerformance.set6Weight.toString() else ""
                    binding.tvSet7Rep.text = if(prevPerformance.set7RepCount != 0) prevPerformance.set7RepCount.toString() else ""
                    binding.tvSet7Weight.text = if(prevPerformance.set7RepCount != 0) prevPerformance.set7Weight.toString() else ""
                }
                else {
                    binding.tvDate.setText("No previous results")

                    binding.tvSet1Rep.text = ""
                    binding.tvSet1Weight.text = ""
                    binding.tvSet2Rep.text = ""
                    binding.tvSet2Weight.text = ""
                    binding.tvSet3Rep.text = ""
                    binding.tvSet3Weight.text = ""
                    binding.tvSet4Rep.text = ""
                    binding.tvSet4Weight.text = ""
                    binding.tvSet5Rep.text = ""
                    binding.tvSet5Weight.text = ""
                    binding.tvSet6Rep.text = ""
                    binding.tvSet6Weight.text = ""
                    binding.tvSet7Rep.text = ""
                    binding.tvSet7Weight.text = ""
                }
            }
        }
        binding.tvName.text = orderedExercises[currentExercise].getExercise().name
        binding.tvDescription.setText(orderedExercises[currentExercise].getExercise().description)
        binding.tvSetNumber.text = getString(R.string.set_number, (currentSet).toString())
        binding.tvExercises.text = ""

        var exercises = "Exercises:\n"
        var weights = "Max weight:\n"
        var weightLengths = ArrayList<Int>()

        thread {
            for (ex in orderedExercises) {
                var performances = database.exercisePerformanceDao().getAll()
                performances = performances.filter { (it.exerciseId == ex.getExercise().id) }

                runOnUiThread {
                    var maxWeight = 0.0
                    if (performances.isNotEmpty()) {
                        prevPerformance = performances[performances.size - 1]
                        maxWeight = if (prevPerformance.set1Weight > maxWeight) prevPerformance.set1Weight else maxWeight
                        maxWeight = if (prevPerformance.set2Weight > maxWeight) prevPerformance.set2Weight else maxWeight
                        maxWeight = if (prevPerformance.set3Weight > maxWeight) prevPerformance.set3Weight else maxWeight
                        maxWeight = if (prevPerformance.set4Weight > maxWeight) prevPerformance.set4Weight else maxWeight
                        maxWeight = if (prevPerformance.set5Weight > maxWeight) prevPerformance.set5Weight else maxWeight
                        maxWeight = if (prevPerformance.set6Weight > maxWeight) prevPerformance.set6Weight else maxWeight
                        maxWeight = if (prevPerformance.set7Weight > maxWeight) prevPerformance.set7Weight else maxWeight
                    }
                    weightLengths.add(maxWeight.toString().length)
                    weights = weights + maxWeight.toString() + "\n"
                    exercises = exercises + ex.getExercise().name + "\n"
                }
            }
            runOnUiThread {
                var index = 11
                exercises = exercises.trim()
                var spannable = SpannableStringBuilder(exercises)
                var before = true

                for (ex in orderedExercises) {
                    if (before)
                        spannable.setSpan(
                            ForegroundColorSpan(Color.rgb(200, 200, 200)),
                            index,
                            index + ex.getExercise().name.length,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                    if (!before)
                        spannable.setSpan(
                            ForegroundColorSpan(Color.rgb(255, 215, 0)),
                            index,
                            index + ex.getExercise().name.length,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                    if (ex.getExercise().id == orderedExercises[currentExercise].getExercise().id) {
                        spannable.setSpan(
                            ForegroundColorSpan(Color.rgb(0, 255, 0)),
                            index,
                            index + ex.getExercise().name.length,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                        spannable.setSpan(
                            StyleSpan(Typeface.BOLD),
                            index,
                            index + ex.getExercise().name.length,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                        before = false
                    }
                    index += ex.getExercise().name.length + 1
                }

                binding.tvExercises.text = spannable
                index = 12
                weights = weights.trim()
                spannable = SpannableStringBuilder(weights)
                before = true

                for (ex in orderedExercises) {
                    if (before)
                        spannable.setSpan(
                            ForegroundColorSpan(Color.rgb(200, 200, 200)),
                            index,
                            index + weightLengths[0],
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                    if (!before)
                        spannable.setSpan(
                            ForegroundColorSpan(Color.rgb(255, 215, 0)),
                            index,
                            index + weightLengths[0],
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                    if (ex.getExercise().id == orderedExercises[currentExercise].getExercise().id) {
                        spannable.setSpan(
                            ForegroundColorSpan(Color.rgb(0, 255, 0)),
                            index,
                            index + weightLengths[0],
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                        spannable.setSpan(
                            StyleSpan(Typeface.BOLD),
                            index,
                            index + weightLengths[0],
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                        before = false
                    }
                    index += weightLengths[0] + 1
                    weightLengths.removeAt(0)
                }

                binding.tvMaxWeights.text = spannable
            }
        }
    }
}