package com.example.timeplantest.weight.timeselectview.viewinterface

import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.timeplantest.weight.timeselectview.bean.TSViewTaskBean

/**
 * @author 985892345
 * @email 2767465918@qq.com
 * @date 2021/4/4
 * @description [com.example.timeplantest.weight.timeselectview.TimeSelectView]
 */
interface IVpLayout {
    fun addBackCardView(lp: ViewGroup.LayoutParams, v: ViewGroup)
    fun addTimeScrollView(lp: ViewGroup.LayoutParams, v: ViewGroup, viewPager2: ViewPager2?)
    fun showNowTimeLine()
    fun onViewDetachedFromWindow()
    fun onViewRecycled()
    fun notifyAllRectRefresh()
    fun initializeBean(taskBeans: MutableList<TSViewTaskBean>)
    fun backCurrentTime()
    fun timeLineScrollTo(scrollY: Int)
    fun timeLineSlowlyScrollTo(scrollY: Int)
    fun notifyRectViewDataChanged()
}