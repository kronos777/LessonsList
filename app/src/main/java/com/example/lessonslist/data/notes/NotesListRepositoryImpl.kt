package com.example.lessonslist.data.notes

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.lessonslist.data.AppDatabase
import com.example.lessonslist.domain.notes.NotesItem
import com.example.lessonslist.domain.notes.NotesListRepository
import com.example.lessonslist.domain.parent.ParentContact
import com.example.lessonslist.domain.parent.ParentListRepository

class NotesListRepositoryImpl(
    application: Application,

) : NotesListRepository {

    private val notesListDao = AppDatabase.getInstance(application).NotesListDao()
    private val mapper = NotesListMapper()


    override suspend fun addNotesItem(notesItem: NotesItem) {
        notesListDao.addNotesItem(mapper.mapEntityToDbModel(notesItem))
    }

    override suspend fun deleteNotesItem(notesItem: NotesItem) {
        notesListDao.deleteNotesItem(notesItem.id)
    }

    override suspend fun editNotesItem(notesItem: NotesItem) {
        notesListDao.addNotesItem(mapper.mapEntityToDbModel(notesItem))
    }

    override suspend fun getNotesItem(notesItemId: Int): NotesItem {
        val dbModel = notesListDao.getNotesItem(notesItemId)
        return mapper.mapDbModelToEntity(dbModel)
    }

    override fun getNotesList(): LiveData<List<NotesItem>> = Transformations.map(
        notesListDao.getNotesList()
    ) {
        mapper.mapListDbModelToListEntity(it)
    }

}