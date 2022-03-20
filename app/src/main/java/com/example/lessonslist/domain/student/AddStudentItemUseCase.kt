package com.example.lessonslist.domain.student


import com.example.buylistapplication.domain.StudentListRepository


class AddStudentItemUseCase(private val studentListRepository: StudentListRepository) {

    suspend fun addStudentItem(studentItem: StudentItem) {
        studentListRepository.addStudentItem(studentItem)
    }
}
