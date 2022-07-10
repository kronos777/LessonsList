package com.example.lessonslist.data.parent

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query



@Dao
interface ParentListDao {

    @Query("SELECT * FROM parent_contact")
    fun getParentList(): LiveData<List<ParentItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addParentContact(parentItemDbModel: ParentItemDbModel)

    @Query("DELETE FROM parent_contact WHERE id=:parentContactId")
    suspend fun deleteParentItem(parentContactId: Int)

    @Query("SELECT * FROM parent_contact WHERE id=:parentContactId LIMIT 1")
    suspend fun getParentItem(parentContactId: Int): ParentItemDbModel
}