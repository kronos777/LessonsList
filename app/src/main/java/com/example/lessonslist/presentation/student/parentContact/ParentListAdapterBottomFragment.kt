package com.example.lessonslist.presentation.student.parentContact



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.lessonslist.R
import com.example.lessonslist.databinding.ItemGroupEnabledBinding
import com.example.lessonslist.databinding.ParentItemBinding
import com.example.lessonslist.domain.group.GroupItem
import com.example.lessonslist.domain.parent.ParentContact
import com.example.lessonslist.presentation.group.GroupItemViewHolder


class ParentListAdapterBottomFragment() : ListAdapter<ParentContact, ParentItemViewHolder>(
    ParentItemDiffCallback()
) {

    var onParentItemClickListener: ((ParentContact) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentItemViewHolder {
        val layout = R.layout.parent_item
        val binding = DataBindingUtil.inflate<ParentItemBinding>(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return ParentItemViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ParentItemViewHolder, position: Int) {
        val parentItem = getItem(position)
        val binding = viewHolder.binding


        binding.root.setOnClickListener {
            onParentItemClickListener?.invoke(parentItem)
        }

        binding.root.setOnLongClickListener {
            true
        }

        binding.parentItem = parentItem
    }




    companion object {

        const val VIEW_TYPE_ENABLED = 100
         const val MAX_POOL_SIZE = 30
    }

}
