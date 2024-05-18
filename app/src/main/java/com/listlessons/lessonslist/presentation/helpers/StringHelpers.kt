package com.listlessons.lessonslist.presentation.helpers

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

}