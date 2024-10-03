package com.llist.lessonslist.domain.student



class EditStudentItemPhoneNumberUseCase(private val studentListRepository: StudentListRepository) {

    suspend fun editStudentItemPhoneNumber(studentItemId: Int, phoneNumber: String) {
        studentListRepository.editStudentItemPhoneNumber(studentItemId, phoneNumber)
    }
}
