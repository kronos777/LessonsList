package com.lesslist.lessonslist.data.sale

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "sale_item")
data class SaleItemDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val idStudent: Int,
    val idLessons: Int,
    val price: Int
)