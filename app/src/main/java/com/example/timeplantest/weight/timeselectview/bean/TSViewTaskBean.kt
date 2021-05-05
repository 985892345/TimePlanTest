package com.example.timeplantest.weight.timeselectview.bean

/**
 * @author 985892345
 * @date 2021/3/20
 * @description
 */
data class TSViewTaskBean(val date: String,
                          var name: String,
                          var startTime: String,
                          var endTime: String,
                          var diffTime: String,
                          var borderColor: Int,
                          var insideColor: Int,
                          var any1: Any? = null,
                          var any2: Any? = null)