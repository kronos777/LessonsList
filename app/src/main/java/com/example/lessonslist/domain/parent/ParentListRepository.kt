package com.example.lessonslist.domain.parent

import androidx.lifecycle.LiveData


interface ParentListRepository {
    suspend fun addParentContact(parentContact: ParentContact)

    suspend fun deleteParentContact(parentContact: ParentContact)

    suspend fun editParentContact(parentContact: ParentContact)

    suspend fun getParentContact(parentContactId: Int): ParentContact

    fun getParentList(): LiveData<List<ParentContact>>
}