package com.example.lessonslist.presentation

import androidx.databinding.BindingAdapter
import com.example.lessonslist.R
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("errorInputName")
fun bindErrorInputName(textInputLayout: TextInputLayout, isError: Boolean) {
    val message = if (isError) {
        textInputLayout.context.getString(R.string.error_input_name)
    } else {
        null
    }
    textInputLayout.error = message
}

@BindingAdapter("errorInputPaymentBalance")
fun bindErrorInputPaymentBalance(textInputLayout: TextInputLayout, isError: Boolean) {
    val message = if (isError) {
        textInputLayout.context.getString(R.string.error_input_payment_balance)
    } else {
        null
    }
    textInputLayout.error = message
}
