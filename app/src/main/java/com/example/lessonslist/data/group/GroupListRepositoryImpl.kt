package com.example.lessonslist.data.group

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.lessonslist.data.AppDatabase
import com.example.lessonslist.domain.group.GroupItem
import com.example.lessonslist.domain.group.GroupListRepository

class GroupListRepositoryImpl(
    application: Application,

) : GroupListRepository {

    private val groupListDao = AppDatabase.getInstance(application).GroupListDao()
    private val mapper = GroupListMapper()


    override suspend fun addGroupItem(groupItem: GroupItem) {
        groupListDao.addGroupItem(mapper.mapEntityToDbModel(groupItem))
    }

    override suspend fun deleteGroupItem(groupItem: GroupItem) {
        groupListDao.deleteGroupItem(groupItem.id)
    }

    override suspend fun editGroupItem(groupItem: GroupItem) {
        groupListDao.addGroupItem(mapper.mapEntityToDbModel(groupItem))
    }

    override suspend fun getGroupItem(groupItemId: Int): GroupItem {
        val dbModel = groupListDao.getGroupItem(groupItemId)
        return mapper.mapDbModelToEntity(dbModel)
    }

    override fun getGroupList(): LiveData<List<GroupItem>> = Transformations.map(
        groupListDao.getGroupList()
    ) {
        mapper.mapListDbModelToListEntity(it)
    }

}