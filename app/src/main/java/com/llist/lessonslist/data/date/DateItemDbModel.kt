package com.llist.lessonslist.data.date

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "date_items")
data class DateItemDbModel (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: String
)