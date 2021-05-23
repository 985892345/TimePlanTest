package com.example.timeplantest.bean

import com.ndhzs.timeselectview.bean.TSViewTaskBean

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/4
 *@description
 */
class ExpandBean(var groupName: String) {
    val children = ArrayList<String>()
    val time = ArrayList<String>()
    val tSViewBean = ArrayList<TSViewTaskBean>()
    val isFinish = ArrayList<Boolean>()
}