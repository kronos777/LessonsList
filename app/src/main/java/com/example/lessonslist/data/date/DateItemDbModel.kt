package com.example.lessonslist.data.date

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lessonslist.domain.date.DateItem

@Entity(tableName = "date_items")
data class DateItemDbModel (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: String
)