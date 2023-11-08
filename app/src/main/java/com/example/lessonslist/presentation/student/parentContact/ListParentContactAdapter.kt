package com.example.lessonslist.presentation.student.parentContact

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.lessonslist.R

class ListParentContactAdapter(private val dataSet: ArrayList<*>, mContext: Context) :
    ArrayAdapter<Any?>(mContext, R.layout.row_contact_parent_item, dataSet) {
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
        var localConvertView = convertView
        val viewHolder: ViewHolder
        val result: View
        if (localConvertView == null) {
            viewHolder = ViewHolder()
            localConvertView =
                LayoutInflater.from(parent.context).inflate(R.layout.row_contact_parent_item, parent, false)
            viewHolder.txtName =
                localConvertView.findViewById(R.id.txtName)
            viewHolder.txtPhone =
                localConvertView.findViewById(R.id.txtPhone)
            result = localConvertView
            localConvertView.tag = viewHolder
        } else {
            viewHolder = localConvertView.tag as ViewHolder
            result = localConvertView
        }
        val item: DataParentContactStudentModel = getItem(position)
        viewHolder.txtName.text = item.text
        viewHolder.txtPhone.text = item.phone.toString()

         return result
    }



}
