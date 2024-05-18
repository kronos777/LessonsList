package com.listlessons.lessonslist.data.lessons

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "lessons_items")
data class LessonsItemDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val notifications: String,
    val student: String,
    val price: Int,
    val dateStart: String,
    //val dateStart: Date,
    val dateEnd: String
    //val dateEnd: Date
    )
