package com.leslist.lessonslist.domain.student


import androidx.lifecycle.LiveData

interface StudentListRepository {

    suspend fun addStudentItem(studentItem: StudentItem)

    suspend fun deleteStudentItem(studentItem: StudentItem)

    suspend fun editStudentItem(studentItem: StudentItem)

    suspend fun editStudentItemPaymentBalance(studentItemId: Int, paymentBalance: Int)

    suspend fun editStudentItemPhoneNumber(studentItemId: Int, phoneNumber: String)

    suspend fun getStudentItem(studentItemId: Int): StudentItem
    suspend fun checkExistsStudentItem(studentName: String, studentLastName: String): StudentItem?
    fun getStudentList(): LiveData<List<StudentItem>>
}
