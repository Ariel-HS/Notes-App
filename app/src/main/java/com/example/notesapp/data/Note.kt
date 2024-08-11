package com.example.notesapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val nId : Int,
    val title : String,
    val note : String,
    val category : String,
    val createdAt : String,
    val updatedAt : String
) : Parcelable
