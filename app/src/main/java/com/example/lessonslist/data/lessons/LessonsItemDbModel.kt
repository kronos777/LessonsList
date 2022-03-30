package com.example.lessonslist.data.lessons

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "lessons_items")
data class LessonsItemDbModel(
    @PrimaryKey(autoGenerate = true)
    val title: String,
    val description: String,
    val student: String,
    val price: Int,
    val dateStart: Date,
    val dateEnd: Date,
    val id: Int
)
