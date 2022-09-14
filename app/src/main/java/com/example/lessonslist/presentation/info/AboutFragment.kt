package com.example.lessonslist.presentation.info

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentAboutBinding
import com.example.lessonslist.databinding.FragmentPaymentItemBinding



class AboutFragment: Fragment() {




    private var _binding: FragmentAboutBinding? = null
    private val binding: FragmentAboutBinding
        get() = _binding ?: throw RuntimeException("FragmentAboutBinding == null")




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "О приложение"
    }

}