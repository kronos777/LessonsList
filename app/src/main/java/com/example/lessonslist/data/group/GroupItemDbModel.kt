package com.example.lessonslist.data.group

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lessonslist.domain.group.GroupItem

@Entity(tableName = "group_items")
data class GroupItemDbModel (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val description: String,
    val student: String
)