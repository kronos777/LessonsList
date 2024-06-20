package com.example.lessonslist.presentation.student.notes

import androidx.recyclerview.widget.DiffUtil
import com.example.lessonslist.domain.notes.NotesItem

class NotesItemDiffCallback: DiffUtil.ItemCallback<NotesItem>() {

    override fun areItemsTheSame(oldItem: NotesItem, newItem: NotesItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NotesItem, newItem: NotesItem): Boolean {
        return oldItem == newItem
    }
}