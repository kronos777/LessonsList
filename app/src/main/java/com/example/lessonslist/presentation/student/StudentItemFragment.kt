package com.example.lessonslist.presentation.student

import android.R
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lessonslist.databinding.FragmentStudentItemBinding
import com.example.lessonslist.domain.student.StudentItem
import com.example.lessonslist.presentation.group.DataStudentGroupModel
import com.example.lessonslist.presentation.lessons.LessonsItemFragment
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import java.util.ArrayList


class StudentItemFragment : Fragment() {

    private lateinit var viewModel: StudentItemViewModel
    private lateinit var viewModelPayment: PaymentListViewModel

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentStudentItemBinding? = null
    private val binding: FragmentStudentItemBinding
        get() = _binding ?: throw RuntimeException("FragmentShopItemBinding == null")
    private lateinit var listView: ListView
    private var screenMode: String = MODE_UNKNOWN
    private var studentItemId: Int = StudentItem.UNDEFINED_ID
    private var dataPaymentStudentModel: ArrayList<DataPaymentStudentModel>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEditingFinishedListener) {
            onEditingFinishedListener = context
        } else {
            throw RuntimeException("Activity must implement OnEditingFinishedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[StudentItemViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        addTextChangeListeners()
        launchRightMode()
        observeViewModel()

        //viewModel.studentItem.
        dataPaymentStudentModel = ArrayList<DataPaymentStudentModel>()
        listView = binding.listView
        val args = requireArguments()
        val mode = args.getString(SCREEN_MODE)
        if (mode == MODE_EDIT || mode == "mode_edit") {
            Log.d("nowmodeadd", "mode add")
            viewModelPayment = ViewModelProvider(this)[PaymentListViewModel::class.java]
            viewModelPayment.paymentList.observe(viewLifecycleOwner) {
                if(it.size > 0) {
                    for (payment in it) {
                        if(payment.studentId == studentItemId) {
                            //dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,true))
                            dataPaymentStudentModel!!.add(DataPaymentStudentModel(payment.title, payment.price))
                            Log.d("nowmodeadd", "aaa" + payment.title + " " + payment.price)
                        }
                    }
                }
/*                adapter = ListStudentAdapter(dataStudentGroupModel!!, requireContext().applicationContext)

                listView.adapter = adapter*/
                val adapter =  ListPaymentAdapter(dataPaymentStudentModel!!, requireContext().applicationContext)
                listView.adapter = adapter

            }
        }

    }

    private fun observeViewModel() {
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onEditingFinished()
        }
    }

    private fun launchRightMode() {
        when (screenMode) {
            MODE_EDIT -> launchEditMode()
            MODE_ADD  -> launchAddMode()
        }
    }

    private fun addTextChangeListeners() {
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputName()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.etLastname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputLastName()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.etPaymentBalance.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputPaymentBalance()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    private fun launchEditMode() {
        viewModel.getStudentItem(studentItemId)
        binding.saveButton.setOnClickListener {
            viewModel.editStudentItem(
                binding.etName.text?.toString(),
                binding.etLastname.text?.toString(),
                binding.etPaymentBalance.text.toString(),
                binding.etNotes.text.toString(),
                binding.etGroup.text.toString()
            )
        }
    }

    private fun launchAddMode() {
        binding.saveButton.setOnClickListener {
            viewModel.addStudentItem(
                binding.etName.text?.toString(),
                binding.etLastname.text?.toString(),
                binding.etPaymentBalance.text.toString(),
                binding.etNotes.text.toString(),
                binding.etGroup.text.toString()
            )
        }
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
            if (!args.containsKey(SHOP_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            studentItemId = args.getInt(SHOP_ITEM_ID, StudentItem.UNDEFINED_ID)
        }
    }

    interface OnEditingFinishedListener {

        fun onEditingFinished()

    }

    companion object {

        private const val SCREEN_MODE = "extra_mode"
        private const val SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""

        fun newInstanceAddItem(): StudentItemFragment {
            return StudentItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(shopItemId: Int): StudentItemFragment {
            return StudentItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(SHOP_ITEM_ID, shopItemId)
                }
            }
        }
    }
}
