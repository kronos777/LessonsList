package com.example.lessonslist.presentation.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentInstructionBinding
import com.github.florent37.expansionpanel.ExpansionLayout
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection


class InstructionFragment: Fragment() {


    private var _binding: FragmentInstructionBinding? = null
    private val binding: FragmentInstructionBinding
        get() = _binding ?: throw RuntimeException("FragmentInstructionBinding == null")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInstructionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "Инструкция"


        val expansionLayout: ExpansionLayout = binding.expansionLayout
        val expansionLayout2: ExpansionLayout = binding.expansionLayout2
       // val expansionLayout3: ExpansionLayout = binding.expansionLayout3
        val expansionLayoutCollection = ExpansionLayoutCollection()
        expansionLayoutCollection.add(expansionLayout)
        expansionLayoutCollection.add(expansionLayout2)
        //expansionLayoutCollection.add(expansionLayout3)


        binding.accordionDescription1.text = "1/1 После добавления учеников и групп создайте урок, в случае создания урока с главного экрана нажмите и удерживайте дату появится экран создания урока дата будет та что Вы выбрали, установите время  начала и конца урока выберите студентов, установите цену и нажмите сохранить."
        binding.accordionDescription2.text = "1/1 После добавления учеников и групп создайте урок, в случае создания урока с главного экрана нажмите и удерживайте дату появится экран создания урока дата будет та что Вы выбрали, установите время  начала и конца урока выберите студентов, установите цену и нажмите сохранить."

    }


}