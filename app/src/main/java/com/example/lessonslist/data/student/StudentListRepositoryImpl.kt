package com.example.lessonslist.data.student


import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.buylistapplication.domain.StudentListRepository
import com.example.lessonslist.data.AppDatabase
import com.example.lessonslist.domain.student.StudentItem


class StudentListRepositoryImpl(
    application: Application
) : StudentListRepository {

    private val studentListDao = AppDatabase.getInstance(application).StudentListDao()
    private val mapper = StudentListMapper()

    override suspend fun addStudentItem(studentItem: StudentItem) {
        studentListDao.addStudentItem(mapper.mapEntityToDbModel(StudentItem))
    }

    override suspend fun deleteStudentItem(studentItem: StudentItem) {
        studentListDao.deleteStudentItem(studentItem.id)
    }

    override suspend fun editStudentItem(studentItem: StudentItem) {
        studentListDao.addStudentItem(mapper.mapEntityToDbModel(StudentItem))
    }

    override suspend fun getStudentItem(studentItemId: Int): StudentItem {
        val dbModel = studentListDao.getStudentItem(studentItemId)
        return mapper.mapDbModelToEntity(dbModel)
    }

    override fun getStudentList(): LiveData<List<StudentItem>> = Transformations.map(
        studentListDao.getStudentList()
    ) {
        mapper.mapListDbModelToListEntity(it)
    }
}
