package com.llist.lessonslist.presentation.lessons

import androidx.recyclerview.widget.DiffUtil
import com.llist.lessonslist.domain.lessons.LessonsItem

class LessonsItemDiffCallback: DiffUtil.ItemCallback<LessonsItem>() {

    override fun areItemsTheSame(oldItem: LessonsItem, newItem: LessonsItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LessonsItem, newItem: LessonsItem): Boolean {
        return oldItem == newItem
    }
}