package hu.bme.aut.android.gymgenius.dialogfragment.newd

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.gymgenius.R
import hu.bme.aut.android.gymgenius.activity.ExerciseActivity
import hu.bme.aut.android.gymgenius.data.table.Exercise
import hu.bme.aut.android.gymgenius.databinding.DialogNewExerciseBinding

class NewExerciseDialogFragment : DialogFragment() {
    interface NewExerciseDialogListener {
        fun onExerciseCreated(newItem: Exercise, exerciseNumber: Int)
        fun onExerciseChosen(item: Exercise, exerciseNumber: Int)
    }

    private lateinit var listener: NewExerciseDialogListener

    private lateinit var binding: DialogNewExerciseBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewExerciseDialogListener
            ?: throw RuntimeException("Activity must implement the NewExerciseDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewExerciseBinding.inflate(LayoutInflater.from(context))
        binding.spSet.adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            resources.getStringArray(R.array.set_items)
        )
        binding.spSet.setSelection(2)

        binding.etRest.setText("120")
        binding.etNumberOfExercise.setText((ExerciseActivity.orderedExercises.size + 1).toString())

        binding.tvExercise.setTextColor(Color.rgb(200, 200, 200))
        binding.spExercise.isEnabled = false
        ExerciseActivity.exercises = ExerciseActivity.exercises.sortedBy { (it.name) }
        binding.spExercise.adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            ExerciseActivity.exercises
        )

        binding.cbIsNew.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(isChecked) {
                binding.spExercise.isEnabled = true
                binding.tvExercise.setTextColor(Color.rgb(255, 215, 0))
                binding.etName.isEnabled = false
                binding.tvName.setTextColor(Color.rgb(200, 200, 200))
                binding.etDescription.isEnabled = false
                binding.tvDescription.setTextColor(Color.rgb(200, 200, 200))
                binding.spSet.isEnabled = false
                binding.tvSet.setTextColor(Color.rgb(200, 200, 200))
                binding.etRest.isEnabled = false
                binding.tvRest.setTextColor(Color.rgb(200, 200, 200))
            }
            else {
                binding.spExercise.isEnabled = false
                binding.tvExercise.setTextColor(Color.rgb(200, 200, 200))
                binding.etName.isEnabled = true
                binding.tvName.setTextColor(Color.rgb(255, 215, 0))
                binding.etDescription.isEnabled = true
                binding.tvDescription.setTextColor(Color.rgb(255, 215, 0))
                binding.spSet.isEnabled = true
                binding.tvSet.setTextColor(Color.rgb(255, 215, 0))
                binding.etRest.isEnabled = true
                binding.tvRest.setTextColor(Color.rgb(255, 215, 0))
            }
        }

        val title = TextView(requireContext())
        title.text = "New exercise"
        title.setPadding(45, 45, 0, 45)
        title.setBackgroundColor(Color.rgb(255, 215, 0))
        title.setTextColor(Color.BLACK)
        title.setTypeface(null, Typeface.BOLD)
        title.textSize = 30f

        var dialog = AlertDialog.Builder(requireContext(), hu.bme.aut.android.gymgenius.R.style.DialogTheme)
            .setCustomTitle(title)
            .setView(binding.root)
            .setPositiveButton(hu.bme.aut.android.gymgenius.R.string.button_ok, null )
            .setNegativeButton(hu.bme.aut.android.gymgenius.R.string.button_cancel, null)
            .create()

        dialog.setOnShowListener{
            val ok = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            val cancel = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
            cancel.setTextColor(Color.BLACK)
            ok.setTextColor(Color.BLACK)

            ok.setOnClickListener {
                if (isValid()) {
                    var numberOfEx = if(binding.etNumberOfExercise.text.toString() != "") Integer.parseInt(binding.etNumberOfExercise.text.toString()) else ExerciseActivity.orderedExercises.size + 1
                    listener.onExerciseCreated(getExercise(), numberOfEx)
                }
                else if(binding.cbIsNew.isChecked){
                    if(ExerciseActivity.exercises.isNotEmpty()) {
                        var numberOfEx = if(binding.etNumberOfExercise.text.toString() != "") Integer.parseInt(binding.etNumberOfExercise.text.toString()) else ExerciseActivity.orderedExercises.size + 1
                        listener.onExerciseChosen(ExerciseActivity.exercises.get(binding.spExercise.selectedItemPosition), numberOfEx)
                    }
                }
                dismiss()
            }
        }
        return dialog
    }

    private fun isValid() = binding.etName.text.isNotEmpty()

    private fun getExercise() = Exercise(
        name = binding.etName.text.toString(),
        description = binding.etDescription.text.toString(),
        set = binding.spSet.selectedItemPosition + 1 ?: 1,
        rest  = if(binding.etRest.text.toString() != "") Integer.parseInt(binding.etRest.text.toString()) else 0
    )

    companion object {
        const val TAG = "NewExerciseDialogFragment"
    }
}