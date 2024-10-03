package com.llist.lessonslist.domain.date

data class DateItem(
    val date: String,
    val id: Int = UNDEFINED_ID
){
    companion object {
        const val UNDEFINED_ID = 0
    }
}