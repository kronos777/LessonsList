package com.example.lessonslist.presentation.student

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import com.example.buylistapplication.domain.StudentItem
import com.example.lessonslist.R
import com.example.lessonslist.databinding.ItemStudentDisabledBinding
import com.example.lessonslist.databinding.ItemStudentEnabledBinding


class StudentListAdapter : ListAdapter<StudentItem, StudentItemViewHolder>(StudentItemDiffCallback()) {

    var onStudentItemLongClickListener: ((StudentItem) -> Unit)? = null
    var onStudentItemClickListener: ((StudentItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentItemViewHolder {
        val layout = when (viewType) {
            VIEW_TYPE_DISABLED -> R.layout.item_shop_disabled
            VIEW_TYPE_ENABLED -> R.layout.item_shop_enabled
            else -> throw RuntimeException("Unknown view type: $viewType")
        }
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return StudentItemViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: StudentItemViewHolder, position: Int) {
        val studentItem = getItem(position)
        val binding = viewHolder.binding
        binding.root.setOnLongClickListener {
            onStudentItemLongClickListener?.invoke(studentItem)
            true
        }
        binding.root.setOnClickListener {
            onStudentItemClickListener?.invoke(studentItem)
        }
        /**/
       when (binding) {
            is ItemStudentDisabledBinding -> {
                binding.studentItem = studentItem
            }
            is ItemStudentEnabledBinding -> {
                binding.studentItem = studentItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.enabled) {
            VIEW_TYPE_ENABLED
        } else {
            VIEW_TYPE_DISABLED
        }
    }

    companion object {

        const val VIEW_TYPE_ENABLED = 100
        const val VIEW_TYPE_DISABLED = 101

        const val MAX_POOL_SIZE = 30
    }
}