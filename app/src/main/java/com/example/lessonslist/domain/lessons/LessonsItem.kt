package com.example.lessonslist.domain.lessons

import java.util.*

data class LessonsItem (
    val title: String,
    val description: String,
    val student: String,
    val price: Int,
    val dateStart: String,
    val dateEnd: String,
    val id: Int = UNDEFINED_ID
){
    companion object {
        const val UNDEFINED_ID = 0
    }
}