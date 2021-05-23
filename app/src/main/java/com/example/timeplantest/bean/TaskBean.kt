package com.example.timeplantest.bean

import com.ndhzs.timeselectview.bean.TSViewTaskBean
import org.litepal.crud.LitePalSupport

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/4
 *@description
 */
class TaskBean: LitePalSupport() {

    var date = ""
    var name = ""
    var startTime = ""
    var endTime = ""
    var diffTime = ""
    var borderColor = 0
    var insideColor = 0
    var describe = ""
    var groupName = ""
    var isFinish = false

    fun update(tsViewTaskBean: TSViewTaskBean) {
        date = tsViewTaskBean.date
        name = tsViewTaskBean.name
        startTime = tsViewTaskBean.startTime
        endTime = tsViewTaskBean.endTime
        diffTime = tsViewTaskBean.diffTime
        borderColor = tsViewTaskBean.borderColor
        insideColor = tsViewTaskBean.insideColor
        describe = if (tsViewTaskBean.any2 == null) {
            ""
        }else {
            tsViewTaskBean.any2 as String
        }
        tsViewTaskBean.any1 = this
    }

    fun obtainTSViewTaskBean(): TSViewTaskBean {
        return TSViewTaskBean(date, name, startTime, endTime, diffTime, borderColor, insideColor, this, describe)
    }
}