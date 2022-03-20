package com.example.lessonslist.domain.student


import com.example.buylistapplication.domain.StudentListRepository

class DeleteStudentItemUseCase(private val studentListRepository: StudentListRepository) {

    suspend fun deleteStudentItem(studentItem: StudentItem) {
        studentListRepository.deleteStudentItem(studentItem)
    }
}
