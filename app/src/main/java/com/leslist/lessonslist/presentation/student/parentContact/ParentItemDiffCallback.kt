package com.leslist.lessonslist.presentation.student.parentContact

import androidx.recyclerview.widget.DiffUtil
import com.leslist.lessonslist.domain.parent.ParentContact

class ParentItemDiffCallback: DiffUtil.ItemCallback<ParentContact>() {

    override fun areItemsTheSame(oldItem: ParentContact, newItem: ParentContact): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ParentContact, newItem: ParentContact): Boolean {
        return oldItem == newItem
    }
}