package com.example.lessonslist.presentation.helpers

import androidx.navigation.NavOptions
import com.example.lessonslist.R

class NavigationOptions {
    fun animationOptions(): NavOptions {
        return NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()
    }
}