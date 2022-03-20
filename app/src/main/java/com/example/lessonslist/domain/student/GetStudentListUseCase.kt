package com.example.lessonslist.domain.student


import androidx.lifecycle.LiveData
import com.example.buylistapplication.domain.StudentListRepository

class GetStudentItemListUseCase(private val studentListRepository: StudentListRepository) {

    fun getStudentList(): LiveData<List<StudentItem>> {
        return studentListRepository.getStudentList()
    }
}
