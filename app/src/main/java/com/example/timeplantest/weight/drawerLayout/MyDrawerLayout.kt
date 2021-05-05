package com.example.timeplantest.weight.drawerLayout

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/3
 *@description
 */
class MyDrawerLayout(context: Context, attrs: AttributeSet?) : DrawerLayout(context, attrs) {

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (isDrawerOpen(GravityCompat.START) && ev.x > width / 2.0f) {
            true
        } else super.onInterceptTouchEvent(ev)
    }
}