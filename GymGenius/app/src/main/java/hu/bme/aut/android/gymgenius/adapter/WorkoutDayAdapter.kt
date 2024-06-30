package hu.bme.aut.android.gymgenius.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.gymgenius.activity.WorkoutDayActivity
import hu.bme.aut.android.gymgenius.data.table.WorkoutDay
import hu.bme.aut.android.gymgenius.databinding.WorkoutDayListBinding

class WorkoutDayAdapter(private val listener: WorkoutDayClickListener, private val selectListener: OnDaySelectedListener) :
    RecyclerView.Adapter<WorkoutDayAdapter.WorkoutDayViewHolder>() {

    private val items = mutableListOf<WorkoutDay>()

    interface OnDaySelectedListener {
        fun onDaySelected(workoutDay: WorkoutDay?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WorkoutDayViewHolder(
        WorkoutDayListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: WorkoutDayViewHolder, position: Int) {
        val workoutDay = items[position]

        holder.bind(workoutDay)
        holder.binding.tvName.text = workoutDay.name

        holder.binding.ibRemove.setOnClickListener(){
            var index = items.indexOf(workoutDay)
            items.remove(workoutDay)
            listener.onItemDeleted(workoutDay)
            notifyItemRemoved(index)
        }

        holder.binding.ibEdit.setOnClickListener(){
            listener.onItemChanged(workoutDay)
            notifyDataSetChanged()
        }

        holder.binding.bStart.setOnClickListener(){
            listener.onItemStarted(workoutDay)
        }

        holder.binding.bStart.isEnabled = WorkoutDayActivity.buttonValues[position]
        if(WorkoutDayActivity.buttonValues[position]) holder.binding.bStart.setTextColor(Color.rgb(0, 0, 0))
        else holder.binding.bStart.setTextColor(Color.rgb(255, 215, 0))
    }

    override fun getItemCount(): Int = items.size

    interface WorkoutDayClickListener {
        fun onItemChanged(item: WorkoutDay)
        fun onItemDeleted(item: WorkoutDay)
        fun onItemStarted(item: WorkoutDay)
    }

    inner class WorkoutDayViewHolder(val binding: WorkoutDayListBinding) : RecyclerView.ViewHolder(binding.root){
        var item: WorkoutDay? = null

        init {
            binding.root.setOnClickListener { selectListener.onDaySelected(item) }
        }

        fun bind(newDay: WorkoutDay?) {
            item = newDay
        }
    }

    fun addItem(item: WorkoutDay) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(workoutDays: List<WorkoutDay>) {
        items.clear()
        items.addAll(workoutDays)
        notifyDataSetChanged()
    }
}