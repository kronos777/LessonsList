package com.leslist.lessonslist.presentation.student

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.leslist.lessonslist.R

class ListPaymentAdapter(private val dataSet: ArrayList<*>, mContext: Context) :
    ArrayAdapter<Any?>(mContext, R.layout.row_payment_student_item, dataSet) {
    private class ViewHolder {
        lateinit var txtName: TextView
        lateinit var price: TextView
    }

    var arrayList: ArrayList<Int> = ArrayList()


    override fun getCount(): Int {
        return dataSet.size
    }
    override fun getItem(position: Int): DataPaymentStudentModel {
        return dataSet[position] as DataPaymentStudentModel
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
            viewHolder.price =
                localConvertView.findViewById(R.id.txtPrice)
            result = localConvertView
            localConvertView.tag = viewHolder
        } else {
            viewHolder = localConvertView.tag as ViewHolder
            result = localConvertView
        }
        val item: DataPaymentStudentModel = getItem(position)
        viewHolder.txtName.text = item.name
        viewHolder.price.text = item.price.toString()

         return result
    }


}
