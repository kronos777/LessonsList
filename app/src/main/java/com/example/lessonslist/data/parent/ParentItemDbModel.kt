package com.example.lessonslist.data.parent

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lessonslist.domain.group.GroupItem

@Entity(tableName = "parent_contact")
data class ParentItemDbModel (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val number: String,
    val student: Int
)