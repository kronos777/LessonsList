package com.example.lessonslist.presentation.group

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lessonslist.databinding.FragmentGroupItemBinding


class GroupItemFragment : Fragment() {

    //   private lateinit var viewModel: StudentItemViewModel
    private lateinit var viewModel: GroupItemViewModel
    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentGroupItemBinding? = null
    private val binding: FragmentGroupItemBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemBinding == null")


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
    ): View? {
     //   return inflater.inflate(R.layout.fragment_group_item, container, false)
        _binding = FragmentGroupItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[GroupItemViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        //addTextChangeListeners()
        launchAddMode()
        observeViewModel()
    }

    private fun addTextChangeListeners() {
        TODO("Not yet implemented")
    }

    private fun launchAddMode() {
        binding.saveButton.setOnClickListener{
            viewModel.addGroupItem(
                binding.etTitle.text.toString(),
                binding.etDescription.text.toString(),
                binding.etStudent.text.toString()
            )
        }
    }
/*
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
    }*/

    private fun observeViewModel() {
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onEditingFinished()
        }
    }

    interface OnEditingFinishedListener {

        fun onEditingFinished()
    }

}

