package com.example.lessonslist.data.group

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query



@Dao
interface GroupListDao {

    @Query("SELECT * FROM group_items")
    fun getGroupList(): LiveData<List<GroupItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGroupItem(groupItemDbModel: GroupItemDbModel)

    @Query("DELETE FROM group_items WHERE id=:groupItemId")
    suspend fun deleteGroupItem(groupItemId: Int)

    @Query("SELECT * FROM group_items WHERE id=:groupItemId LIMIT 1")
    suspend fun getGroupItem(groupItemId: Int): GroupItemDbModel

    @Query("SELECT * FROM group_items WHERE title=:groupName LIMIT 1")
    suspend fun checkExistsGroupItem(groupName: String): GroupItemDbModel
}