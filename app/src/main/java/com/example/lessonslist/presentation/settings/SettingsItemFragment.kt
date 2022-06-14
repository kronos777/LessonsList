package com.example.lessonslist.presentation.settings


import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.lessonslist.data.AppDatabase
import com.example.lessonslist.databinding.FragmentSettingsItemBinding
import com.example.lessonslist.presentation.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.raphaelebner.roomdatabasebackup.core.RoomBackup


class SettingsItemFragment : Fragment() {

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentSettingsItemBinding? = null
    private val binding: FragmentSettingsItemBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemBinding == null")

    private val appDatabase = AppDatabase
    private lateinit var contextForDump: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        parseParams()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        contextForDump = context

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
        _binding = FragmentSettingsItemBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Настройки"




        binding.switch1.setOnClickListener {
            if (binding.switch1.isChecked) {
                log(binding.switch1.text.toString() + " Включено")
            } else {
                log(binding.switch1.text.toString() + " Выключено ")
            }
        }
      //  val appContext = this.context?.applicationContext



        binding.backupButton.setOnClickListener {
           //val activity = (activity as AppCompatActivity).requireActivity()
            //val fragmentActivity = (activity as SettingsItemFragment)

        }

}




    interface OnEditingFinishedListener {
        fun onEditingFinished()
    }

    private fun log(message: String) {
        Log.d("SERVICE_TAG", "SwithSettings: $message")
    }

}

