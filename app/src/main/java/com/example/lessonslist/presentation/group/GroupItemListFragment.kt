package com.example.lessonslist.presentation.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lessonslist.databinding.FragmentGroupItemListBinding
import java.lang.RuntimeException

class GroupItemListFragment: Fragment() {

    private var _binding: FragmentGroupItemListBinding? = null
    private val binding: FragmentGroupItemListBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemListBinding == null")
    /*           _binding = FragmentGroupItemBinding.inflate(inflater, container, false)
        return binding.root

*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

}