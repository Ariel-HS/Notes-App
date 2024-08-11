package com.example.notesapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert
    suspend fun createNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM notes WHERE nId = :nId")
    suspend fun deleteNoteById(nId : Int)

    @Query("SELECT * FROM notes")
    fun readAllNotes() : Flow<MutableList<Note>>

    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()
}