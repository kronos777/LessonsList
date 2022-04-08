package com.example.lessonslist.presentation.lessons



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.lessonslist.R
import com.example.lessonslist.databinding.ItemGroupEnabledBinding
import com.example.lessonslist.databinding.ItemLessonsEnabledBinding
import com.example.lessonslist.domain.group.GroupItem
import com.example.lessonslist.domain.lessons.LessonsItem


class LessonsListAdapter : ListAdapter<LessonsItem, LessonsItemViewHolder>(LessonsItemDiffCallback()) {

    var onLessonsItemClickListener: ((LessonsItem) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonsItemViewHolder {
       // Log.d("viewType", viewType.toString())
        val layout = R.layout.item_lessons_enabled
        val binding = DataBindingUtil.inflate<ItemLessonsEnabledBinding>(
        //val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return LessonsItemViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: LessonsItemViewHolder, position: Int) {
        val lessonsItem = getItem(position)
        val binding = viewHolder.binding
        binding.root.setOnClickListener {
            onLessonsItemClickListener?.invoke(lessonsItem)
        }
        binding.lessonsItem = lessonsItem
    }

    companion object {

        const val VIEW_TYPE_ENABLED = 100
         const val MAX_POOL_SIZE = 30
    }

}
