package com.lesslist.lessonslist.data.parent

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.lesslist.lessonslist.data.AppDatabase
import com.lesslist.lessonslist.domain.parent.ParentContact
import com.lesslist.lessonslist.domain.parent.ParentListRepository

class ParentListRepositoryImpl(
    application: Application,

) : ParentListRepository {

    private val parentListDao = AppDatabase.getInstance(application).ParentListDao()
    private val mapper = ParentListMapper()
    override suspend fun addParentContact(parentContact: ParentContact) {
        parentListDao.addParentContact(mapper.mapEntityToDbModel(parentContact))
    }

    override suspend fun deleteParentContact(parentContact: ParentContact) {
        parentListDao.deleteParentItem(parentContact.id)
    }

    override suspend fun editParentContact(parentContact: ParentContact) {
        parentListDao.addParentContact(mapper.mapEntityToDbModel(parentContact))
    }

    override suspend fun getParentContact(parentContactId: Int): ParentContact {
        val dbModel = parentListDao.getParentItem(parentContactId)
        return mapper.mapDbModelToEntity(dbModel)
    }

    override fun getParentList(): LiveData<List<ParentContact>> = Transformations.map(
        parentListDao.getParentList()
    ) {
        mapper.mapListDbModelToListEntity(it)
    }

}