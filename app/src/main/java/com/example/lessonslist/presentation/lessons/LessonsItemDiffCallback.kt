package com.example.lessonslist.presentation.lessons

import androidx.recyclerview.widget.DiffUtil
import com.example.lessonslist.domain.lessons.LessonsItem

class LessonsItemDiffCallback: DiffUtil.ItemCallback<LessonsItem>() {

    override fun areItemsTheSame(oldItem: LessonsItem, newItem: LessonsItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LessonsItem, newItem: LessonsItem): Boolean {
        return oldItem == newItem
    }
}