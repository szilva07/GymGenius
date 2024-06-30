package hu.bme.aut.android.gymgenius.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.gymgenius.activity.ExerciseActivity
import hu.bme.aut.android.gymgenius.data.table.Exercise
import hu.bme.aut.android.gymgenius.databinding.ExerciseListBinding

class ExerciseAdapter(private val listener: ExerciseClickListener, private val selectListener: OnExerciseSelectedListener) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    private val items = mutableListOf<Exercise>()

    private var orderedExercises = ExerciseActivity.orderedExercises

    interface OnExerciseSelectedListener {
        fun onExerciseSelected(exercise: Exercise?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ExerciseViewHolder(
        ExerciseListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        orderedExercises = ExerciseActivity.orderedExercises
        val exercise = items[position]
        var exercises = items.toMutableList()
        val exercisesById = orderedExercises.associateBy { it.getExercise().id }
        val sortedExercises = exercises.map { exercisesById[it.id] }

        holder.bind(exercise)
        holder.binding.tvName.text = exercise.name
        holder.binding.tvDescription.text = exercise.description
        holder.binding.tvSets.text = exercise.set.toString()
        holder.binding.tvRestTime.text = exercise.rest.toString()
        holder.binding.tvNumberOfEx.text = sortedExercises[position]?.getNumberOfExercise().toString()

        holder.binding.ibRemove.setOnClickListener(){
            listener.onItemDeleted(exercise)
        }

        holder.binding.ibEdit.setOnClickListener(){
            listener.onItemChanged(exercise)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = items.size

    interface ExerciseClickListener {
        fun onItemChanged(item: Exercise)
        fun onItemDeleted(item: Exercise)
    }

    inner class ExerciseViewHolder(val binding: ExerciseListBinding) : RecyclerView.ViewHolder(binding.root) {
        var item: Exercise? = null

        init {
            binding.root.setOnClickListener { selectListener.onExerciseSelected(item) }
        }

        fun bind(newExercise: Exercise?) {
            item = newExercise
        }
    }

    fun addItem(item: Exercise) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(exercises: List<Exercise>) {
        items.clear()
        items.addAll(exercises)
        notifyDataSetChanged()
    }
}