package com.example.lessonslist.presentation.student


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.student.StudentListRepositoryImpl
import com.example.lessonslist.domain.student.*


import kotlinx.coroutines.launch

class StudentItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudentListRepositoryImpl(application)

    private val getStudentItemUseCase = GetStudentItemUseCase(repository)
    private val checkStudentItemUseCase = CheckStudentItemUseCase(repository)
    private val addStudentItemUseCase = AddStudentItemUseCase(repository)
    private val deleteStudentItemUseCase = DeleteStudentItemUseCase(repository)
    private val editStudentItemUseCase = EditStudentItemUseCase(repository)
    private val editStudentItemPhoneNumberUseCase = EditStudentItemPhoneNumberUseCase(repository)
    private val editStudentItemPaymentBalanceUseCase = EditStudentItemPaymentBalanceUseCase(repository)

    private val _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>
        get() = _errorInputName

    private val _errorInputLastName = MutableLiveData<Boolean>()
    val errorInputLastName: LiveData<Boolean>
        get() = _errorInputLastName


    private val _errorInputPaymentBalance = MutableLiveData<Boolean>()
    val errorInputPaymentBalance: LiveData<Boolean>
        get() = _errorInputPaymentBalance


    private val _errorInputPhone = MutableLiveData<Boolean>()
    val errorInputPhone: LiveData<Boolean>
        get() = _errorInputPhone

    private val _studentItem = MutableLiveData<StudentItem>()
    val studentItem: LiveData<StudentItem>
        get() = _studentItem

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen

    private val _existsStudent = MutableLiveData<StudentItem?>()
    val existsStudent: LiveData<StudentItem?>
        get() = _existsStudent

    fun getStudentItem(studentItemId: Int) {
        viewModelScope.launch {
            val item = getStudentItemUseCase.getStudentItem(studentItemId)
            _studentItem.value = item
        }
    }

    fun setStudentPhone() {

    }

    fun checkExistsStudent(name: String, lastName: String) {
        viewModelScope.launch {
            val student = checkStudentItemUseCase.checkExistsStudentItem(name, lastName)
            if(student != null) {
                _existsStudent.value = student
            } else {
                _existsStudent.value = null
            }
        }
    }

    fun addStudentItem(inputName: String?, inputLastName: String?, inputPaymentBalance: String, inputNotes: String, inputGroup: String, inputImage: String, inputPhone: String) {
        val name = parseName(inputName)
        val lastName = parseName(inputLastName)

        viewModelScope.launch {
                val studentItem = StudentItem(
                    inputPaymentBalance.toInt(),
                    name,
                    lastName,
                    inputGroup,
                    inputImage,
                    inputNotes,
                    inputPhone,
                    true
                )

                addStudentItemUseCase.addStudentItem(studentItem)
                finishWork()
            }
    }

    fun editStudentItem(inputName: String?, inputLastName: String?, inputPaymentBalance: String, inputNotes: String, inputGroup: String, inputImage: String, inputPhone: String) {
        val name = parseName(inputName)
        val lastName = parseName(inputLastName)
        val paymentBalance = parsePaymentBalance(inputPaymentBalance)

        val fieldsValid = validateInput(name, paymentBalance.toString())
        if (fieldsValid) {
            _studentItem.value?.let {
                viewModelScope.launch {
                    val item = it.copy(
                        name = name,
                        lastname = lastName,
                        paymentBalance = paymentBalance,
                        group = inputGroup,
                        notes = inputNotes,
                        image = inputImage,
                        telephone = inputPhone,
                        enabled = true
                    )
                    editStudentItemUseCase.editStudentItem(item)
                    finishWork()
                }
            }
        }
    }

    fun editPaymentBalance(studentId: Int, paymentBalance: Int) {
        viewModelScope.launch {
            editStudentItemPaymentBalanceUseCase.editStudentItemPaymentBalance(studentId, paymentBalance)
            //finishWork()
        }
    }

   fun editPhoneNumber(studentId: Int, phoneNumber: String, callback: CallbackPhone) {
        viewModelScope.launch {
            editStudentItemPhoneNumberUseCase.editStudentItemPhoneNumber(studentId, phoneNumber)
            callback.success()
           //finishWork()
        }
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    private fun parsePaymentBalance(inputCount: String?): Int {
        return try {
            inputCount?.trim()?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    fun validateInput(name: String, paymentBalance: String): Boolean {
        var result = true
        if (name.isBlank()) {
            _errorInputName.value = true
            result = false
        }

      /*  if (lastName.isBlank()) {
            _errorInputLastName.value = true
            result = false
        }*/

        if (paymentBalance.isBlank()) {
            _errorInputPaymentBalance.value = true
            result = false
        }

        /*if (inputPhone.isBlank()) {
            _errorInputPhone.value = true
            result = false
        }*/

        return result
    }

    fun resetErrorInputName() {
        _errorInputName.value = false
    }

    fun resetErrorInputLastName() {
        _errorInputLastName.value = false
    }


    fun resetErrorInputPaymentBalance() {
        _errorInputPaymentBalance.value = false
    }

    fun resetErrorInputPhone() {
        _errorInputPhone.value = false
    }

    private fun finishWork() {
        _shouldCloseScreen.value = Unit
    }

    fun deleteStudentItem(studentItemId: Int) {
        viewModelScope.launch {
            val item = getStudentItemUseCase.getStudentItem(studentItemId)
            _studentItem.value = item
            deleteStudentItemUseCase.deleteStudentItem(item)
            finishWork()
        }
    }



}
