package com.example.lessonslist.domain.student


import androidx.lifecycle.LiveData
import com.example.lessonslist.domain.student.StudentItem

interface StudentListRepository {

    suspend fun addStudentItem(studentItem: StudentItem)

    suspend fun deleteStudentItem(studentItem: StudentItem)

    suspend fun editStudentItem(studentItem: StudentItem)

    suspend fun editStudentItemPaymentBalance(studentItemId: Int, paymentBalance: Float)

    suspend fun getStudentItem(studentItemId: Int): StudentItem

    fun getStudentList(): LiveData<List<StudentItem>>
}
