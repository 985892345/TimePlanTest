package com.example.timeplantest.weight.timeselectview.bean

import java.text.SimpleDateFormat
import java.util.*

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/4/25
 *@description
 */
class TSViewDayBean {

    var date: String
    var tSViewTaskBeans: MutableList<TSViewTaskBean>

    constructor(date: String) {
        this.date = date
        tSViewTaskBeans = LinkedList()
    }

    constructor(date: Date) {
        val sdf = SimpleDateFormat("yyyy-M-d")
        this.date = sdf.format(date)
        tSViewTaskBeans = LinkedList()
    }

    constructor(day: String, taskBeans: MutableList<TSViewTaskBean>) {
        this.date = day
        this.tSViewTaskBeans = taskBeans
    }

    fun size(): Int {
        return tSViewTaskBeans.size
    }
}