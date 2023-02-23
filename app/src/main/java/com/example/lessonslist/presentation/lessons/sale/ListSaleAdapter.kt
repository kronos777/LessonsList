package com.example.lessonslist.presentation.lessons.sale

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.lessonslist.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ListSaleAdapter(private val dataSet: ArrayList<*>, mContext: Context) :
    ArrayAdapter<Any?>(mContext, R.layout.row_sale_payment_item, dataSet) {
    private class ViewHolder {
        lateinit var txtName: TextInputLayout
        lateinit var valuePrice: TextInputEditText
        lateinit var checkBox: CheckBox
    }

    var arrayList: ArrayList<Int> = ArrayList()


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
                LayoutInflater.from(parent.context).inflate(R.layout.row_sale_payment_item, parent, false)
            viewHolder.txtName =
                convertView.findViewById(R.id.til_name)
            viewHolder.checkBox =
                convertView.findViewById(R.id.checkBox)
            viewHolder.valuePrice =
                convertView.findViewById(R.id.et_price)

            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }
        val item: DataSalePaymentModel = getItem(position)
        viewHolder.txtName.hint = item.name
        viewHolder.checkBox.isChecked = item.checked
        //viewHolder.txtName.text = item.price


        if (viewHolder.checkBox.isChecked) {
            arrayList.add(item.id!!)
        }


            viewHolder.valuePrice.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    Log.d("chechBox", item.id.toString())
                    Log.d("chechBox", viewHolder.valuePrice.text.toString())
                }
            })


            viewHolder.checkBox.setOnClickListener {
                if (viewHolder.checkBox.isChecked) {
                    addCountArrayList(item.id!!)
                    Log.d("chechBox", item.id.toString())
                    Log.d("chechBox", viewHolder.checkBox.isChecked.toString())
                    item.checked = true
                    Log.d("chechBox", item.checked.toString())
                    Log.d("chechBox", viewHolder.valuePrice.text.toString())
                    Log.d("chechBox", item.name.toString())
                } else {
                    item.checked = false
                    removeCountArrayList(item.id!!)
                }

            }


        //  Log.d("Modelchecked", item.checked.toString())
        return result
    }

    fun removeCountArrayList(id: Int) {
        if (arrayList.size > 0) {
            arrayList.removeIf {
                it.equals(id)
            }
        }
    }

   fun addCountArrayList(id: Int) {
        //  val countArrayListSize: Int = arrayList.size
/* if (arrayList.size > 0) {
    arrayList.removeIf {
        it.equals(id)

    }
*/
    /* for (ids in arrayList) {
     if(ids == id) {
            Log.d("ExistsInArray", "Exists")
            arrayList.remove(id)

        }
        if(ids == id) {
            Log.d("countExistsInArray", "Exi")
            arrayList.remove(id)
            val idsRemove: Int = id
        } else {

            Log.d("countExistsInArray", "No Exists")
            arrayList.add(id)
        }
       // Log.d("countExistsInArray", ids.toString())
    }*/
    //  arrayList.add(id)
        //} else {
    arrayList.add(id)
    //}
        Log.d("allListSale", arrayList.toString())
    //GroupItemFragment.setStudentData(arrayList.toString())
    }

}
