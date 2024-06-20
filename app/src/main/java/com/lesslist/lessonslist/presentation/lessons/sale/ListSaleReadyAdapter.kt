package com.lesslist.lessonslist.presentation.lessons.sale

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.lesslist.lessonslist.R

class ListSaleReadyAdapter(private val dataSet: ArrayList<*>, mContext: Context) :
    ArrayAdapter<Any?>(mContext, R.layout.row_sale_ready_payment_item, dataSet) {
    private class ViewHolder {
        lateinit var txtName: TextView
        lateinit var valuePrice: TextView
    }

    var arrayList: ArrayList<Int> = ArrayList()
    //val idValueMutableMap: MutableMap<Int, Int> = mutableMapOf()

    override fun getCount(): Int {
        return dataSet.size
    }
    override fun getItem(position: Int): DataSalePaymentModel {
        return dataSet[position] as DataSalePaymentModel
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
                LayoutInflater.from(parent.context).inflate(R.layout.row_sale_ready_payment_item, parent, false)
            viewHolder.txtName =
                convertView.findViewById(R.id.nameStudent)

            viewHolder.valuePrice =
                convertView.findViewById(R.id.newPrice)

            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }
        val item: DataSalePaymentModel = getItem(position)
        viewHolder.txtName.text = item.name
        viewHolder.valuePrice.text = item.price.toString()

     return result
    }



}
