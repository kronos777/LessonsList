package com.leslist.lessonslist.domain.student



class EditStudentItemPaymentBalanceUseCase(private val studentListRepository: StudentListRepository) {

    suspend fun editStudentItemPaymentBalance(studentItemId: Int, paymentBalance: Int) {
        studentListRepository.editStudentItemPaymentBalance(studentItemId, paymentBalance)
    }
}
