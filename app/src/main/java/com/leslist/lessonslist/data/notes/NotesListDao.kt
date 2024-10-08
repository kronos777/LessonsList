package com.leslist.lessonslist.data.notes

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query



@Dao
interface NotesListDao {

    @Query("SELECT * FROM notes_item ORDER BY id DESC")
    fun getNotesList(): LiveData<List<NotesItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNotesItem(notesItemDbModel: NotesItemDbModel)

    @Query("DELETE FROM notes_item WHERE id=:parentContactId")
    suspend fun deleteNotesItem(parentContactId: Int)

    @Query("SELECT * FROM notes_item WHERE id=:notesItemId LIMIT 1")
    suspend fun getNotesItem(notesItemId: Int): NotesItemDbModel
}