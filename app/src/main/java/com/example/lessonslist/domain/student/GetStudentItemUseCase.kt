package com.example.lessonslist.domain.student



class GetStudentItemUseCase(private val studentListRepository: StudentListRepository) {

    suspend fun getStudentItem(studentItemId: Int): StudentItem {
        return studentListRepository.getStudentItem(studentItemId)
    }
}
