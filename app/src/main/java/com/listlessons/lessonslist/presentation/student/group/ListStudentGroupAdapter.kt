package com.listlessons.lessonslist.presentation.student.group

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.listlessons.lessonslist.R

class ListStudentGroupAdapter(private val dataSet: ArrayList<*>, mContext: Context) :
    ArrayAdapter<Any?>(mContext, R.layout.row_student_group_item, dataSet) {

    var onGroupItemClick: ((DataStudentGroupModel) -> Unit)? = null

    private class ViewHolder {
        lateinit var txtName: TextView

    }

    var arrayList: ArrayList<Int> = ArrayList()


    override fun getCount(): Int {
        return dataSet.size
    }
    override fun getItem(position: Int): DataStudentGroupModel {
        return dataSet[position] as DataStudentGroupModel
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
                LayoutInflater.from(parent.context).inflate(R.layout.row_student_group_item, parent, false)
            viewHolder.txtName =
                localConvertView.findViewById(R.id.txtName)

            result = localConvertView
            localConvertView.tag = viewHolder
        } else {
            viewHolder = localConvertView.tag as ViewHolder
            result = localConvertView
        }
        val item: DataStudentGroupModel = getItem(position)
        viewHolder.txtName.text = item.name
        val groupItem = getItem(position)
        //onGroupItemClick.s
        viewHolder.txtName.setOnClickListener {
            onGroupItemClick?.invoke(groupItem)
        }


        return result
    }



}
