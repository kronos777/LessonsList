package com.leslist.lessonslist.presentation.lessons.sale

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import com.leslist.lessonslist.R
import com.leslist.lessonslist.presentation.helpers.StringHelpers
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ListFlexibleSaleAdapter(private val dataSet: ArrayList<*>, mContext: Context) :
    ArrayAdapter<Any?>(mContext, R.layout.row_sale_flexible_item, dataSet) {
    private class ViewHolder {
        lateinit var errorPrice: TextInputLayout
        lateinit var valuePriceOld: TextInputEditText
        lateinit var valuePrice: TextInputEditText
        lateinit var checkBox: CheckBox
    }

    var idValueMutableMap: MutableMap<Int, Int> = mutableMapOf()
    //var arrayList: ArrayList<Int> = ArrayList()
    //val idValueMutableMap: MutableMap<Int, Int> = mutableMapOf()

    override fun getCount(): Int {
        return dataSet.size
    }
    override fun getItem(position: Int): DataSalePaymentModel {
        return dataSet[position] as DataSalePaymentModel
    }
    @SuppressLint("SetTextI18n")
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
                LayoutInflater.from(parent.context).inflate(R.layout.row_sale_flexible_item, parent, false)
            viewHolder.checkBox =
                convertView.findViewById(R.id.checkBox)
            viewHolder.valuePrice =
                convertView.findViewById(R.id.et_newPrice)
            viewHolder.valuePriceOld =
                convertView.findViewById(R.id.et_Price)
            viewHolder.errorPrice =
                convertView.findViewById(R.id.til_newPrice)

            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }
        val item: DataSalePaymentModel = getItem(position)
       // viewHolder.txtName.text = item.name
        viewHolder.checkBox.isChecked = item.checked
        if(item.price!! > 0) {
            viewHolder.valuePrice.hint = item.name + " " + item.price.toString()
            //Log.d("oldPriceAdapter", item.oldPrice.toString())
             viewHolder.valuePriceOld.setText(item.oldPrice.toString())
        } else {
            viewHolder.valuePrice.hint = item.name
            //Log.d("oldPriceAdapter", item.oldPrice.toString())
            viewHolder.valuePriceOld.setText(item.oldPrice.toString())
        }




        if (viewHolder.checkBox.isChecked) {
            idValueMutableMap.put(item.id!!, item.price!!.toInt())
        }


            viewHolder.valuePrice.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    /*Log.d("chechBox", item.id.toString())
                    Log.d("chechBox", viewHolder.valuePrice.text.toString())*/
                    if (viewHolder.checkBox.isChecked) {
                        val calculatePrice = StringHelpers.calculateTheDiscountValue(viewHolder.valuePrice.text.toString(), item.oldPrice.toString())
                        calculatePrice?.let { addCountArrayList(item.id!!, it) }
                    }
                }
            })


            viewHolder.checkBox.setOnClickListener {
                    if (viewHolder.checkBox.isChecked) {
                        if(viewHolder.valuePrice.text.toString().isNullOrBlank()) {
                            viewHolder.errorPrice.error = "Сумма скидки не может быть пустой"
                        } else {
                            val calculatePrice = StringHelpers.calculateTheDiscountValue(viewHolder.valuePrice.text.toString(), item.oldPrice.toString())
                            calculatePrice?.let { it1 -> addCountArrayList(item.id!!, it1) }
                            item.checked = true
                            /*Log.d("chechBox old", item.oldPrice.toString())
                            Log.d("chechBox", item.id.toString())
                            Log.d("chechBox", viewHolder.checkBox.isChecked.toString())
                            Log.d("chechBox", item.checked.toString())
                            Log.d("chechBox", viewHolder.valuePrice.text.toString())
                            Log.d("chechBox", item.name.toString())*/
                        }
                    } else {
                        if(viewHolder.valuePrice.text.toString().isNullOrBlank()) {
                            viewHolder.errorPrice.error = ""
                        }
                        item.checked = false
                        removeCountArrayList(item.id!!)
                    }



            }


        return result
    }

    fun removeCountArrayList(id: Int) {
        if (idValueMutableMap.size > 0) {
            idValueMutableMap.remove(id)
        }
    }

   fun addCountArrayList(id: Int, price: Int) {
       idValueMutableMap.put(id, price)
    }




}
