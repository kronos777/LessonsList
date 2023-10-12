package com.example.lessonslist.presentation.lessons



import android.util.Log
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
   // var onLessonsItemLongClickListener: ((LessonsItem) -> Unit)? = null


    private var isEnabled = false
    private var itemSelectedList = mutableListOf<Int>()
    //val pairList = ArrayList<Pair<Int, Int>>()
    val pairList = mutableMapOf<Int, Int>()


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

        binding.root.setOnClickListener {
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

        }

        //binding.root.setOnLongClickListener {
        binding.root.setOnLongClickListener {
            selectItem(viewHolder, lessonsItem, position)
            true
            /*onLessonsItemLongClickListener?.invoke(lessonsItem)
            true*/
        }
        binding.lessonsItem = lessonsItem
    }


    private fun selectItem(viewHolder: LessonsItemViewHolder, lessonsItem: LessonsItem, position: Int) {
        isEnabled = true
        viewHolder.binding.checkImage.visibility = View.VISIBLE
        itemSelectedList.add(position)
        lessonsItem.student = "500"
        showMenuDelete(true)
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return VIEW_TYPE_ENABLED

    }

    fun deleteChoiceItem() {
        if (itemSelectedList.isNotEmpty()) {
            currentList.filter{ it.student == "500" }.forEach {
                /*val pair = Pair(it.id, it.idBussines)
                pairList.add(pair)*/
                pairList.put(it.id, it.price)
            }
            itemSelectedList.clear()
            isEnabled = false
        }
    }

    companion object {

        const val VIEW_TYPE_ENABLED = 100
         const val MAX_POOL_SIZE = 30
    }

}
