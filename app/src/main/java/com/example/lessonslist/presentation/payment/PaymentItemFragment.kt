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
import com.example.lessonslist.presentation.lessons.LessonsItemListFragment
import com.example.lessonslist.presentation.lessons.LessonsItemViewModel
import com.example.lessonslist.presentation.student.StudentItemViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class PaymentItemFragment: Fragment() {


    private lateinit var viewModel: PaymentItemViewModel
    private lateinit var viewModelStudent: StudentItemViewModel
    private lateinit var viewModelLessons: LessonsItemViewModel

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

//    private var _bindingItem: RowGroupStudentItemBinding? = null
    // private lateinit var bindingItem: RowGroupStudentItemBinding
    //      get() = _bindingItem ?: throw RuntimeException("RowGroupItemBinding == null")


    private var _binding: FragmentPaymentItemBinding? = null
    private val binding: FragmentPaymentItemBinding
        get() = _binding ?: throw RuntimeException("PaymentCardBinding == null")

    private var screenMode: String = MODE_UNKNOWN
    private var paymentItemId: Int = PaymentItem.UNDEFINED_ID




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
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
                Log.d("enabledpay", "it.paymentBalance " + it.paymentBalance)
                Log.d("enabledpay", "paymentCount " + ( - paymentCount))
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
        //val paymentItem = viewModel.getPaymentItem(paymentItemId)
        viewModelStudent = ViewModelProvider(this)[StudentItemViewModel::class.java]
        viewModelLessons = ViewModelProvider(this)[LessonsItemViewModel::class.java]
        viewModel.paymentItem.observe(viewLifecycleOwner) {
            if(!it.enabled) {
                val payOff = it.price
                val itemPaymentId = it.id
                val idLessons = it.lessonsId
                viewModelStudent.getStudentItem(it.studentId)
                viewModelStudent.studentItem.observe(viewLifecycleOwner) {
                    if(it.paymentBalance > ( - payOff)) {
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
/*
    public void reLoadFragment(Fragment fragment)
    {
        Log.i(LogGeneratorHelper.INFO_TAG, "reloading fragment");
        Fragment currentFragment = fragment;
        if (currentFragment instanceof "FRAGMENT CLASS NAME") {
            FragmentTransaction fragTransaction =   (getActivity()).getFragmentManager().beginTransaction();
            fragTransaction.detach(currentFragment);
            fragTransaction.attach(currentFragment);
            fragTransaction.commit();
            Log.i(LogGeneratorHelper.INFO_TAG, "reloading fragment finish");
        }
        else Log.i(LogGeneratorHelper.INFO_TAG, "fragment reloading failed");
    }*/

    private fun reLoafFragment(fragment: Fragment){
        val currentFragment: Fragment = fragment
        if(currentFragment is PaymentItemFragment) {
            val fragTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragTransaction.detach(currentFragment)
            fragTransaction.attach(currentFragment)
            fragTransaction.commit()
        }
        Toast.makeText(getActivity(),"Фрагмент перезагружен.",Toast.LENGTH_SHORT).show()
    }

    private fun launchRightMode() {
        Log.d("screenMode", screenMode)
        when (screenMode) {
            MODE_EDIT -> launchEditMode()

        }
    }

    private fun goListNavigation() {
        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.paymentItemListFragment)
    }

    private fun launchEditMode() {
        viewModel.getPaymentItem(paymentItemId)
        binding.saveButton.setOnClickListener{
            goListNavigation()
            /*viewModel.editPaymentItem(
                binding.valueTitle.text.toString(),
                "",//binding.etDescription.text.toString(),
                "",//binding.etLessonId.text.toString(),
                "",//binding.etStudentId.text.toString(),
                binding.valueDatepayment.text.toString(),
                binding.valueStudent.text.toString(),
                binding.valuePricePayment.text.toString(),
                true
            )*/
        }
    }


/*
    private fun launchEditMode() {
        viewModel.getPaymentItem(paymentItemId)
        binding.saveButton.setOnClickListener{
            viewModel.editPaymentItem(
                binding.etTitle.text.toString(),
                binding.etDescription.text.toString(),
                binding.etLessonId.text.toString(),
                binding.etStudentId.text.toString(),
                binding.etDateId.text.toString(),
                binding.etStudent.text.toString(),
                binding.etPrice.text.toString(),
                true
            )

        }
    }

    private fun launchAddMode() {
        binding.saveButton.setOnClickListener{
            viewModel.addPaymentItem(
                binding.etTitle.text.toString(),
                binding.etDescription.text.toString(),
                binding.etLessonId.text.toString(),
                binding.etStudentId.text.toString(),
                binding.etDateId.text.toString(),
                binding.etStudent.text.toString(),
                binding.etPrice.text.toString(),
                true
            )
        }
    }
    */

    private fun observeViewModel() {
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onEditingFinished()
        }
    }

    interface OnEditingFinishedListener {

        fun onEditingFinished()
    }
    private fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }
        val mode = args.getString(SCREEN_MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown screen mode $mode")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!args.containsKey(PAYMENT_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            paymentItemId = args.getInt(PAYMENT_ITEM_ID, PaymentItem.UNDEFINED_ID)
        }
    }
    companion object {

        const val SCREEN_MODE = "extra_mode"
        const val PAYMENT_ITEM_ID = "extra_payment_item_id"
        const val MODE_EDIT = "mode_edit"
        const val MODE_ADD = "mode_add"
        const val MODE_UNKNOWN = ""

        fun newInstanceAddItem(): PaymentItemFragment {
            return PaymentItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(paymentItemId: Int): PaymentItemFragment {
            return PaymentItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(PAYMENT_ITEM_ID, paymentItemId)
                }
            }
        }


    }

}