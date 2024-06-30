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
import hu.bme.aut.android.gymgenius.databinding.DialogSkipExerciseBinding

class SkipExerciseDialogFragment : DialogFragment() {
    interface SkipExerciseDialogListener {
        fun onRestTimeSkipped()
        fun onExercisePushedToEnd()
        fun onExerciseSkipped()
        fun onWorkoutFinished()
    }

    companion object {
        const val TAG = "SkipExerciseDialogFragment"
    }

    private lateinit var listener: SkipExerciseDialogListener

    private lateinit var binding: DialogSkipExerciseBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? SkipExerciseDialogListener
            ?: throw RuntimeException("Activity must implement the SkipExerciseDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogSkipExerciseBinding.inflate(LayoutInflater.from(context))

        binding.rbPushToEnd.isChecked = true

        val title = TextView(requireContext())
        title.text = "Skip exercise"
        title.setPadding(45, 45, 0, 45)
        title.setBackgroundColor(Color.rgb(255, 215, 0))
        title.setTextColor(Color.BLACK)
        title.setTypeface(null, Typeface.BOLD)
        title.textSize = 30f

        var dialog = AlertDialog.Builder(requireContext(), R.style.DialogTheme)
            .setCustomTitle(title)
            .setView(binding.root)
            .setPositiveButton(R.string.button_ok, null )
            .setNegativeButton(hu.bme.aut.android.gymgenius.R.string.button_cancel, null)
            .create()

        dialog.setOnShowListener{
            val ok = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            val cancel = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
            cancel.setTextColor(Color.BLACK)
            ok.setTextColor(Color.BLACK)

            ok.setOnClickListener {
                if (binding.rbSkipRestTime.isChecked) {
                    listener.onRestTimeSkipped()
                }
                if (binding.rbPushToEnd.isChecked) {
                    listener.onExercisePushedToEnd()
                }
                else if(binding.rbSkipExercise.isChecked){
                    listener.onExerciseSkipped()
                }
                else if(binding.rbFinishWorkout.isChecked){
                    listener.onWorkoutFinished()
                }
                dismiss()
            }
        }

        return dialog
    }
}
