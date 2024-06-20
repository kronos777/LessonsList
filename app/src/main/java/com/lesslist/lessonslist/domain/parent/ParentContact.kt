package com.lesslist.lessonslist.domain.parent

data class ParentContact(
    val name: String,
    val number: String,
    val student: Int,
    val id: Int = UNDEFINED_ID
){

    companion object {
        const val UNDEFINED_ID = 0
    }
}