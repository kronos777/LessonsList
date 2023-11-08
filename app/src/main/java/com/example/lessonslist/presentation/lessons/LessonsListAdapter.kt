package com.example.lessonslist.presentation.lessons



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.lessonslist.R
import com.example.lessonslist.databinding.ItemLessonsEnabledBinding
import com.example.lessonslist.domain.lessons.LessonsItem


class LessonsListAdapter(
    private val showMenuDelete: (Boolean) -> Unit
) : ListAdapter<LessonsItem, LessonsItemViewHolder>(LessonsItemDiffCallback()) {

    var onLessonsItemClickListener: ((LessonsItem) -> Unit)? = null


    private var isEnabled = false
    val pairList = hashMapOf<Int, LessonsItem>()


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
       /* binding.root.setOnClickListener {
            onLessonsItemClickListener?.invoke(lessonsItem)
        }*/

      /*  binding.root.setOnClickListener {
            Log.d("thisCurrentPosition", position.toString())
            if(lessonsItem.student != "500" && itemSelectedList.isEmpty()){
                onLessonsItemClickListener?.invoke(lessonsItem)
            } else if(itemSelectedList.contains(position)) {
                itemSelectedList.removeAt(position)
                lessonsItem.student = "0"
                viewHolder.binding.checkImage.visibility = View.GONE
                if (itemSelectedList.isEmpty()) {
                    showMenuDelete(false)
                }
            } else if(lessonsItem.student != "500" && itemSelectedList.isNotEmpty()) {
                selectItem(viewHolder, lessonsItem, position)
            }

        }*/
        if (lessonsItem.student == "500") {
            pairList[lessonsItem.id] = lessonsItem
        }

        binding.root.setOnClickListener {
            //Log.d("thisCurrentPosition", position.toString())
            //pairList.put(it.id, it.price)
            if(lessonsItem.student != "500" && pairList.isEmpty()){
                onLessonsItemClickListener?.invoke(lessonsItem)
            } else if(pairList.containsKey(lessonsItem.id)) {
                //itemSelectedList.removeAt(position)
                pairList.remove(lessonsItem.id)
                lessonsItem.student = "0"
                viewHolder.binding.checkImage.visibility = View.GONE
                if (pairList.isEmpty()) {
                    showMenuDelete(false)
                }
            } else if(lessonsItem.student != "500" && pairList.isNotEmpty()) {
                selectItem(viewHolder, lessonsItem)
            }

        }
        //binding.root.setOnLongClickListener {
        binding.root.setOnLongClickListener {
            selectItem(viewHolder, lessonsItem)
            true
            /*onLessonsItemLongClickListener?.invoke(lessonsItem)
            true*/
        }
        binding.lessonsItem = lessonsItem
    }


    private fun selectItem(viewHolder: LessonsItemViewHolder, lessonsItem: LessonsItem) {
        isEnabled = true
        viewHolder.binding.checkImage.visibility = View.VISIBLE
       // itemSelectedList.add(position)
        pairList[lessonsItem.id] = lessonsItem
        lessonsItem.student = "500"
        showMenuDelete(true)
    }

    override fun getItemViewType(position: Int): Int {
        //val item = getItem(position)
        return VIEW_TYPE_ENABLED

    }


    companion object {

        const val VIEW_TYPE_ENABLED = 100
         const val MAX_POOL_SIZE = 30
    }

}
