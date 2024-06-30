package hu.bme.aut.android.gymgenius.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.gymgenius.data.table.ExercisePerformance
import hu.bme.aut.android.gymgenius.databinding.ExercisePerformanceListBinding

class ExercisePerformanceAdapter(private val listener: ExercisePerformanceClickListener) :
    RecyclerView.Adapter<ExercisePerformanceAdapter.ExercisePerformanceViewHolder>() {

    private val items = mutableListOf<ExercisePerformance>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ExercisePerformanceViewHolder(
        ExercisePerformanceListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ExercisePerformanceViewHolder, position: Int) {
        val exercisePerformance = items[position]

        holder.binding.tvDate.text = exercisePerformance.date.toString()
        holder.binding.tvSet1Rep.text = exercisePerformance.set1RepCount.toString()
        holder.binding.tvSet1Weight.text = exercisePerformance.set1Weight.toString()

        holder.binding.tvSet2Rep.text = if(exercisePerformance.set2RepCount != 0) exercisePerformance.set2RepCount.toString() else ""
        holder.binding.tvSet2Weight.text = if(exercisePerformance.set2RepCount != 0) exercisePerformance.set2Weight.toString() else ""
        holder.binding.tvSet3Rep.text = if(exercisePerformance.set3RepCount != 0) exercisePerformance.set3RepCount.toString() else ""
        holder.binding.tvSet3Weight.text = if(exercisePerformance.set3RepCount != 0) exercisePerformance.set3Weight.toString() else ""
        holder.binding.tvSet4Rep.text = if(exercisePerformance.set4RepCount != 0) exercisePerformance.set4RepCount.toString() else ""
        holder.binding.tvSet4Weight.text = if(exercisePerformance.set4RepCount != 0) exercisePerformance.set4Weight.toString() else ""
        holder.binding.tvSet5Rep.text = if(exercisePerformance.set5RepCount != 0) exercisePerformance.set5RepCount.toString() else ""
        holder.binding.tvSet5Weight.text = if(exercisePerformance.set5RepCount != 0) exercisePerformance.set5Weight.toString() else ""
        holder.binding.tvSet6Rep.text = if(exercisePerformance.set6RepCount != 0) exercisePerformance.set6RepCount.toString() else ""
        holder.binding.tvSet6Weight.text = if(exercisePerformance.set6RepCount != 0) exercisePerformance.set6Weight.toString() else ""
        holder.binding.tvSet7Rep.text = if(exercisePerformance.set7RepCount != 0) exercisePerformance.set7RepCount.toString() else ""
        holder.binding.tvSet7Weight.text = if(exercisePerformance.set7RepCount != 0) exercisePerformance.set7Weight.toString() else ""

        holder.binding.ibRemove.setOnClickListener(){
            var index = items.indexOf(exercisePerformance)
            items.remove(exercisePerformance)
            listener.onItemDeleted(exercisePerformance)
            notifyItemRemoved(index)
        }
    }

    override fun getItemCount(): Int = items.size

    interface ExercisePerformanceClickListener {
        fun onItemChanged(item: ExercisePerformance)
        fun onItemDeleted(item: ExercisePerformance)
    }

    inner class ExercisePerformanceViewHolder(val binding: ExercisePerformanceListBinding) : RecyclerView.ViewHolder(binding.root)

    fun addItem(item: ExercisePerformance) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(exercisesPerformance: List<ExercisePerformance>) {
        items.clear()
        items.addAll(exercisesPerformance)
        notifyDataSetChanged()
    }
}