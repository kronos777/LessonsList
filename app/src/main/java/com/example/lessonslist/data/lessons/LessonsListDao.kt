package com.example.lessonslist.data.lessons

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query



@Dao
interface LessonsListDao {

    @Query("SELECT * FROM lessons_items")
    fun getLessonsList(): LiveData<List<LessonsItemDbModel>>

    @Query("SELECT * FROM lessons_items")
    fun getAllLessonsList(): List<LessonsItemDbModel>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLessonsItem(lessonsItemDbModel: LessonsItemDbModel)

    @Query("DELETE FROM lessons_items WHERE id=:lessonsItemId")
    suspend fun deleteLessonsItem(lessonsItemId: Int)

    @Query("SELECT * FROM lessons_items WHERE id=:lessonsItemId LIMIT 1")
    suspend fun getLessonsItem(lessonsItemId: Int): LessonsItemDbModel

}
