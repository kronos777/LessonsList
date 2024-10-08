package com.leslist.lessonslist.presentation.payment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.leslist.lessonslist.R
import com.leslist.lessonslist.databinding.FragmentPaymentItemBinding
import com.leslist.lessonslist.domain.payment.PaymentItem
import com.leslist.lessonslist.presentation.lessons.LessonsItemViewModel
import com.leslist.lessonslist.presentation.student.StudentItemViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class PaymentItemFragment: Fragment() {


    private val viewModel by lazy {
        ViewModelProvider(this)[PaymentItemViewModel::class.java]
    }
    private val viewModelStudent by lazy {
        ViewModelProvider(this)[StudentItemViewModel::class.java]
    }
    private val viewModelLessons by lazy {
      ViewModelProvider(this)[LessonsItemViewModel::class.java]
    }

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener


    private var _binding: FragmentPaymentItemBinding? = null
    private val binding: FragmentPaymentItemBinding
        get() = _binding ?: throw RuntimeException("PaymentCardBinding == null")

    
    private var paymentItemId: Int = PaymentItem.UNDEFINED_ID

    private val navController by lazy {
        (activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment).navController
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = requireArguments()
        paymentItemId = args.getInt(PAYMENT_ITEM_ID, PaymentItem.UNDEFINED_ID)
      //  parseParams()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEditingFinishedListener) {
            onEditingFinishedListener = context
        } else {
            throw RuntimeException("Activity must implement OnEditingFinishedListener")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Платежи"

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        launchRightMode()
        observeViewModel()
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem2).isChecked = true




    }

    override fun onStart() {
        super.onStart()
        viewModel.paymentItem.observe(viewLifecycleOwner) { paymentItem ->
            val statusPayment= if(paymentItem.enabled) { "Оплачен" } else { "Долг" }
            setTitleInfo(statusPayment)
            if (paymentItem.enabled) {
                binding.paymentOff.visibility = (View.GONE)
                binding.valueStatusPayment.text = statusPayment
                binding.paymentPicture.setBackgroundResource(R.drawable.ic_baseline_check_circle_24)
            } else {
                binding.valueStatusPayment.text = statusPayment
                binding.paymentPicture.setBackgroundResource(R.drawable.ic_baseline_indeterminate_check_box_24)
            }

            val paymentCount = paymentItem.price
            viewModelStudent.getStudentItem(paymentItem.studentId)
            viewModelStudent.studentItem.observe(viewLifecycleOwner) {studentItem->

                if(studentItem.paymentBalance >= ( - paymentCount)) {
                    binding.paymentOff.setOnClickListener {
                        deptOff()
                    }

                } else {
                    binding.paymentOff.visibility = (View.GONE)
                    Toast.makeText(activity,"Баланс студента не позволяет списать долг.",Toast.LENGTH_SHORT).show()

                }
            }

        }
    }

    private fun setTitleInfo(infoTitle: String) {
        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = infoTitle
    }

    private fun deptOff() {
        viewModel.paymentItem.observe(viewLifecycleOwner) { paymentItem ->
            if(!paymentItem.enabled) {
                val payOff = paymentItem.price
                val itemPaymentId = paymentItem.id
                val idLessons = paymentItem.lessonsId
                viewModelStudent.getStudentItem(paymentItem.studentId)
                viewModelStudent.studentItem.observe(viewLifecycleOwner) { studentItem ->

                    if(studentItem.paymentBalance >= ( - payOff)) {
                        //производит замену прайса с учетом списания долга в записи студента
                        viewModelStudent.editPaymentBalance(studentItem.id, (studentItem.paymentBalance + payOff))

                        //выстаявляет значение платежа в соответствии со стоимостью урока
                        viewModelLessons.getLessonsItem(idLessons)
                        viewModelLessons.lessonsItem.observe(viewLifecycleOwner) {
                            viewModel.changeEnableState(it.price, itemPaymentId)
                            binding.valuePricePayment.text = it.price.toString() //заменит значение платежа
                        }

                        binding.paymentOff.visibility = (View.GONE)
                        binding.valueStatusPayment.text = "Оплачен"
                        binding.paymentPicture.setBackgroundResource(R.drawable.ic_baseline_check_circle_24)

                        Toast.makeText(activity,"Баланс: " + studentItem.paymentBalance + " Долг:  " + payOff,Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(activity,"Баланс студента не позволяет списать долг.",Toast.LENGTH_SHORT).show()
                    }

                }

            }

        }
    }



    private fun launchRightMode() {
        launchEditMode()
    }

    private fun goListNavigation() {
        val args = requireArguments()
        val mode = args.getString(DATE_ID_BACKSTACK)
        val dateMode = mode?.split("/")
        val studentMode = mode?.split("&")

        if (dateMode!!.size == 3) {
            val args = Bundle().apply {
                putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.DATE_ID_LIST)
                putString(PaymentItemListFragment.DATE_ID, mode)
            }
            navController.navigate(R.id.paymentItemListFragment, args)
        } else if (studentMode!![0] == "student_id_list" && dateMode.size == 1) {
            val args = Bundle().apply {
                putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.STUDENT_ID_LIST)
                putInt(PaymentItemListFragment.STUDENT_ID, studentMode[1].toInt())
            }
            navController.navigate(R.id.paymentItemListFragment, args)
        } else if (studentMode[0] == "student_no_pay_list" && dateMode.size == 1) {
            val args = Bundle().apply {
                putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.STUDENT_NO_PAY_LIST)
                putInt(PaymentItemListFragment.STUDENT_ID, studentMode[1].toInt())
            }
            navController.navigate(R.id.paymentItemListFragment, args)
        } else if (studentMode[0] == "payment_enabled" && dateMode.size == 1) {
            val btnArgsLessons = Bundle().apply {
                putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.PAYMENT_ENABLED)
            }
            navController.navigate(R.id.paymentItemListFragment, btnArgsLessons)
        } else {
            val args = Bundle().apply {
                putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.CUSTOM_LIST)
            }
            navController.navigate(R.id.paymentItemListFragment, args)
        }


    }

    private fun launchEditMode() {
        viewModel.getPaymentItem(paymentItemId)
        binding.saveButton.setOnClickListener{
            goListNavigation()
        }
    }

    private fun observeViewModel() {
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onEditingFinished()
        }
    }

    interface OnEditingFinishedListener {
        fun onEditingFinished()
    }

    companion object {

        const val SCREEN_MODE = "extra_mode"
        const val PAYMENT_ITEM_ID = "extra_payment_item_id"
        const val DATE_ID_BACKSTACK = ""
        const val STUDENT_ID_LIST = ""
        const val STUDENT_NO_PAY_LIST = ""

    }

}