package com.example.lessonslist.presentation.payment


import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentPaymentItemListBinding
import com.example.lessonslist.domain.payment.PaymentItem
import com.example.lessonslist.domain.student.StudentItem
import com.example.lessonslist.presentation.student.StudentItemEditFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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
            //Log.d("studentId", studentId.toString())
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
            //Log.d("studentId", studentId.toString())
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
            Log.d("lessonsId", lessonsId.toString())
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
            Log.d("dateId", dateId)
            val listArrayPayment: ArrayList<PaymentItem> = ArrayList()
            viewModel = ViewModelProvider(this).get(PaymentListViewModel::class.java)
            viewModel.paymentList.observe(viewLifecycleOwner) {
                for (payment in it) {
                    val pay = payment.datePayment.split(" ")
                    val datePay = Date(pay[0])
                    val dateFormated = SimpleDateFormat("d/M/yyyy").format(datePay)
                  ///  val dateString = Date(dateId)
                    //Log.d("dateId", datePay.toString())
                    Log.d("dateId", dateFormated.toString())
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
                    var enabledPay = payment.enabled
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
            /*fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_item_container, PaymentItemFragment.newInstanceEditItem(it.id))
                ?.addToBackStack(null)
                ?.commit()*/
            navigateBtnEditStudent(it.id)
       }
    }


    private fun navigateBtnEditStudent(id: Int) {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsLessons = Bundle().apply {
            putString(PaymentItemFragment.SCREEN_MODE, PaymentItemFragment.MODE_EDIT)
            putInt(PaymentItemFragment.PAYMENT_ITEM_ID, id)
        }

        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()

        navController.navigate(R.id.paymentItemFragment, btnArgsLessons, animationOptions)
    }


    private fun dialogWindow() {

        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("Платежи не удаляются таким образом.")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.HORIZONTAL

        val paymentsLabel = TextView(requireContext())
        paymentsLabel.setSingleLine()
        paymentsLabel.text = """Будьте внимательны платежи удаляются либо вместе с уроком, либо вместе со студентов.""".trimMargin()
        paymentsLabel.height = 250
        paymentsLabel.top = 15
        layout.addView(paymentsLabel)


        layout.setPadding(50, 40, 50, 10)

        alert.setView(layout)

        alert.setPositiveButton("ок", DialogInterface.OnClickListener {
                dialog, id ->
            //deleteLessonsPay
            dialog.dismiss()
        })



        alert.setCancelable(false)
        alert.show()

    }


    private fun setupLongClickListener() {
        paymentListAdapter.onPaymentItemClickListener = {
            viewModel.changeEnableState(it)
        }
    }




    companion object {

        const val SCREEN_MODE = "screen_mode"
        const val CUSTOM_LIST = "custom_list"
        const val STUDENT_ID_LIST = "student_id_list"
        const val STUDENT_NO_PAY_LIST = "student_no_pay_list"
        const val LESSONS_ID_LIST = "lesson_id_list"
        const val DATE_ID_LIST = "date_id_list"
        const val PAYMENT_ENABLED = "payment_enabled"


        const val DATE_ID = "date_id"
        const val STUDENT_ID = "student_id"
        const val LESSONS_ID = "lessons_id"

        fun newInstanceNoneParams(): PaymentItemListFragment {
            return PaymentItemListFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, CUSTOM_LIST)
                }
            }
        }

        fun newInstanceStudentId(studentId: Int): PaymentItemListFragment {
            return PaymentItemListFragment().apply {
                arguments = Bundle().apply {
                    putInt(STUDENT_ID, studentId)
                    putString(SCREEN_MODE, STUDENT_ID_LIST)
                }
            }
        }

        fun newInstanceStudentIdNoPay(studentId: Int): PaymentItemListFragment {
            return PaymentItemListFragment().apply {
                arguments = Bundle().apply {
                    putInt(STUDENT_ID, studentId)
                    putString(SCREEN_MODE, STUDENT_NO_PAY_LIST)
                }
            }
        }

        fun newInstanceLessonsId(lessonsId: Int): PaymentItemListFragment {
            return PaymentItemListFragment().apply {
                arguments = Bundle().apply {
                    putInt(LESSONS_ID, lessonsId)
                    putString(SCREEN_MODE, LESSONS_ID_LIST)
                }
            }
        }

        fun newInstanceDateId(dateId: String): PaymentItemListFragment {
            return PaymentItemListFragment().apply {
                arguments = Bundle().apply {
                    putString(DATE_ID, dateId)
                    putString(SCREEN_MODE, DATE_ID_LIST)
                }
            }
        }

        fun newInstanceEnabledPayment(): PaymentItemListFragment {
            return PaymentItemListFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, PAYMENT_ENABLED)
                }
            }
        }

    }

}
