package com.example.lessonslist.presentation.group



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.lessonslist.R
import com.example.lessonslist.databinding.ItemGroupEnabledBinding
import com.example.lessonslist.domain.group.GroupItem


class GroupListAdapter(
    private val showMenuDelete: (Boolean) -> Unit
) : ListAdapter<GroupItem, GroupItemViewHolder>(GroupItemDiffCallback()) {

    var onGroupItemClickListener: ((GroupItem) -> Unit)? = null


    private var isEnabled = false
    val pairList = hashMapOf<Int, GroupItem>()

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
        /*binding.root.setOnClickListener {
            onGroupItemClickListener?.invoke(groupItem)
        }
        */
        if(groupItem.description == "500") {
            pairList[groupItem.id] = groupItem
        }

        binding.root.setOnClickListener {
            if(groupItem.description != "500" && pairList.isEmpty()){
                onGroupItemClickListener?.invoke(groupItem)
            } else if(pairList.containsKey(groupItem.id)) {
                pairList.remove(groupItem.id)
                groupItem.description = "0"
                viewHolder.binding.checkImage.visibility = View.GONE
                if (pairList.isEmpty()) {
                    showMenuDelete(false)
                }
            } else if(groupItem.description != "500" && pairList.isNotEmpty()) {
                selectItem(viewHolder, groupItem)
            }

        }

        binding.root.setOnLongClickListener {
            selectItem(viewHolder, groupItem)
            true
        }

        binding.groupItem = groupItem
    }


    private fun selectItem(viewHolder: GroupItemViewHolder, groupItem: GroupItem) {
        isEnabled = true
        viewHolder.binding.checkImage.visibility = View.VISIBLE
        // itemSelectedList.add(position)
        pairList[groupItem.id] = groupItem
        groupItem.description = "500"
        showMenuDelete(true)
    }


    companion object {

        const val VIEW_TYPE_ENABLED = 100
         const val MAX_POOL_SIZE = 30
    }

}
