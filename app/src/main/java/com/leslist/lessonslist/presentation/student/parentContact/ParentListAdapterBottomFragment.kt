package com.leslist.lessonslist.presentation.student.parentContact



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.leslist.lessonslist.R
import com.leslist.lessonslist.databinding.ParentItemBinding
import com.leslist.lessonslist.domain.parent.ParentContact


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
