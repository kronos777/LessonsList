package com.lesslist.lessonslist.presentation.student.notes



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.lesslist.lessonslist.R
import com.lesslist.lessonslist.databinding.NotesItemBinding
import com.lesslist.lessonslist.domain.notes.NotesItem


class NotesListAdapterBottomFragment() : ListAdapter<NotesItem, NotesItemViewHolder>(
    NotesItemDiffCallback()
) {

    var onNotesItemClickListener: ((NotesItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesItemViewHolder {
        val layout = R.layout.notes_item
        val binding = DataBindingUtil.inflate<NotesItemBinding>(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return NotesItemViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: NotesItemViewHolder, position: Int) {
        val notesItem = getItem(position)
        val binding = viewHolder.binding


        binding.root.setOnClickListener {
            onNotesItemClickListener?.invoke(notesItem)
        }

        binding.root.setOnLongClickListener {
            true
        }

        binding.notesItem = notesItem
    }




    companion object {
        const val VIEW_TYPE_ENABLED = 100
        const val MAX_POOL_SIZE = 30
    }

}
