package com.example.timeplantest.weight.timeselectview.viewinterface

import com.example.timeplantest.weight.timeselectview.bean.TSViewTaskBean

/**
 * @author 985892345
 * @email 2767465918@qq.com
 * @date 2021/4/4
 * @description [com.example.timeplantest.weight.timeselectview.utils.rect.RectManger]
 */
interface IRectManger {
    fun getClickUpperLimit(): Int
    fun getClickLowerLimit(): Int
    fun getUpperLimit(insideY: Int, position: Int): Int
    fun getLowerLimit(insideY: Int, position: Int): Int
    fun getBean(insideY: Int, position: Int): TSViewTaskBean?
    fun getDeletedBean(): TSViewTaskBean
    fun addBean(taskBean: TSViewTaskBean)
    fun deleteBean(taskBean: TSViewTaskBean)

    /**
     * 判断长按的情况，并求出此时上下边界、记录长按的起始点、在数组中删除Rect和Bean
     *
     * 调用该方法后可以使用RectManger的getDeletedRect()、getDeletedBean()、getUpperLimit()、getLowerLimit()方法
     */
    fun longClickConditionJudge(insideY: Int, position: Int)
}