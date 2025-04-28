package com.example.myapplication.utils

import android.content.res.Resources
import android.view.View

fun Int.dpToPx() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun switchVisibility(visibleView: View, vararg goneViews: View) {
    visibleView.visibility = View.VISIBLE
    goneViews.forEach {
        it.visibility = View.GONE
    }
}