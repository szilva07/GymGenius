package hu.bme.aut.android.gymgenius.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.gymgenius.activity.ExerciseActivity
import hu.bme.aut.android.gymgenius.activity.StartWorkoutActivity
import hu.bme.aut.android.gymgenius.activity.WorkoutResultActivity
import hu.bme.aut.android.gymgenius.data.table.Exercise
import hu.bme.aut.android.gymgenius.data.table.ExercisePerformance
import hu.bme.aut.android.gymgenius.databinding.WorkoutResultListBinding

class WorkoutResultAdapter : RecyclerView.Adapter<WorkoutResultAdapter.WorkoutResultViewHolder>() {

    private val items = mutableListOf<Exercise>()

    private var orderedExercises = ExerciseActivity.orderedExercises

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WorkoutResultViewHolder(
        WorkoutResultListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: WorkoutResultViewHolder, position: Int) {
        orderedExercises = ExerciseActivity.orderedExercises
        val exercise = items[position]

        holder.bind(exercise)
        holder.binding.tvName.text = exercise.name

        var prevResults = "Previous results:\n"
        var newResults = ""

        if (StartWorkoutActivity.doneExercises.contains(items[position])) {
            var index = 17
            var prevPerformances = WorkoutResultActivity.performances.filter { (it.exerciseId == items[position].id) }
            var prevPerformance: ExercisePerformance
            newResults = "Today's results:\n"
            var newPerformance = prevPerformances[prevPerformances.size - 1]

            newResults = if (newPerformance.set1RepCount > 0) newResults + newPerformance.set1RepCount + " x " + newPerformance.set1Weight + "\n" else newResults
            newResults = if (newPerformance.set2RepCount > 0) newResults + newPerformance.set2RepCount + " x " + newPerformance.set2Weight + "\n" else newResults
            newResults = if (newPerformance.set3RepCount > 0) newResults + newPerformance.set3RepCount + " x " + newPerformance.set3Weight + "\n" else newResults
            newResults = if (newPerformance.set4RepCount > 0) newResults + newPerformance.set4RepCount + " x " + newPerformance.set4Weight + "\n" else newResults
            newResults = if (newPerformance.set5RepCount > 0) newResults + newPerformance.set5RepCount + " x " + newPerformance.set5Weight + "\n" else newResults
            newResults = if (newPerformance.set6RepCount > 0) newResults + newPerformance.set6RepCount + " x " + newPerformance.set6Weight + "\n" else newResults
            newResults = if (newPerformance.set7RepCount > 0) newResults + newPerformance.set7RepCount + " x " + newPerformance.set7Weight + "\n" else newResults

            var spannable = SpannableStringBuilder(newResults)

            if(prevPerformances.size > 1) {
                prevPerformance = prevPerformances[prevPerformances.size - 2]

                var prevReps: MutableList<Int> = ArrayList<Int>()
                prevReps.add(prevPerformance.set1RepCount)
                prevReps.add(prevPerformance.set2RepCount)
                prevReps.add(prevPerformance.set3RepCount)
                prevReps.add(prevPerformance.set4RepCount)
                prevReps.add(prevPerformance.set5RepCount)
                prevReps.add(prevPerformance.set6RepCount)
                prevReps.add(prevPerformance.set7RepCount)

                var prevWeights: MutableList<Double> = ArrayList<Double>()
                prevWeights.add(prevPerformance.set1Weight)
                prevWeights.add(prevPerformance.set2Weight)
                prevWeights.add(prevPerformance.set3Weight)
                prevWeights.add(prevPerformance.set4Weight)
                prevWeights.add(prevPerformance.set5Weight)
                prevWeights.add(prevPerformance.set6Weight)
                prevWeights.add(prevPerformance.set7Weight)

                var newReps: MutableList<Int> = ArrayList<Int>()
                newReps.add(newPerformance.set1RepCount)
                newReps.add(newPerformance.set2RepCount)
                newReps.add(newPerformance.set3RepCount)
                newReps.add(newPerformance.set4RepCount)
                newReps.add(newPerformance.set5RepCount)
                newReps.add(newPerformance.set6RepCount)
                newReps.add(newPerformance.set7RepCount)

                var newWeights: MutableList<Double> = ArrayList<Double>()
                newWeights.add(newPerformance.set1Weight)
                newWeights.add(newPerformance.set2Weight)
                newWeights.add(newPerformance.set3Weight)
                newWeights.add(newPerformance.set4Weight)
                newWeights.add(newPerformance.set5Weight)
                newWeights.add(newPerformance.set6Weight)
                newWeights.add(newPerformance.set7Weight)

                for(i in 0..6){
                    prevResults = if (prevReps[i] > 0) prevResults + prevReps[i] + " x " + prevWeights[i] + "\n" else prevResults
                }

                for (i in 0..6){
                    if((newReps[i] > prevReps[i] && newWeights[i] == prevWeights[i])
                        || newWeights[i] > prevWeights[i]){
                        spannable.setSpan(
                            ForegroundColorSpan(Color.rgb(0, 255, 0)),
                            index,
                            index + newReps[i].toString().length + newWeights[i].toString().length + 4,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                    }
                    index += newReps[i].toString().length + newWeights[i].toString().length + 4
                }
            }
            else{
                prevResults = "Previous results:\nnone"
                spannable.setSpan(
                    ForegroundColorSpan(Color.rgb(0, 255, 0)),
                    17,
                    spannable.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
            }

            spannable.trim()
            holder.binding.tvNewResults.text = spannable

            prevResults = prevResults.trim()
            spannable = SpannableStringBuilder(prevResults)
            holder.binding.tvPrevResults.text = spannable

        }
        else{
            var prevPerformances = WorkoutResultActivity.performances.filter { (it.exerciseId == items[position].id) }
            if(prevPerformances.isNotEmpty()) {
                var prevPerformance = prevPerformances[prevPerformances.size - 1]
                prevResults = if (prevPerformance.set1RepCount > 0) prevResults + prevPerformance.set1RepCount + " x " + prevPerformance.set1Weight + "\n" else prevResults
                prevResults = if (prevPerformance.set2RepCount > 0) prevResults + prevPerformance.set2RepCount + " x " + prevPerformance.set2Weight + "\n" else prevResults
                prevResults = if (prevPerformance.set3RepCount > 0) prevResults + prevPerformance.set3RepCount + " x " + prevPerformance.set3Weight + "\n" else prevResults
                prevResults = if (prevPerformance.set4RepCount > 0) prevResults + prevPerformance.set4RepCount + " x " + prevPerformance.set4Weight + "\n" else prevResults
                prevResults = if (prevPerformance.set5RepCount > 0) prevResults + prevPerformance.set5RepCount + " x " + prevPerformance.set5Weight + "\n" else prevResults
                prevResults = if (prevPerformance.set6RepCount > 0) prevResults + prevPerformance.set6RepCount + " x " + prevPerformance.set6Weight + "\n" else prevResults
                prevResults = if (prevPerformance.set7RepCount > 0) prevResults + prevPerformance.set7RepCount + " x " + prevPerformance.set7Weight + "\n" else prevResults
            }
            else
                prevResults = "Previous results:\nnone"

            prevResults = prevResults.trim()
            var spannable = SpannableStringBuilder(prevResults)

            spannable.setSpan(
                ForegroundColorSpan(Color.rgb(200, 200, 200)),
                0,
                spannable.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )

            holder.binding.tvPrevResults.text = spannable

            newResults = newResults.trim()
            spannable = SpannableStringBuilder(newResults)
            holder.binding.tvNewResults.text = spannable

            holder.binding.tvName.setTextColor(Color.rgb(200, 200, 200))
        }
    }

    override fun getItemCount(): Int = items.size

    inner class WorkoutResultViewHolder(val binding: WorkoutResultListBinding) : RecyclerView.ViewHolder(binding.root) {
        var item: Exercise? = null

        fun bind(newExercise: Exercise?) {
            item = newExercise
        }
    }

    fun update(exercises: List<Exercise>) {
        items.clear()
        items.addAll(exercises)
        notifyDataSetChanged()
    }
}