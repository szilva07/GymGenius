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
import hu.bme.aut.android.gymgenius.data.table.WorkoutRoutine
import hu.bme.aut.android.gymgenius.databinding.DialogNewWorkoutRoutineBinding

class NewWorkoutRoutineDialogFragment : DialogFragment() {
    interface NewWorkoutRoutineDialogListener {
        fun onWorkoutRoutineCreated(newItem: WorkoutRoutine)
    }

    private lateinit var listener: NewWorkoutRoutineDialogListener

    private lateinit var binding: DialogNewWorkoutRoutineBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewWorkoutRoutineDialogListener
            ?: throw RuntimeException("Activity must implement the NewWorkoutRoutineDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewWorkoutRoutineBinding.inflate(LayoutInflater.from(context))
        binding.spSplit.adapter = ArrayAdapter(
            requireContext(),
            hu.bme.aut.android.gymgenius.R.layout.spinner_item,
            resources.getStringArray(hu.bme.aut.android.gymgenius.R.array.split_items)
        )

        val title = TextView(requireContext())
        title.text = "New workout routine"
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
                    listener.onWorkoutRoutineCreated(getWorkoutRoutine())
                }
                dismiss()
            }
        }
        return dialog
    }

    private fun isValid() = binding.etName.text.isNotEmpty()

    private fun getWorkoutRoutine() = WorkoutRoutine(
        name = binding.etName.text.toString(),
        split = WorkoutRoutine.Split.getByOrdinal(binding.spSplit.selectedItemPosition) ?: WorkoutRoutine.Split.Other
    )

    companion object {
        const val TAG = "NewWorkoutRoutineDialogFragment"
    }
}