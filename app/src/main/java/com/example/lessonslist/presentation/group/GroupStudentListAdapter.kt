package com.example.lessonslist.presentation.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.lessonslist.domain.student.StudentItem
import androidx.recyclerview.widget.ListAdapter
import com.example.lessonslist.R
import com.example.lessonslist.databinding.RowGroupStudentItemBinding

class GroupStudentListAdapter : ListAdapter<StudentItem, GroupStudentItemListViewHolder>(GroupStudentItemListDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupStudentItemListViewHolder {
        val layout = R.layout.row_group_student_item
        val binding = DataBindingUtil.inflate<RowGroupStudentItemBinding>(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return GroupStudentItemListViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: GroupStudentItemListViewHolder, position: Int) {
        val studentItem = getItem(position)
        val binding = viewHolder.binding
      /*  binding.root.setOnClickListener {
            onGroupItemClickListener?.invoke(groupItem)
        }*/
        binding.studentsCheck = studentItem
    }
}

/*
    override fun onBindViewHolder(viewHolder: GroupStudentItemListViewHolder, position: Int) {
        val studentItem = getItem(position)
        val binding = viewHolder.binding
        binding.root.setOnClickListener {
            onGroupItemClickListener?.invoke(groupItem)
        }
        binding.groupItem = groupItem
    }*/