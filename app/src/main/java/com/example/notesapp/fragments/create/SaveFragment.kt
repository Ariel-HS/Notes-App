package com.example.notesapp.fragments.create

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.notesapp.data.Note
import com.example.notesapp.databinding.FragmentCreateBinding
import java.util.Date
import java.util.Locale

class SaveFragment() : DialogFragment() {

    private lateinit var binding: FragmentCreateBinding
    private var listener : SaveNoteListener? = null
    private var noteArg: Note? = null

    fun setListener(listener: SaveNoteListener) {
        this.listener = listener
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as SaveNoteListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateBinding.inflate(layoutInflater)

        binding.close.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                noteArg = arguments?.getParcelable("note", Note::class.java)
            else
                noteArg = arguments?.getParcelable("note")
        }

        noteArg?.let { showNoteDetails(it) }
        attachUIListener()
    }

    private fun showNoteDetails(note: Note) {
        binding.titleEntry.setText(note.title)
        binding.noteEntry.setText(note.note)
        binding.categoryEntry.setText(note.category)
        binding.createButton.text = "Update Note"
    }

    private fun attachUIListener() {
        binding.createButton.setOnClickListener {
            val title = binding.titleEntry.text.toString()
            val note = binding.noteEntry.text.toString()
            val category = binding.categoryEntry.text.toString()

            if (title.isNotEmpty() && note.isNotEmpty() && category.isNotEmpty()) {
                // get current time
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val current = dateFormat.format(Date())

                // create or update note
                val newNote = Note(noteArg?.nId ?: 0, title, note, category, noteArg?.createdAt ?: current, current)

                // add data to database
                listener?.onSaveButtonClicked(noteArg != null, newNote)
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Fill out all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val TAG = "CreateFragment"

        @JvmStatic
        fun newInstance(note: Note?) = SaveFragment().apply {
            arguments = Bundle().apply {
                putParcelable("note", note)
            }
        }
    }

    interface SaveNoteListener {
        fun onSaveButtonClicked(isUpdate: Boolean, note: Note)
    }
}