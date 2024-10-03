package com.llist.lessonslist.presentation.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.llist.lessonslist.R
import com.llist.lessonslist.databinding.ItemStudentEnabledBinding
import com.llist.lessonslist.domain.student.StudentItem


class StudentListAdapter(
    private val showMenuDelete: (Boolean) -> Unit
) : ListAdapter<StudentItem, StudentItemViewHolder>(StudentItemDiffCallback()) {

    var onStudentItemClickListener: ((StudentItem) -> Unit)? = null

    private var isEnabled = false
    val pairList = hashMapOf<Int, StudentItem>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentItemViewHolder {
        /*val layout = when (viewType) {
            VIEW_TYPE_DISABLED -> R.layout.item_student_disabled
            VIEW_TYPE_ENABLED -> R.layout.item_student_enabled
            else -> throw RuntimeException("Unknown view type: $viewType")
        }*/
        val layout = R.layout.item_student_enabled
        val binding = DataBindingUtil.inflate<ItemStudentEnabledBinding>(
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
        /*binding.root.setOnLongClickListener {
            onStudentItemLongClickListener?.invoke(studentItem)
            true
        }
        binding.root.setOnClickListener {
            onStudentItemClickListener?.invoke(studentItem)
        }

       when (binding) {
            is ItemStudentDisabledBinding -> {
                binding.studentItem = studentItem
            }
            is ItemStudentEnabledBinding -> {
                binding.studentItem = studentItem
            }
        }
        if (studentItem.group == "500") {
            //Log.d("itemStudentForDelete", studentItem.id.toString())
           // pairList[studentItem.id] = studentItem
        }*/

        binding.root.setOnClickListener {
            if(studentItem.group != "500" && pairList.isEmpty()){
                onStudentItemClickListener?.invoke(studentItem)
            } else if(pairList.containsKey(studentItem.id)) {
                pairList.remove(studentItem.id)
                studentItem.group = "0"
                viewHolder.binding.checkImage.visibility = View.GONE
                if (pairList.isEmpty()) {
                    showMenuDelete(false)
                }
            } else if(studentItem.group != "500" && pairList.isNotEmpty()) {
                selectItem(viewHolder, studentItem)
            }
        }

        binding.root.setOnLongClickListener {
            selectItem(viewHolder, studentItem)
            true
        }

        binding.studentItem = studentItem

    }
    private fun selectItem(viewHolder: StudentItemViewHolder, studentItem: StudentItem) {
        isEnabled = true
        viewHolder.binding.checkImage.visibility = View.VISIBLE
        pairList[studentItem.id] = studentItem
        studentItem.group = "500"
        showMenuDelete(true)
    }


    companion object {
        const val VIEW_TYPE_ENABLED = 100
        const val MAX_POOL_SIZE = 30
    }
}