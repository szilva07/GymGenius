package hu.bme.aut.android.gymgenius.dialogfragment.edit

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
import hu.bme.aut.android.gymgenius.activity.WorkoutDayActivity
import hu.bme.aut.android.gymgenius.databinding.DialogEditWorkoutDayBinding

class EditWorkoutDayDialogFragment : DialogFragment() {
    interface EditWorkoutDayDialogListener {
        fun onWorkoutDayEdited()
    }

    private lateinit var listener: EditWorkoutDayDialogListener

    private lateinit var binding: DialogEditWorkoutDayBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? EditWorkoutDayDialogListener
            ?: throw RuntimeException("Activity must implement the EditWorkoutDayDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogEditWorkoutDayBinding.inflate(LayoutInflater.from(context))
        binding.etName.setText(WorkoutDayActivity.workoutDay.name)

        val title = TextView(requireContext())
        title.text = "Edit workout day"
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
                if (isValid()) {
                    getWorkoutDay()
                    listener.onWorkoutDayEdited()
                }
                dismiss()
            }
        }
        return dialog
    }

    private fun isValid() = binding.etName.text.isNotEmpty()

    private fun getWorkoutDay() {
        WorkoutDayActivity.workoutDay.name  = binding.etName.text.toString()
    }

    companion object {
        const val TAG = "EditWorkoutDayDialogFragment"
    }
}