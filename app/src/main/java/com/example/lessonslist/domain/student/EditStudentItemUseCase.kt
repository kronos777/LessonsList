package com.example.lessonslist.domain.student

import com.example.buylistapplication.domain.StudentItem
import com.example.buylistapplication.domain.StudentListRepository


class EditStudentItemUseCase(private val studentListRepository: StudentListRepository) {

    suspend fun editStudentItem(studentItem: StudentItem) {
        studentListRepository.editStudentItem(studentItem)
    }
}
