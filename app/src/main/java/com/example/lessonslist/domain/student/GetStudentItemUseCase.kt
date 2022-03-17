package com.example.lessonslist.domain.student

import com.example.buylistapplication.domain.StudentItem
import com.example.buylistapplication.domain.StudentListRepository


class GetStudentItemUseCase(private val studentListRepository: StudentListRepository) {

    suspend fun getStudentItem(studentItemId: Int): StudentItem {
        return studentListRepository.getStudentItem(studentItemId)
    }
}
