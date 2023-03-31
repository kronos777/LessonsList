package com.example.lessonslist.presentation.payment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentPaymentItemBinding
import com.example.lessonslist.domain.payment.PaymentItem
import com.example.lessonslist.domain.student.StudentItem
import com.example.lessonslist.presentation.lessons.LessonsItemViewModel
import com.example.lessonslist.presentation.student.StudentItemViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class PaymentItemFragment: Fragment() {


    private lateinit var viewModel: PaymentItemViewModel
    private lateinit var viewModelStudent: StudentItemViewModel
    private lateinit var viewModelLessons: LessonsItemViewModel

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener


    private var _binding: FragmentPaymentItemBinding? = null
    private val binding: FragmentPaymentItemBinding
        get() = _binding ?: throw RuntimeException("PaymentCardBinding == null")

    private var screenMode: String = MODE_UNKNOWN
    private var paymentItemId: Int = PaymentItem.UNDEFINED_ID
    private var studentItemId: Int = StudentItem.UNDEFINED_ID




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


        viewModel = ViewModelProvider(this)[PaymentItemViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        launchRightMode()
        observeViewModel()
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem2).isChecked = true


         viewModel.paymentItem.observe(viewLifecycleOwner) {

            viewModelStudent = ViewModelProvider(this)[StudentItemViewModel::class.java]

                if (it.enabled) {
                    binding.paymentOff.visibility = (View.GONE)
                    binding.valueStatusPayment.text = "Оплачен"
                    binding.paymentPicture.setBackgroundResource(R.drawable.ic_baseline_check_circle_24)
                } else {
                    binding.valueStatusPayment.text = "Долг"
                    binding.paymentPicture.setBackgroundResource(R.drawable.ic_baseline_indeterminate_check_box_24)
                }

            val paymentCount = it.price
            viewModelStudent.getStudentItem(it.studentId)
            viewModelStudent.studentItem.observe(viewLifecycleOwner) {

                if(it.paymentBalance > ( - paymentCount)) {
                    binding.paymentOff.setOnClickListener {
                        deptOff()
                    }

                } else {
                    binding.paymentOff.visibility = (View.GONE)
                    Toast.makeText(getActivity(),"Баланс студента не позволяет списать долг.",Toast.LENGTH_SHORT).show();

                }
            }

        }



    }


    private fun deptOff() {
        viewModelStudent = ViewModelProvider(this)[StudentItemViewModel::class.java]
        viewModelLessons = ViewModelProvider(this)[LessonsItemViewModel::class.java]
        viewModel.paymentItem.observe(viewLifecycleOwner) {
            if(!it.enabled) {
                val payOff = it.price
                val itemPaymentId = it.id
                val idLessons = it.lessonsId
                viewModelStudent.getStudentItem(it.studentId)
                viewModelStudent.studentItem.observe(viewLifecycleOwner) {
                    if(it.paymentBalance >= ( - payOff)) {
                        //производит замену прайса с учетом списания долга в записи студента
                        viewModelStudent.editPaymentBalance(it.id, (it.paymentBalance + payOff))

                        //выстаявляет значение платежа в соответствии со стоимостью урока
                        viewModelLessons.getLessonsItem(idLessons)
                        viewModelLessons.lessonsItem.observe(viewLifecycleOwner) {
                            viewModel.changeEnableState(it.price, itemPaymentId)
                            binding.valuePricePayment.text = it.price.toString() //заменит значение платежа
                        }

                        binding.paymentOff.visibility = (View.GONE)
                        binding.valueStatusPayment.text = "Оплачен"
                        binding.paymentPicture.setBackgroundResource(R.drawable.ic_baseline_check_circle_24)

                        Toast.makeText(getActivity(),"Баланс: " + it.paymentBalance + " Долг:  " + payOff,Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(),"Баланс студента не позволяет списать долг.",Toast.LENGTH_SHORT).show();
                    }
                }

            }

        }
    }



    private fun launchRightMode() {
        launchEditMode()
    }

    private fun goListNavigation() {
        val argss = requireArguments()
        val mode = argss.getString(DATE_ID_BACKSTACK)
        val dateMode = mode?.split("/")
        val studentMode = mode?.split("&")

        if (dateMode!!.size == 3) {
            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
            val navController = navHostFragment.navController
            val args = Bundle().apply {
                putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.DATE_ID_LIST)
                putString(PaymentItemListFragment.DATE_ID, mode)
            }
            navController.navigate(R.id.paymentItemListFragment, args)
        } else if (studentMode!![0] == "student_id_list" && dateMode!!.size == 1) {
            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
            val navController = navHostFragment.navController
            val args = Bundle().apply {
                putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.STUDENT_ID_LIST)
                putInt(PaymentItemListFragment.STUDENT_ID, studentMode!![1].toInt())
            }
            navController.navigate(R.id.paymentItemListFragment, args)
        } else if (studentMode!![0] == "student_no_pay_list" && dateMode!!.size == 1) {
            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
            val navController = navHostFragment.navController
            val args = Bundle().apply {
                putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.STUDENT_NO_PAY_LIST)
                putInt(PaymentItemListFragment.STUDENT_ID, studentMode!![1].toInt())
            }
            navController.navigate(R.id.paymentItemListFragment, args)
        } else if (studentMode!![0] == "payment_enabled" && dateMode!!.size == 1) {
            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
            val navController = navHostFragment.navController
            val btnArgsLessons = Bundle().apply {
                putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.PAYMENT_ENABLED)
            }
            navController.navigate(R.id.paymentItemListFragment, btnArgsLessons)
        } else {
            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
            val navController = navHostFragment.navController
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
        const val MODE_EDIT = "mode_edit"
        const val MODE_ADD = "mode_add"
        const val MODE_UNKNOWN = ""
        const val DATE_ID_BACKSTACK = ""
        const val STUDENT_ID_LIST = ""
        const val STUDENT_NO_PAY_LIST = ""

    }

}