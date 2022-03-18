package com.example.buylistapplication.domain


data class StudentItem(
    val paymentBalance: Float,
    val name: String,
    val lastname: String,
    val group: ArrayList<Int>,
    val notes: ArrayList<String>,
    var id: Int = UNDEFINED_ID
) {

    companion object {

        const val UNDEFINED_ID = 0
    }
}
