package com.leslist.lessonslist.domain.student



class DeleteStudentItemUseCase(private val studentListRepository: StudentListRepository) {

    suspend fun deleteStudentItem(studentItem: StudentItem) {
        studentListRepository.deleteStudentItem(studentItem)
    }
}
