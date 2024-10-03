package com.llist.lessonslist.presentation.lessons

import androidx.recyclerview.widget.DiffUtil
import com.llist.lessonslist.domain.lessons.LessonsItem

class LessonsItemListDiffCallback(
    private val oldList: List<LessonsItem>,
    private val newList: List<LessonsItem>,
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem == newItem
    }
}
