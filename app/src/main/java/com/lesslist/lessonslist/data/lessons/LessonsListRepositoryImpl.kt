package com.lesslist.lessonslist.data.lessons

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.lesslist.lessonslist.data.AppDatabase
import com.lesslist.lessonslist.domain.lessons.LessonsItem
import com.lesslist.lessonslist.domain.lessons.LessonsListRepository

class LessonsListRepositoryImpl(
    application: Application,
) : LessonsListRepository {

    private val lessonsListDao = AppDatabase.getInstance(application).LessonsListDao()
    private val mapper = LessonsListMapper()


    override suspend fun addLessonsItem(lessonsItem: LessonsItem) {
        lessonsListDao.addLessonsItem(mapper.mapEntityToDbModel(lessonsItem))
    }

    override suspend fun deleteLessonsItem(lessonsItem: LessonsItem) {
        lessonsListDao.deleteLessonsItem(lessonsItem.id)
    }

    override suspend fun editLessonsItem(lessonsItem: LessonsItem) {
        lessonsListDao.addLessonsItem(mapper.mapEntityToDbModel(lessonsItem))
    }

    override suspend fun getLessonsItem(lessonsItemId: Int): LessonsItem {
        val dbModel = lessonsListDao.getLessonsItem(lessonsItemId)
        return mapper.mapDbModelToEntity(dbModel)
    }


    override fun getLessonsList(): LiveData<List<LessonsItem>> = Transformations.map(
        lessonsListDao.getLessonsList()
    ) {
        mapper.mapListDbModelToListEntity(it)
    }

    override fun getLessonsListDate(date: String): LiveData<List<LessonsItem>> = Transformations.map(
        lessonsListDao.getLessonsList()
    ) {
        mapper.mapListDbModelToListEntity(it)
    }

}
