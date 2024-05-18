package com.listlessons.lessonslist.data.student


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_items")
data class StudentItemDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
	val paymentBalance: Int,
    val name: String,
    val lastname: String,
    val group: String,
    val image: String,
    val notes: String,
    val telephone: String,
    val enabled: Boolean
)
