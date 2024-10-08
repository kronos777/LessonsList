package com.leslist.lessonslist.domain.notes

data class NotesItem(
    val text: String,
    val date: String,
    val student: Int,
    val id: Int = UNDEFINED_ID
){

    companion object {
        const val UNDEFINED_ID = 0
    }
}