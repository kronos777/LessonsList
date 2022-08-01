package com.example.lessonslist.presentation.lessons

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

class ListPaymentLessonsAdapter(private val dataSet: ArrayList<*>, mContext: Context) :
    ArrayAdapter<Any?>(mContext, R.layout.row_payment_student_lessons_item, dataSet) {
    private class ViewHolder {
        lateinit var txtName: TextView
        lateinit var txtStatus: TextView
        lateinit var price: TextView
    }

    var arrayList: ArrayList<Int> = ArrayList()


    override fun getCount(): Int {
        return dataSet.size
    }
    override fun getItem(position: Int): DataPaymentStudentLessonsModel {
        return dataSet[position] as DataPaymentStudentLessonsModel
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
                LayoutInflater.from(parent.context).inflate(R.layout.row_payment_student_lessons_item, parent, false)
            viewHolder.txtName =
                convertView.findViewById(R.id.txtName)
            viewHolder.txtStatus =
                convertView.findViewById(R.id.txtStatus)
            viewHolder.price =
                convertView.findViewById(R.id.txtPrice)
            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }
        val item: DataPaymentStudentLessonsModel = getItem(position)
        viewHolder.txtName.text = item.name
        viewHolder.txtStatus.text = item.status
        viewHolder.price.text = item.price.toString()

         return result
    }



}
