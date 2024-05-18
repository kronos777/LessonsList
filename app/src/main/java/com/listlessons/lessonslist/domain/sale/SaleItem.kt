package com.listlessons.lessonslist.domain.sale

import com.listlessons.lessonslist.domain.notes.NotesItem

data class SaleItem(
    val idStudent: Int,
    val idLessons: Int,
    val price: Int,
    val id: Int = NotesItem.UNDEFINED_ID
) {
    companion object {
        const val UNDEFINED_ID = 0
    }
}