package com.example.lessonslist.domain.student



class EditStudentItemPaymentBalanceUseCase(private val studentListRepository: StudentListRepository) {

    suspend fun editStudentItemPaymentBalance(studentItemId: Int, paymentBalance: Float) {
        studentListRepository.editStudentItemPaymentBalance(studentItemId, paymentBalance)
    }
}
