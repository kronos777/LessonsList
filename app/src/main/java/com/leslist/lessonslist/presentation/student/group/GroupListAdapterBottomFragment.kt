package com.leslist.lessonslist.presentation.student.group



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.leslist.lessonslist.R
import com.leslist.lessonslist.databinding.ItemGroupEnabledBinding
import com.leslist.lessonslist.domain.group.GroupItem
import com.leslist.lessonslist.presentation.group.GroupItemDiffCallback
import com.leslist.lessonslist.presentation.group.GroupItemViewHolder


class GroupListAdapterBottomFragment() : ListAdapter<GroupItem, GroupItemViewHolder>(
    GroupItemDiffCallback()
) {

    var onGroupItemClickListener: ((GroupItem) -> Unit)? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupItemViewHolder {
        val layout = R.layout.item_group_enabled
        val binding = DataBindingUtil.inflate<ItemGroupEnabledBinding>(
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
            true
        }

        binding.groupItem = groupItem
    }




    companion object {

        const val VIEW_TYPE_ENABLED = 100
         const val MAX_POOL_SIZE = 30
    }

}
