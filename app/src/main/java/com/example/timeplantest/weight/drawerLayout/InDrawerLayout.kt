package com.example.timeplantest.weight.drawerLayout

import android.app.Activity
import android.graphics.Color
import android.view.View
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import com.example.timeplantest.R
import kotlin.math.pow
import kotlin.math.sqrt

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/3
 *@description
 */
class InDrawerLayout(activity: Activity, drawerLayout: DrawerLayout) {

    private val mActivity = activity
    private val mDrawerLayout = drawerLayout

    init {
        initDrawerLayout()
    }

    private fun initDrawerLayout() {

        //隐藏状态栏时，获取状态栏高度
        val resourceId = mActivity.resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = mActivity.resources.getDimensionPixelSize(resourceId)
        //初始化状态栏的高度
        val statusBar = mActivity.findViewById<View>(R.id.main_statusBar)
        val params = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            statusBarHeight
        )
        statusBar.layoutParams = params
        //隐藏状态栏
        val window = mActivity.window

        val option = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }else {
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        window.decorView.systemUiVisibility = option
        window.statusBarColor = Color.TRANSPARENT

        //蒙层颜色
        val content = mDrawerLayout.getChildAt(0) as CardView
        mDrawerLayout.setScrimColor(Color.TRANSPARENT)
        mDrawerLayout.addDrawerListener(object : DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // 滑动的过程中执行 slideOffset：从0 ~ 1
                // 侧边栏
                val scale = 1 - slideOffset //1 ~ 0
                val leftScale = 1 - 0.4f * scale //0.6 ~ 1.0
                val leftAlpha = 0.4f + 0.6f * slideOffset //0.4 ~ 1.0
                val rightScale = 0.6f + 0.4f * scale //1.0 ~ 0.6
                val rightAlpha = 0.4f + 0.6f * scale //1.0 ~ 0.4
                val rotation = -5 * slideOffset //设置旋转0 ~ -5
                val radius = if (slideOffset < 0.2) (slideOffset * 5).pow(2) * 40 else slideOffset * 25 + 35 //设置圆角0 ~ 60
                drawerView.scaleX = leftScale
                drawerView.scaleY = leftScale
                drawerView.alpha = leftAlpha
                content.scaleX = rightScale
                content.scaleY = rightScale
                content.alpha = rightAlpha
                content.rotationY = rotation
                content.translationX = 500 * slideOffset //0~width
                content.radius = radius
            }

            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
        })
    }
}