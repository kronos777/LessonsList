package com.listlessons.lessonslist.domain.lessons

data class LessonsItem (
    val title: String,
    val notifications: String,
    var student: String,
    val price: Int,
    val dateStart: String,
    val dateEnd: String,
    val id: Int = UNDEFINED_ID
){
    companion object {
        const val UNDEFINED_ID = 0
    }
}