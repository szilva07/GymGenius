package hu.bme.aut.android.gymgenius.dialogfragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.gymgenius.R
import hu.bme.aut.android.gymgenius.activity.ExerciseActivity
import hu.bme.aut.android.gymgenius.data.table.Exercise
import hu.bme.aut.android.gymgenius.databinding.DialogDeleteExerciseBinding


class DeleteExerciseDialogFragment : DialogFragment() {
    interface DeleteExerciseDialogListener {
        fun onItemDeletedFromWorkout(item: Exercise)
        fun onItemDeletedFromDatabase(item: Exercise)
    }

    companion object {
        const val TAG = "DeleteExerciseDialogFragment"
    }

    private lateinit var listener: DeleteExerciseDialogListener

    private lateinit var binding: DialogDeleteExerciseBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? DeleteExerciseDialogListener
            ?: throw RuntimeException("Activity must implement the DeleteExerciseDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogDeleteExerciseBinding.inflate(LayoutInflater.from(context))

        binding.rbRemoveFromWorkout.isChecked = true

        val title = TextView(requireContext())
        title.text = "Delete exercise"
        title.setPadding(45, 45, 0, 45)
        title.setBackgroundColor(Color.rgb(255, 215, 0))
        title.setTextColor(Color.BLACK)
        title.setTypeface(null, Typeface.BOLD)
        title.textSize = 30f

        var dialog = AlertDialog.Builder(requireContext(), R.style.DialogTheme)
            .setCustomTitle(title)
            .setView(binding.root)
            .setPositiveButton(R.string.button_ok, null )
            .setNegativeButton(R.string.button_cancel, null)
            .create()

        dialog.setOnShowListener{
            val ok = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            val cancel = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
            cancel.setTextColor(Color.BLACK)
            ok.setTextColor(Color.BLACK)

            ok.setOnClickListener {
                if (binding.rbRemoveFromWorkout.isChecked) {
                    listener.onItemDeletedFromWorkout(ExerciseActivity.exercise)
                } else if(binding.rbDeleteFromDatabase.isChecked){
                    listener.onItemDeletedFromDatabase(ExerciseActivity.exercise)
                }
                dismiss()
            }
        }
        return dialog
    }
}