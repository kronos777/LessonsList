package com.example.lessonslist.data.student


import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.lessonslist.data.AppDatabase
import com.example.lessonslist.domain.student.StudentItem
import com.example.lessonslist.domain.student.StudentListRepository


class StudentListRepositoryImpl(
    application: Application
) : StudentListRepository {

    private val studentListDao = AppDatabase.getInstance(application).StudentListDao()
    private val mapper = StudentListMapper()

    override suspend fun addStudentItem(studentItem: StudentItem) {
        studentListDao.addStudentItem(mapper.mapEntityToDbModel(studentItem))
    }

    override suspend fun deleteStudentItem(studentItem: StudentItem) {
        studentListDao.deleteStudentItem(studentItem.id)
    }

    override suspend fun editStudentItem(studentItem: StudentItem) {
        studentListDao.addStudentItem(mapper.mapEntityToDbModel(studentItem))
    }

    override suspend fun getStudentItem(studentItemId: Int): StudentItem {
        val dbModel = studentListDao.getStudentItem(studentItemId)
        return mapper.mapDbModelToEntity(dbModel)
    }

    override suspend fun editStudentItemPaymentBalance(studentItemId: Int, paymentBalance: Int) {
        studentListDao.editStudentItemPaymentBalance(studentItemId, paymentBalance)
    }

    override suspend fun editStudentItemPhoneNumber(studentItemId: Int, phoneNumber: String) {
        studentListDao.editStudentItemPhoneNumber(studentItemId, phoneNumber)
    }

    override suspend fun checkExistsStudentItem(studentName: String, studentLastName: String): StudentItem? {
        val student = studentListDao.checkExistsStudentItem(studentName, studentLastName)
        if(student != null) {
            return mapper.mapDbModelToEntity(student)
        } else {
            return null
        }

    }


    override fun getStudentList(): LiveData<List<StudentItem>> = Transformations.map(
        studentListDao.getStudentList()
    ) {
        mapper.mapListDbModelToListEntity(it)
    }
}
