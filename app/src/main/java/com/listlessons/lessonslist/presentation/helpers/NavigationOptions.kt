package com.listlessons.lessonslist.presentation.helpers

import androidx.navigation.NavOptions
import com.listlessons.lessonslist.R

class NavigationOptions {
    operator fun invoke(): NavOptions {
        return NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()
    }
}