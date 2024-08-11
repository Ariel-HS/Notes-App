package com.example.notesapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.data.Note
import com.example.notesapp.databinding.NoteItemBinding

class NoteDetailsAdapter(private val listener : NoteDetailsClickListener) : ListAdapter<Note , NoteDetailsAdapter.NoteDetailsViewHolder>(DiffUtilCallback()) {

    inner class NoteDetailsViewHolder(private val binding : NoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.edit.setOnClickListener {
                listener.editButtonClicked(getItem(adapterPosition))
            }

            binding.delete.setOnClickListener {
                listener.deleteButtonClicked(getItem(adapterPosition))
            }
        }

        fun bind(note: Note) {
            binding.title.text = note.title
            binding.note.text = note.note
            binding.category.text = note.category
            binding.created.text = note.createdAt
            binding.updated.text = note.updatedAt
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem.nId == newItem.nId

        override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem

    }

    interface NoteDetailsClickListener {
        fun editButtonClicked(note: Note)
        fun deleteButtonClicked(note: Note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteDetailsViewHolder {
        return NoteDetailsViewHolder(NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NoteDetailsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}