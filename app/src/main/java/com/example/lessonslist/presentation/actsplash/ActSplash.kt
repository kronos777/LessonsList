package com.example.lessonslist.presentation.actsplash

import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.widget.ImageView
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
    lateinit var viewModel: DateItemViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_splash)
       // val imgSplash = findViewById<ImageView>(R.id.actSplash)
        //imgSplash.colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f)})
        showProgressBarOrNot()
    }

    private fun showProgressBarOrNot() {
        viewModel = ViewModelProvider(this)[DateItemViewModel::class.java]
        viewModel.checkExistsDateItem(currentDate().toString())
        viewModel.dateItem.observe(this) {
            if(it!=null) {
                goMain()
                return@observe
            } else {
                viewModel.addDateItem(currentDate().toString())
                deletePrevDate()
                onFromSplash()
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


    private fun onFromSplash() {
        if (timer == null) {
            timer = Timer().schedule(4000) {
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