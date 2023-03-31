package com.example.lessonslist.presentation.info

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentInstructionBinding



class InstructionFragment: Fragment() {


    private var _binding: FragmentInstructionBinding? = null
    private val binding: FragmentInstructionBinding
        get() = _binding ?: throw RuntimeException("FragmentInstructionBinding == null")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInstructionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "Инструкция"


    }


}