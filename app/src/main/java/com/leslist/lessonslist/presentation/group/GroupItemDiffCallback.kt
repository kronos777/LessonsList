package com.leslist.lessonslist.presentation.group

import androidx.recyclerview.widget.DiffUtil
import com.leslist.lessonslist.domain.group.GroupItem

class GroupItemDiffCallback: DiffUtil.ItemCallback<GroupItem>() {
    /*
    *
class StudentItemDiffCallback: DiffUtil.ItemCallback<StudentItem>() {

    override fun areItemsTheSame(oldItem: StudentItem, newItem: StudentItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StudentItem, newItem: StudentItem): Boolean {
        return oldItem == newItem
    }
}
*/
    override fun areItemsTheSame(oldItem: GroupItem, newItem: GroupItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GroupItem, newItem: GroupItem): Boolean {
        return oldItem == newItem
    }
}