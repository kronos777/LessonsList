package com.leslist.lessonslist.presentation.student


import androidx.recyclerview.widget.DiffUtil
import com.leslist.lessonslist.domain.student.StudentItem


class StudentItemDiffCallback: DiffUtil.ItemCallback<StudentItem>() {

    override fun areItemsTheSame(oldItem: StudentItem, newItem: StudentItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StudentItem, newItem: StudentItem): Boolean {
        return oldItem == newItem
    }
}
