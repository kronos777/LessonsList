package com.example.buylistapplication.domain


import androidx.lifecycle.LiveData
import com.example.lessonslist.domain.student.StudentItem

interface StudentListRepository {

    suspend fun addStudentItem(studentItem: StudentItem)

    suspend fun deleteStudentItem(studentItem: StudentItem)

    suspend fun editStudentItem(studentItem: StudentItem)

    suspend fun getStudentItem(studentItemId: Int): StudentItem

    fun getStudentList(): LiveData<List<StudentItem>>
}
