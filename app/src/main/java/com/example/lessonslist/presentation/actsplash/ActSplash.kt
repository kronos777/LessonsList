package com.example.lessonslist.presentation.actsplash

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.lessonslist.R
import com.example.lessonslist.presentation.MainActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule


class ActSplash : AppCompatActivity() {

    private val openMainAct = MutableLiveData<Boolean>()
    private var timer: TimerTask? = null
    private val viewModel by lazy {
        ViewModelProvider(this)[DateItemViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_splash)
        setCleanDisplay()
        showProgressBarOrNot()
    }

    private fun setCleanDisplay() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    private fun showProgressBarOrNot() {
        viewModel.checkExistsDateItem(currentDate().toString())
        viewModel.dateItem.observe(this) {
            if(it!=null) {
                onFromSplash(1500)
                goMain()
                return@observe
            } else {
                viewModel.addDateItem(currentDate().toString())
                deletePrevDate()
                onFromSplash(6000)
                return@observe
            }
        }
    }


    private fun deletePrevDate() {
        val prevDate = currentDate()!!.minusDays(1)
        viewModel.deleteDateItem(prevDate.toString())
    }

    private fun currentDate(): LocalDate? {
        val formatter = DateTimeFormatter.ofPattern("yyyy/M/d")
        val current = LocalDate.now()
        val formatted = current.format(formatter)
        return LocalDate.parse(formatted, formatter)
    }


    private fun onFromSplash(valueSleep: Long) {
        if (timer == null) {
            timer = Timer().schedule(valueSleep) {
                openMainAct.postValue(true)
                goMain()
            }
        }
    }

    private fun goMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}