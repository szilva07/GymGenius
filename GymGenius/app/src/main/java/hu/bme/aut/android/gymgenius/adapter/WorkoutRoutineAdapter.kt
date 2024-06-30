package hu.bme.aut.android.gymgenius.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.gymgenius.data.table.WorkoutRoutine
import hu.bme.aut.android.gymgenius.databinding.WorkoutRoutineListBinding

class WorkoutRoutineAdapter(private val listener: WorkoutRoutineClickListener, private val selectListener: OnRoutineSelectedListener) :
    RecyclerView.Adapter<WorkoutRoutineAdapter.WorkoutRoutineViewHolder>() {

    private val items = mutableListOf<WorkoutRoutine>()

    interface OnRoutineSelectedListener {
        fun onRoutineSelected(workoutRoutine: WorkoutRoutine?)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WorkoutRoutineViewHolder(
        WorkoutRoutineListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: WorkoutRoutineViewHolder, position: Int) {
        val workoutRoutine = items[position]

        holder.bind(workoutRoutine)
        holder.binding.tvName.text = workoutRoutine.name
        holder.binding.tvSplit.text = workoutRoutine.split.toString()

        holder.binding.ibRemove.setOnClickListener(){
            var index = items.indexOf(workoutRoutine)
            items.remove(workoutRoutine)
            listener.onItemDeleted(workoutRoutine)
            notifyItemRemoved(index)
        }

        holder.binding.ibEdit.setOnClickListener(){
            listener.onItemChanged(workoutRoutine)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = items.size

    interface WorkoutRoutineClickListener {
        fun onItemChanged(item: WorkoutRoutine)
        fun onItemDeleted(item: WorkoutRoutine)
    }

    inner class WorkoutRoutineViewHolder(val binding: WorkoutRoutineListBinding) : RecyclerView.ViewHolder(binding.root){
        var item: WorkoutRoutine? = null

        init {
            binding.root.setOnClickListener { selectListener.onRoutineSelected(item) }
        }

        fun bind(newRoutine: WorkoutRoutine?) {
            item = newRoutine
        }
    }

    fun addItem(item: WorkoutRoutine) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(workoutRoutines: List<WorkoutRoutine>) {
        items.clear()
        items.addAll(workoutRoutines)
        notifyDataSetChanged()
    }
}