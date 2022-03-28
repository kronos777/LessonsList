package com.example.lessonslist.domain.student



class EditStudentItemUseCase(private val studentListRepository: StudentListRepository) {

    suspend fun editStudentItem(studentItem: StudentItem) {
        studentListRepository.editStudentItem(studentItem)
    }
}
