package com.example.lessonslist.presentation.student


import androidx.recyclerview.widget.DiffUtil
import com.example.buylistapplication.domain.StudentItem

class StudentItemDiffCallback: DiffUtil.ItemCallback<StudentItem>() {

    override fun areItemsTheSame(oldItem: StudentItem, newItem: StudentItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StudentItem, newItem: StudentItem): Boolean {
        return oldItem == newItem
    }
}
