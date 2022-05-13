package com.example.lessonslist.presentation.payment



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.lessonslist.R
import com.example.lessonslist.databinding.ItemGroupEnabledBinding
import com.example.lessonslist.databinding.ItemPaymentEnabledBinding
import com.example.lessonslist.domain.group.GroupItem
import com.example.lessonslist.domain.payment.PaymentItem


class PaymentListAdapter : ListAdapter<PaymentItem, PaymentItemViewHolder>(PaymentItemDiffCallback()) {

    var onPaymentItemClickListener: ((PaymentItem) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentItemViewHolder {
        val layout = R.layout.item_payment_enabled
        val binding = DataBindingUtil.inflate<ItemPaymentEnabledBinding>(
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
        binding.paymentItem = paymentItem
    }

    companion object {

        const val VIEW_TYPE_ENABLED = 100
         const val MAX_POOL_SIZE = 30
    }

}
