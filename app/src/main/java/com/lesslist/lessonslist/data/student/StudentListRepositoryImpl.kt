package com.lesslist.lessonslist.data.student


import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.lesslist.lessonslist.data.AppDatabase
import com.lesslist.lessonslist.domain.student.StudentItem
import com.lesslist.lessonslist.domain.student.StudentListRepository


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
        return if(student != null) {
            mapper.mapDbModelToEntity(student)
        } else {
            null
        }

    }


    override fun getStudentList(): LiveData<List<StudentItem>> = Transformations.map(
        studentListDao.getStudentList()
    ) {
        mapper.mapListDbModelToListEntity(it)
    }
}
