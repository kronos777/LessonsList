package com.example.lessonslist.data.notes

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lessonslist.domain.group.GroupItem

@Entity(tableName = "notes_item")
data class NotesItemDbModel (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val text: String,
    val date: String,
    val student: Int
)