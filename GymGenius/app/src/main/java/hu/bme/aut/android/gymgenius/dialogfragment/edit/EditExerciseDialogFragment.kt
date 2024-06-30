package hu.bme.aut.android.gymgenius.dialogfragment.edit

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
import hu.bme.aut.android.gymgenius.activity.ExerciseActivity
import hu.bme.aut.android.gymgenius.databinding.DialogEditExerciseBinding

class EditExerciseDialogFragment : DialogFragment() {
    interface EditExerciseDialogListener {
        fun onExerciseEdited(exerciseNumber: Int)
    }

    private lateinit var listener: EditExerciseDialogListener

    private lateinit var binding: DialogEditExerciseBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? EditExerciseDialogListener
            ?: throw RuntimeException("Activity must implement the EditExerciseDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogEditExerciseBinding.inflate(LayoutInflater.from(context))
        binding.spSet.adapter = ArrayAdapter(
            requireContext(),
            hu.bme.aut.android.gymgenius.R.layout.spinner_item,
            resources.getStringArray(hu.bme.aut.android.gymgenius.R.array.set_items)
        )

        binding.etName.setText(ExerciseActivity.exercise.name)
        binding.etDescription.setText(ExerciseActivity.exercise.description)
        binding.spSet.setSelection(ExerciseActivity.exercise.set - 1)
        binding.etRest.setText(ExerciseActivity.exercise.rest.toString())
        binding.etNumberOfExercise.setText(ExerciseActivity.numberOfExercise.toString())

        val title = TextView(requireContext())
        title.text = "Edit exercise"
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
                    getExercise()
                    var numberOfEx = if(binding.etNumberOfExercise.text.toString() != "") Integer.parseInt(binding.etNumberOfExercise.text.toString()) else ExerciseActivity.numberOfExercise
                    listener.onExerciseEdited(numberOfEx)
                }
                dismiss()
            }
        }
        return dialog
    }

    private fun isValid() = binding.etName.text.isNotEmpty()

    private fun getExercise() {
        ExerciseActivity.exercise.name  = binding.etName.text.toString()
        ExerciseActivity.exercise.description  = binding.etDescription.text.toString()
        ExerciseActivity.exercise.set = binding.spSet.selectedItemPosition + 1 ?: 1
        ExerciseActivity.exercise.rest  = if(binding.etRest.text.toString() != "") Integer.parseInt(binding.etRest.text.toString()) else ExerciseActivity.numberOfExercise
    }

    companion object {
        const val TAG = "EditExerciseDialogFragment"
    }
}