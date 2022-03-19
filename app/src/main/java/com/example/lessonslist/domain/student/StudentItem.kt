package com.example.lessonslist.domain.student


data class StudentItem(
    val paymentBalance: Float,
    val name: String,
    val lastname: String,
    val group: ArrayList<Int>,
    val notes: ArrayList<String>,
    val enabled: Boolean,
    var id: Int = UNDEFINED_ID
) {

    companion object {

        const val UNDEFINED_ID = 0
    }
}
