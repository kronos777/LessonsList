package com.example.lessonslist.presentation.group

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lessonslist.databinding.FragmentGroupItemBinding
import com.example.lessonslist.domain.group.GroupItem
import com.example.lessonslist.domain.student.StudentItem
import com.example.lessonslist.presentation.student.StudentItemFragment


class GroupItemFragment : Fragment() {

    private lateinit var viewModel: GroupItemViewModel
    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentGroupItemBinding? = null
    private val binding: FragmentGroupItemBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemBinding == null")

    private var screenMode: String = MODE_UNKNOWN
    private var groupItemId: Int = GroupItem.UNDEFINED_ID



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
        launchRightMode()
        observeViewModel()
    }

    private fun addTextChangeListeners() {
        TODO("Not yet implemented")
    }


    private fun launchRightMode() {
        Log.d("screenMode", screenMode)
        when (screenMode) {
            MODE_EDIT -> launchEditMode()
            MODE_ADD -> launchAddMode()
            else -> launchEditMode()
        }
    }


    private fun launchEditMode() {
        viewModel.getGroupItem(groupItemId)
        binding.saveButton.setOnClickListener{
            viewModel.editGroupItem(
                binding.etTitle.text.toString(),
                binding.etDescription.text.toString(),
                binding.etStudent.text.toString()
            )
        }
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

    private fun observeViewModel() {
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onEditingFinished()
        }
    }

    interface OnEditingFinishedListener {

        fun onEditingFinished()
    }

    companion object {

        private const val SCREEN_MODE = "extra_mode"
        private const val GROUP_ITEM_ID = "extra_group_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""

        fun newInstanceAddItem(): GroupItemFragment {
            return GroupItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(groupItemId: Int): GroupItemFragment {
            return GroupItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(GROUP_ITEM_ID, groupItemId)
                }
            }
        }
    }
}

