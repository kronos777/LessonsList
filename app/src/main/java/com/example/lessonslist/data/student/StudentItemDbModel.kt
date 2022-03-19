package com.example.lessonslist.data.student


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_items")
data class StudentItemDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
	val paymentBalance: Float,
    val name: String,
    val lastname: String,
    val group: ArrayList<Int>,
    val notes: ArrayList<String>,
    val enabled: Boolean
)
