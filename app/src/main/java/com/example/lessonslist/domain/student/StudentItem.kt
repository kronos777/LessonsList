package com.example.lessonslist.domain.student


data class StudentItem(
    val paymentBalance: Int,
    val name: String,
    val lastname: String,
    var group: String,
    val image: String,
    val notes: String,
    val telephone: String,
    val enabled: Boolean,
    var id: Int = UNDEFINED_ID
) {
    companion object {
        const val UNDEFINED_ID = 0
    }
}
