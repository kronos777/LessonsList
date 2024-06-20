package com.lesslist.lessonslist.data.date

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.lesslist.lessonslist.data.AppDatabase
import com.lesslist.lessonslist.domain.date.DateItem
import com.lesslist.lessonslist.domain.date.DateListRepository

class DateListRepositoryImpl(
    application: Application,

) : DateListRepository {

    private val dateListDao = AppDatabase.getInstance(application).DateListDao()
    private val mapper = DateListMapper()

    override suspend fun addDateItem(dateItem: DateItem) {
        dateListDao.addDateItem(mapper.mapEntityToDbModel(dateItem))
    }

    override suspend fun deleteDateItem(dateItem: DateItem) {
        dateListDao.deleteDateItem(dateItem.id)
    }

    override suspend fun editDateItem(dateItem: DateItem) {
        dateListDao.addDateItem(mapper.mapEntityToDbModel(dateItem))
    }

    override suspend fun getDateItem(dateItemId: Int): DateItem {
        val dbModel = dateListDao.getDateItem(dateItemId)
        return mapper.mapDbModelToEntity(dbModel)
    }

    override suspend fun checkExistsDateItem(dateName: String): DateItem? {
        val dbModel = dateListDao.checkExistsDateItem(dateName)
        return dbModel?.let { mapper.mapDbModelToEntity(it) }
    }

    override fun getDateList(): LiveData<List<DateItem>> = Transformations.map(
        dateListDao.getDateList()
    ) {
        mapper.mapListDbModelToListEntity(it)
    }


}