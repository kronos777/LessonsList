package com.example.lessonslist.presentation.lessons

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.lessonslist.R

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
        var convertView = convertView
        val viewHolder: ViewHolder
        val result: View
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView =
                LayoutInflater.from(parent.context).inflate(R.layout.row_group_student_item, parent, false)
            viewHolder.txtName =
                convertView.findViewById(R.id.txtName)
            viewHolder.checkBox =
                convertView.findViewById(R.id.checkBox)
            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }
        val item: DataGroupLessonsModel = getItem(position)
        viewHolder.txtName.text = item.name
        viewHolder.checkBox.isChecked = item.checked
        var students = item.student


        if (viewHolder.checkBox.isChecked) {
            if (students != null) {
                arrayList.add(students)
            }
        }


            viewHolder.checkBox.setOnClickListener {
                if (viewHolder.checkBox.isChecked) {
                    addCountArrayList(students)
                    item.checked = true
                    Log.d("chechBoxGroup", item.id.toString())
                    Log.d("chechBoxGroup", viewHolder.checkBox.isChecked.toString())
                    Log.d("chechBoxGroup", item.name.toString())
                } else {
                    removeCountArrayList(students)
                    item.checked = false
                }

            }


        //  Log.d("Modelchecked", item.checked.toString())
        return result
    }

    fun removeCountArrayList(id: String?) {
        if (arrayList.size > 0) {
            arrayList.removeIf {
                it.equals(id)
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
