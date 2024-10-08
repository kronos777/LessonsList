package com.leslist.lessonslist.domain.student



class CheckStudentItemUseCase(private val studentListRepository: StudentListRepository) {

    suspend fun checkExistsStudentItem(studentName: String, studentLastName: String): StudentItem? {
        return studentListRepository.checkExistsStudentItem(studentName, studentLastName)
    }
}
