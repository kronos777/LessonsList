package com.llist.lessonslist.data.notes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_item")
data class NotesItemDbModel (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val text: String,
    val date: String,
    val student: Int
)