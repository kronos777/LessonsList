package com.example.lessonslist.presentation.payment

import androidx.recyclerview.widget.DiffUtil
import com.example.lessonslist.domain.payment.PaymentItem

class PaymentItemDiffCallback: DiffUtil.ItemCallback<PaymentItem>() {

    override fun areItemsTheSame(oldItem: PaymentItem, newItem: PaymentItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PaymentItem, newItem: PaymentItem): Boolean {
        return oldItem == newItem
    }
}