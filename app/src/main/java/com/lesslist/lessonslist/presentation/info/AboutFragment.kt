package com.lesslist.lessonslist.presentation.info

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.lesslist.lessonslist.R
import com.lesslist.lessonslist.databinding.FragmentAboutBinding


class AboutFragment: Fragment() {




    private var _binding: FragmentAboutBinding? = null
    private val binding: FragmentAboutBinding
        get() = _binding ?: throw RuntimeException("FragmentAboutBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "О приложение"

        binding.tilEmail.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "creatorweb77@gmail.com", null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Список занятий - вопрос/предложение")
            try {
                startActivity(Intent.createChooser(emailIntent, "Отправить сообщение..."))
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    activity,
                    "Почтовые клиенты не установлены.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}