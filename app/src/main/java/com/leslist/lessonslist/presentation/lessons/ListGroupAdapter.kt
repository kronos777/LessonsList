package com.leslist.lessonslist.presentation.lessons

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.leslist.lessonslist.R

class ListGroupAdapter(private val dataSet: ArrayList<*>, mContext: Context) :
    ArrayAdapter<Any?>(mContext, R.layout.row_lessons_group_item, dataSet) {
    private class ViewHolder {
        lateinit var txtName: TextView
        lateinit var checkBox: CheckBox
    }

    var arrayList: ArrayList<String> = ArrayList()


    override fun getCount(): Int {
        return dataSet.size
    }
    override fun getItem(position: Int): DataGroupLessonsModel {
        return dataSet[position] as DataGroupLessonsModel
    }
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var localConvertView = convertView
        val viewHolder: ViewHolder
        val result: View
        if (localConvertView == null) {
            viewHolder = ViewHolder()
            localConvertView =
                LayoutInflater.from(parent.context).inflate(R.layout.row_group_student_item, parent, false)
            viewHolder.txtName =
                localConvertView.findViewById(R.id.txtName)
            viewHolder.checkBox =
                localConvertView.findViewById(R.id.checkBox)
            result = localConvertView
            localConvertView.tag = viewHolder
        } else {
            viewHolder = localConvertView.tag as ViewHolder
            result = localConvertView
        }
        val item: DataGroupLessonsModel = getItem(position)
        viewHolder.txtName.text = item.name
        viewHolder.checkBox.isChecked = item.checked
        val students = item.student


        if (viewHolder.checkBox.isChecked) {
            if (students != null) {
                arrayList.add(students)
            }
        }


            viewHolder.checkBox.setOnClickListener {
                if (viewHolder.checkBox.isChecked) {
                    addCountArrayList(students)
                    item.checked = true
                } else {
                    removeCountArrayList(students)
                    item.checked = false
                }

            }


        return result
    }

    fun removeCountArrayList(id: String?) {
        if (arrayList.size > 0) {
            arrayList.removeIf {
                it == id
            }
        }
    }

   fun addCountArrayList(id: String?) {
       if (id != null) {
           arrayList.add(id)
       }
      Log.d("allListGroup", arrayList.toString())
    }

}
