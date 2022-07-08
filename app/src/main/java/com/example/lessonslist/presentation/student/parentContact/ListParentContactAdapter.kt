package com.example.lessonslist.presentation.student.parentContact

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.lessonslist.R

class ListParentContactAdapter(private val dataSet: ArrayList<*>, mContext: Context) :
    ArrayAdapter<Any?>(mContext, R.layout.row_payment_student_item, dataSet) {
    private class ViewHolder {
        lateinit var txtName: TextView
        lateinit var txtPhone: TextView
    }

    var arrayList: ArrayList<Int> = ArrayList()


    override fun getCount(): Int {
        return dataSet.size
    }
    override fun getItem(position: Int): DataParentContactStudentModel {
        return dataSet[position] as DataParentContactStudentModel
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
                LayoutInflater.from(parent.context).inflate(R.layout.row_contact_parent_item, parent, false)
            viewHolder.txtName =
                convertView.findViewById(R.id.txtName)
            viewHolder.txtPhone =
                convertView.findViewById(R.id.txtPhone)
            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }
        val item: DataParentContactStudentModel = getItem(position)
        viewHolder.txtName.text = item.text
        viewHolder.txtPhone.text = item.phone.toString()

         return result
    }



}
