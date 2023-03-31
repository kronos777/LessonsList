package com.example.lessonslist.presentation.group



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.lessonslist.R
import com.example.lessonslist.databinding.ItemGroupEnabledBinding
import com.example.lessonslist.domain.group.GroupItem


class GroupListAdapter : ListAdapter<GroupItem, GroupItemViewHolder>(GroupItemDiffCallback()) {

    var onGroupItemClickListener: ((GroupItem) -> Unit)? = null
    var onGroupItemLongClickListener: ((GroupItem) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupItemViewHolder {
       // Log.d("viewType", viewType.toString())
        val layout = R.layout.item_group_enabled
        val binding = DataBindingUtil.inflate<ItemGroupEnabledBinding>(
        //val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return GroupItemViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: GroupItemViewHolder, position: Int) {
        val groupItem = getItem(position)
        val binding = viewHolder.binding
        binding.root.setOnClickListener {
            onGroupItemClickListener?.invoke(groupItem)
        }
        binding.root.setOnLongClickListener {
            onGroupItemLongClickListener?.invoke(groupItem)
            true
        }
        binding.groupItem = groupItem
    }

    companion object {

        const val VIEW_TYPE_ENABLED = 100
         const val MAX_POOL_SIZE = 30
    }

}
