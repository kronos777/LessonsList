package com.llist.lessonslist.data.date

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query



@Dao
interface DateListDao {

    @Query("SELECT * FROM date_items")
    fun getDateList(): LiveData<List<DateItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDateItem(dateItemDbModel: DateItemDbModel)

    @Query("DELETE FROM date_items WHERE id=:dateItemId")
    suspend fun deleteDateItem(dateItemId: Int)

    @Query("SELECT * FROM date_items WHERE id=:dateItemId LIMIT 1")
    suspend fun getDateItem(dateItemId: Int): DateItemDbModel

    @Query("SELECT * FROM date_items WHERE date=:date LIMIT 1")
    suspend fun checkExistsDateItem(date: String): DateItemDbModel?
}