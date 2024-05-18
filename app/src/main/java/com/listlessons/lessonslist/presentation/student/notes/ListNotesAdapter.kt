package com.listlessons.lessonslist.presentation.student.notes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.listlessons.lessonslist.R

class ListNotesAdapter(private val dataSet: ArrayList<*>, mContext: Context) :
    ArrayAdapter<Any?>(mContext, R.layout.row_payment_student_item, dataSet) {
    private class ViewHolder {
        lateinit var txtName: TextView
        lateinit var txtDate: TextView
    }

    var arrayList: ArrayList<Int> = ArrayList()
    var onNotesItemClickListener: ((DataNotesStudentModel) -> Unit)? = null

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
        var localConvertView = convertView
        val viewHolder: ViewHolder
        val result: View

        if (localConvertView == null) {
            viewHolder = ViewHolder()
            localConvertView =
                LayoutInflater.from(parent.context).inflate(R.layout.row_payment_student_item, parent, false)
            viewHolder.txtName =
                localConvertView.findViewById(R.id.txtName)
            viewHolder.txtDate =
                localConvertView.findViewById(R.id.txtPrice)
            result = localConvertView
            localConvertView.tag = viewHolder

        } else {
            viewHolder = localConvertView.tag as ViewHolder
            result = localConvertView
        }
        val item: DataNotesStudentModel = getItem(position)
        viewHolder.txtName.text = item.text
        viewHolder.txtDate.text = item.date.toString()

        localConvertView?.setOnClickListener {
            onNotesItemClickListener?.invoke(item)
        }

         return result
    }



}


