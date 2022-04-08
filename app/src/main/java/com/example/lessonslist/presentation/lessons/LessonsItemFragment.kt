package com.example.lessonslist.presentation.lessons

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lessonslist.databinding.FragmentLessonsItemBinding
import com.example.lessonslist.domain.group.GroupItem
import com.example.lessonslist.domain.lessons.LessonsItem


class LessonsItemFragment : Fragment() {

    private lateinit var viewModel: LessonsItemViewModel
    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentLessonsItemBinding? = null
    private val binding: FragmentLessonsItemBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemBinding == null")

    private var screenMode: String = MODE_UNKNOWN
    private var lessonsItemId: Int = LessonsItem.UNDEFINED_ID


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
    ): View? {
     //   return inflater.inflate(R.layout.fragment_group_item, container, false)
        _binding = FragmentLessonsItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[LessonsItemViewModel::class.java]
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
           // else -> launchEditMode()
        }
    }


    private fun launchEditMode() {
        viewModel.getLessonsItem(lessonsItemId)
        binding.saveButton.setOnClickListener{
            viewModel.editLessonsItem(
                binding.etTitle.text.toString(),
                binding.etDescription.text.toString(),
                binding.etStudent.text.toString(),
                binding.etPrice.text.toString(),
                binding.etDatestart.text.toString(),
                binding.etDateend.text.toString()
            )
        }
    }

    private fun launchAddMode() {
        binding.saveButton.setOnClickListener{
            viewModel.addLessonsItem(
                binding.etTitle.text.toString(),
                binding.etDescription.text.toString(),
                binding.etStudent.text.toString(),
                binding.etPrice.text.toString(),
                binding.etDatestart.text.toString(),
                binding.etDateend.text.toString()
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
            if (!args.containsKey(LESSONS_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            lessonsItemId = args.getInt(LESSONS_ITEM_ID, GroupItem.UNDEFINED_ID)
        }
    }
    companion object {

        private const val SCREEN_MODE = "extra_mode"
        private const val LESSONS_ITEM_ID = "extra_lessons_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""

        fun newInstanceAddItem(): LessonsItemFragment {
            return LessonsItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(lessonsItemId: Int): LessonsItemFragment {
            return LessonsItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(LESSONS_ITEM_ID, lessonsItemId)
                }
            }
        }
    }
}

