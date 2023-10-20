package com.example.lessonslist.domain.group

data class GroupItem(
    val title: String,
    var description: String,
    val student: String,
    val id: Int = UNDEFINED_ID
){

    companion object {
        const val UNDEFINED_ID = 0
    }
}