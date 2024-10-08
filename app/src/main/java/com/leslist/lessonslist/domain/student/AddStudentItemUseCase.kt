package com.leslist.lessonslist.domain.student




class AddStudentItemUseCase(private val studentListRepository: StudentListRepository) {

    suspend fun addStudentItem(studentItem: StudentItem) {
        studentListRepository.addStudentItem(studentItem)
    }
}
