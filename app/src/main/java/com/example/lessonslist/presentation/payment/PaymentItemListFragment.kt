package com.example.lessonslist.presentation.payment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentPaymentItemListBinding
import com.example.lessonslist.domain.payment.PaymentItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*


class PaymentItemListFragment: Fragment() {

    private var _binding: FragmentPaymentItemListBinding? = null
    private val binding: FragmentPaymentItemListBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemListBinding == null")

    private lateinit var viewModel: PaymentListViewModel
    private lateinit var paymentListAdapter: PaymentListAdapter
    private var studentId: Int = 0
    private var lessonsId: Int = 0
    private var dateId: String = ""

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



        val args = requireArguments()
        val mode = args.getString(SCREEN_MODE)
        if(mode == "student_id_list") {
            studentId = args.getInt(STUDENT_ID)
            val listArrayPayment: ArrayList<PaymentItem> = ArrayList()
            viewModel = ViewModelProvider(this).get(PaymentListViewModel::class.java)
            viewModel.paymentList.observe(viewLifecycleOwner) {
                for (payment in it) {
                    if(payment.studentId == studentId){
                        listArrayPayment.add(payment)
                    }
                }
                paymentListAdapter.submitList(listArrayPayment)
            }
        } else if(mode == "student_no_pay_list") {
            studentId = args.getInt(STUDENT_ID)
            val listArrayPayment: ArrayList<PaymentItem> = ArrayList()
            viewModel = ViewModelProvider(this).get(PaymentListViewModel::class.java)
            viewModel.paymentList.observe(viewLifecycleOwner) {
                for (payment in it) {
                    if(payment.studentId == studentId && !payment.enabled){
                        listArrayPayment.add(payment)
                    }
                }
                paymentListAdapter.submitList(listArrayPayment)
            }
        } else if (mode == "lesson_id_list") {
            lessonsId = args.getInt(LESSONS_ID)
            val listArrayPayment: ArrayList<PaymentItem> = ArrayList()
            viewModel = ViewModelProvider(this).get(PaymentListViewModel::class.java)
            viewModel.paymentList.observe(viewLifecycleOwner) {
                for (payment in it) {
                    if(payment.lessonsId == lessonsId){
                        listArrayPayment.add(payment)
                    }
                }
                paymentListAdapter.submitList(listArrayPayment)
            }


        } else if (mode == "date_id_list") {
            dateId = args.getString(DATE_ID)!!
            val listArrayPayment: ArrayList<PaymentItem> = ArrayList()
            viewModel = ViewModelProvider(this).get(PaymentListViewModel::class.java)
            viewModel.paymentList.observe(viewLifecycleOwner) {
                for (payment in it) {
                    val pay = payment.datePayment.split(" ")
                    val datePay = Date(pay[0])
                    val dateFormated = SimpleDateFormat("d/M/yyyy").format(datePay)
                    if(dateFormated == dateId){
                        listArrayPayment.add(payment)
                    }
                }
                paymentListAdapter.submitList(listArrayPayment)
            }
        } else if (mode == "payment_enabled") {
            val listArrayPayment: ArrayList<PaymentItem> = ArrayList()
            viewModel = ViewModelProvider(this).get(PaymentListViewModel::class.java)
            viewModel.paymentList.observe(viewLifecycleOwner) {
                for (payment in it) {
                    val enabledPay = payment.enabled
                    if(!enabledPay) {
                        listArrayPayment.add(payment)
                    }
                }
                paymentListAdapter.submitList(listArrayPayment)
            }
        } else {
            viewModel = ViewModelProvider(this).get(PaymentListViewModel::class.java)
            viewModel.paymentList.observe(viewLifecycleOwner) {
                paymentListAdapter.submitList(it)
            }
        }

        goCalendarFragmentBackPressed()
    }

    private fun goCalendarFragmentBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
            val navController = navHostFragment.navController
            navController.popBackStack(R.id.calendarItemFragment, true)
            val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
                .setExitAnim(R.anim.slide_in_right)
                .setPopEnterAnim(R.anim.slide_out_left)
                .setPopExitAnim(R.anim.slide_out_right).build()
            navController.navigate(R.id.calendarItemFragment, null, animationOptions)
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
       // setupLongClickListener()
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

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsLessons = Bundle().apply {
            if(mode == "date_id_list") {
                putString(PaymentItemFragment.DATE_ID_BACKSTACK, dateIdBackstack)
            } else if(mode == "student_id_list") {
                putString(PaymentItemFragment.STUDENT_ID_LIST, "student_id_list" + "&" + studentIdBackstack.toString())
            } else if(mode == "student_no_pay_list") {
                putString(PaymentItemFragment.STUDENT_NO_PAY_LIST, mode + "&" + studentIdBackstack)
            } else if(mode == "payment_enabled") {
                putString(PaymentItemFragment.STUDENT_NO_PAY_LIST, mode)
            } else if(mode == "custom_list")  {
                putString(PaymentItemFragment.STUDENT_NO_PAY_LIST, mode)
            }
            putInt(PaymentItemFragment.PAYMENT_ITEM_ID, id)
        }

        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()

        navController.navigate(R.id.paymentItemFragment, btnArgsLessons, animationOptions)
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
