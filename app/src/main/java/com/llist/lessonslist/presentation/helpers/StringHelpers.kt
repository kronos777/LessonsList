package com.llist.lessonslist.presentation.helpers

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

object StringHelpers {

    @SuppressLint("SimpleDateFormat")
    fun calendarCreate(yourDateString: String): Date {
        val dtValue = yourDateString.split(" ")
        val pattern = "yyyy/M/dd"
        val formatter = SimpleDateFormat(pattern)
        return formatter.parse(dtValue[0]) as Date
    }
    fun getStudentIds(dataString: String): List<Int> {
        var dataStr = dataString.replace("]", "")
        dataStr = dataStr.replace("[", "")
        return dataStr.split(",").map { it.trim().toInt() }
    }

    fun timeForLessons(number: Int): String {
        return if (number < 10) {
            "0$number"
        } else if (number >= 10) {
            number.toString()
        } else {
            "00"
        }
    }

    fun calculateTheDiscountValue(stringValue: String, lessonsPrice: String): Int? {
        if(stringValue.contains("%")) {
            val price = stringValue.split("%")
            return if (price[0].toInt() < 100) {
                if(price[0] != "" && price.size > 1 && price.size < 3 && price[1] == "") {
                    ((lessonsPrice).toFloat() / (100).toFloat() * price[0].toFloat()).toInt()
                } else if(price.size >= 3) {
                    lessonsPrice.toInt()
                } else {
                    lessonsPrice.toInt()
                }
            } else {
                //Toast.makeText(activity, "Сумма скидки в % не может быть равной 100 и более %.", Toast.LENGTH_SHORT).show()
                null
            }
        } else {
            return stringValue.toFloat().toInt()
        }
    }


}