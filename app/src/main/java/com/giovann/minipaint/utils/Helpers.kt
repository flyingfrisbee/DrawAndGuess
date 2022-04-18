package com.giovann.minipaint.utils

import android.view.View

object Helpers {

    fun View.showView() {
        visibility = View.VISIBLE
    }

    fun View.hideView() {
        visibility = View.INVISIBLE
    }

    fun View.enable() {
        isEnabled = true
    }

    fun View.disable() {
        isEnabled = false
    }
}