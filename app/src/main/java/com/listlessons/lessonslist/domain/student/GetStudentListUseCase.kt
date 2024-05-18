package com.listlessons.lessonslist.domain.student


import androidx.lifecycle.LiveData

class GetStudentItemListUseCase(private val studentListRepository: StudentListRepository) {

    fun getStudentList(): LiveData<List<StudentItem>> {
        return studentListRepository.getStudentList()
    }
}
