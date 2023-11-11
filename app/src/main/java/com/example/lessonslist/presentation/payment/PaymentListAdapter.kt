package com.example.lessonslist.presentation.payment



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import com.example.lessonslist.R
import com.example.lessonslist.databinding.ItemPaymentDisabledBinding
import com.example.lessonslist.databinding.ItemPaymentEnabledBinding
import com.example.lessonslist.domain.payment.PaymentItem


class PaymentListAdapter : ListAdapter<PaymentItem, PaymentItemViewHolder>(PaymentItemDiffCallback()) {

    var onPaymentItemClickListener: ((PaymentItem) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentItemViewHolder {
        //val layout = R.layout.item_payment_enabled
        val layout = when(viewType) {
            VIEW_TYPE_DISABLED -> R.layout.item_payment_disabled
            VIEW_TYPE_ENABLED -> R.layout.item_payment_enabled
            else -> throw RuntimeException("Unknown view type: $viewType")
        }
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
          LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return PaymentItemViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: PaymentItemViewHolder, position: Int) {
        val paymentItem = getItem(position)
        val binding = viewHolder.binding
        binding.root.setOnClickListener {
            onPaymentItemClickListener?.invoke(paymentItem)
        }

        /*       when (binding) {
            is ItemStudentDisabledBinding -> {
                binding.studentItem = studentItem
            }
            is ItemStudentEnabledBinding -> {
                binding.studentItem = studentItem
            }
        }*/
        //binding.paymentItem = paymentItem
        when(binding) {
            is ItemPaymentEnabledBinding -> {
                binding.paymentItem = paymentItem
            }
            is ItemPaymentDisabledBinding -> {
                binding.paymentItem = paymentItem
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.enabled) {
           VIEW_TYPE_ENABLED
        } else {
            VIEW_TYPE_DISABLED
        }
    }

    companion object {

        const val VIEW_TYPE_ENABLED = 100
        const val VIEW_TYPE_DISABLED = 101
         const val MAX_POOL_SIZE = 30
    }

}
