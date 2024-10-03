package com.llist.lessonslist.presentation.payment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.llist.lessonslist.R
import com.llist.lessonslist.databinding.FragmentPaymentItemListBinding
import com.llist.lessonslist.domain.payment.PaymentItem
import com.llist.lessonslist.presentation.helpers.NavigationOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class PaymentItemListFragment: Fragment() {

    private var _binding: FragmentPaymentItemListBinding? = null
    private val binding: FragmentPaymentItemListBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemListBinding == null")

    private val viewModel by lazy {
        ViewModelProvider(this)[PaymentListViewModel::class.java]
    }

    private lateinit var paymentListAdapter: PaymentListAdapter
    private lateinit var args: Bundle
    private var studentId: Int = 0
    private var lessonsId: Int = 0
    private var dateId: String = ""

    private val navController by lazy {
        (activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment).navController
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "Список платежей"
        setupRecyclerView()

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem2).isChecked = true

        args = requireArguments()
        val mode = args.getString(SCREEN_MODE)
        showPayment(mode!!)
        switchViewPayments()
        goCalendarFragmentBackPressed()
    }

    private fun switchViewPayments() {
        binding.buttonSwitchPayment.setOnClickListener {
            navController.navigate(R.id.calendarPaymentItemFragment, null, NavigationOptions().invoke())
        }
    }
    private fun showPayment(mode: String) {

        if(mode == "student_id_list") {
            studentId = args.getInt(STUDENT_ID)
            val listArrayPayment: ArrayList<PaymentItem> = ArrayList()
            viewModel.paymentList.observe(viewLifecycleOwner) {
                for (payment in it) {
                    if(payment.studentId == studentId){
                        listArrayPayment.add(payment)
                    }
                }
                showImageNoneItem(listArrayPayment)
                paymentListAdapter.submitList(listArrayPayment.reversed())
            }
        } else if(mode == "student_no_pay_list") {
            studentId = args.getInt(STUDENT_ID)
            val listArrayPayment: ArrayList<PaymentItem> = ArrayList()
            viewModel.paymentList.observe(viewLifecycleOwner) {
                for (payment in it) {
                    if(payment.studentId == studentId && !payment.enabled){
                        listArrayPayment.add(payment)
                    }
                }
                showImageNoneItem(listArrayPayment)
                paymentListAdapter.submitList(listArrayPayment.reversed())
            }
        } else if (mode == "lesson_id_list") {
            lessonsId = args.getInt(LESSONS_ID)
            val listArrayPayment: ArrayList<PaymentItem> = ArrayList()
            viewModel.paymentList.observe(viewLifecycleOwner) {
                for (payment in it) {
                    if(payment.lessonsId == lessonsId){
                        listArrayPayment.add(payment)
                    }
                }
                showImageNoneItem(listArrayPayment)
                paymentListAdapter.submitList(listArrayPayment.reversed())
            }


        } else if (mode == "date_id_list") {
                    val listArrayPayment: ArrayList<PaymentItem> = ArrayList()
                    dateId = args.getString(DATE_ID)!!

                    if(dateId.contains("&debts")) {
                        val depotsExists = dateId.split("&debts")

                        viewModel.paymentList.observe(viewLifecycleOwner) {
                            for (payment in it) {
                                val enabledPay = payment.enabled
                                val pay = payment.datePayment.split(" ")
                                val dateFormatted = stringConvertInDate(pay[0])
                                val dateFormattedEnter = stringConvertInDateOther(depotsExists[0])
                                if(dateFormatted == dateFormattedEnter && !enabledPay){
                                    listArrayPayment.add(payment)
                                }
                            }
                            showImageNoneItem(listArrayPayment)
                            paymentListAdapter.submitList(listArrayPayment.reversed())
                        }
                    } else {
                        viewModel.paymentList.observe(viewLifecycleOwner) {
                            for (payment in it) {
                               val pay = payment.datePayment.split(" ")
                               val dateFormatted = stringConvertInDate(pay[0])
                               val dateFormattedEnter = stringConvertInDateOther(dateId)
                                if(dateFormatted == dateFormattedEnter){
                                    listArrayPayment.add(payment)
                                }
                            }
                            showImageNoneItem(listArrayPayment)
                            paymentListAdapter.submitList(listArrayPayment.reversed())
                        }
                    }
        } else if (mode == "payment_enabled") {
            val listArrayPayment: ArrayList<PaymentItem> = ArrayList()
            viewModel.paymentList.observe(viewLifecycleOwner) {
                for (payment in it) {
                    val enabledPay = payment.enabled
                    if(!enabledPay) {
                        listArrayPayment.add(payment)
                    }
                }
                showImageNoneItem(listArrayPayment)
                paymentListAdapter.submitList(listArrayPayment.reversed())
            }
        } else if (mode == "payment_yes") {
            val listArrayPayment: ArrayList<PaymentItem> = ArrayList()
            viewModel.paymentList.observe(viewLifecycleOwner) {
                for (payment in it) {
                    val enabledPay = payment.enabled
                    if(enabledPay) {
                        listArrayPayment.add(payment)
                    }
                }
                showImageNoneItem(listArrayPayment)
                paymentListAdapter.submitList(listArrayPayment.reversed())
            }
        } else {
            viewModel.paymentList.observe(viewLifecycleOwner) {
                showImageNoneItem(it)
                paymentListAdapter.submitList(it.reversed())
            }
        }

    }


    private fun showImageNoneItem(listItem: List<PaymentItem>) {
        if (listItem.isEmpty()) {
            binding.noPayment.visibility = View.VISIBLE
        }
    }
    private fun stringConvertInDate(str: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy/M/d")
        return LocalDate.parse(str, formatter)
    }
    private fun stringConvertInDateOther(str: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
        return LocalDate.parse(str, formatter)
    }
    private fun goCalendarFragmentBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navController.popBackStack(R.id.calendarItemFragment, true)
            navController.navigate(R.id.calendarItemFragment, null, NavigationOptions().invoke())
        }
    }


    private fun setupRecyclerView() {
        with(binding.rvPaymentList) {
            paymentListAdapter = PaymentListAdapter()
            adapter = paymentListAdapter
            recycledViewPool.setMaxRecycledViews(
                PaymentListAdapter.VIEW_TYPE_ENABLED,
                PaymentListAdapter.MAX_POOL_SIZE
            )

        }
        setupClickListener()
    }


    private fun setupClickListener() {
        paymentListAdapter.onPaymentItemClickListener = {
            navigateBtnEditStudent(it.id)
       }
    }


    private fun navigateBtnEditStudent(id: Int) {
        val args = requireArguments()
        val mode = args.getString(SCREEN_MODE)
        val dateIdBackstack = args.getString(DATE_ID)
        val studentIdBackstack = args.getInt(STUDENT_ID)

        val btnArgsLessons = Bundle().apply {
            when (mode) {
                "date_id_list" -> {
                    putString(PaymentItemFragment.DATE_ID_BACKSTACK, dateIdBackstack)
                }
                "student_id_list" -> {
                    putString(PaymentItemFragment.STUDENT_ID_LIST,
                        "student_id_list&$studentIdBackstack"
                    )
                }
                "student_no_pay_list" -> {
                    putString(PaymentItemFragment.STUDENT_NO_PAY_LIST, "$mode&$studentIdBackstack")
                }
                "payment_enabled" -> {
                    putString(PaymentItemFragment.STUDENT_NO_PAY_LIST, mode)
                }
                "custom_list" -> {
                    putString(PaymentItemFragment.STUDENT_NO_PAY_LIST, mode)
                }
            }
            putInt(PaymentItemFragment.PAYMENT_ITEM_ID, id)
        }

        navController.navigate(R.id.paymentItemFragment, btnArgsLessons, NavigationOptions().invoke())
    }


    companion object {

        const val SCREEN_MODE = "screen_mode"
        const val CUSTOM_LIST = "custom_list"
        const val STUDENT_ID_LIST = "student_id_list"
        const val STUDENT_NO_PAY_LIST = "student_no_pay_list"
        const val DATE_ID_LIST = "date_id_list"
        const val PAYMENT_ENABLED = "payment_enabled"

        const val DATE_ID = "date_id"
        const val STUDENT_ID = "student_id"
        const val LESSONS_ID = "lessons_id"



    }

}
