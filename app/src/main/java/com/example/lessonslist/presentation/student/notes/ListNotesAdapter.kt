package com.example.lessonslist.presentation.student.notes

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.example.lessonslist.R

class ListNotesAdapter(private val dataSet: ArrayList<*>, mContext: Context) :
    ArrayAdapter<Any?>(mContext, R.layout.row_payment_student_item, dataSet) {
    private class ViewHolder {
        lateinit var txtName: TextView
        lateinit var txtDate: TextView
    }

    var arrayList: ArrayList<Int> = ArrayList()


    override fun getCount(): Int {
        return dataSet.size
    }
    override fun getItem(position: Int): DataNotesStudentModel {
        return dataSet[position] as DataNotesStudentModel
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
                LayoutInflater.from(parent.context).inflate(R.layout.row_payment_student_item, parent, false)
            viewHolder.txtName =
                convertView.findViewById(R.id.txtName)
            viewHolder.txtDate =
                convertView.findViewById(R.id.txtPrice)
            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }
        val item: DataNotesStudentModel = getItem(position)
        viewHolder.txtName.text = item.text
        viewHolder.txtDate.text = item.date.toString()

         return result
    }



}
