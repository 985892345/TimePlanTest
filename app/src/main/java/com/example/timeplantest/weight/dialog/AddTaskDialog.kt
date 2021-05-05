package com.example.timeplantest.weight.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.timeplantest.R
import com.example.timeplantest.bean.TaskBean
import com.example.timeplantest.weight.timeselectview.bean.TSViewTaskBean
import java.text.SimpleDateFormat
import java.util.*

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/4
 *@description
 */
class AddTaskDialog(context: Context,
                    themeResId: Int,
                    isRepeat: ((startTime: List<String>?, hour: Int, minute: Int, tSViewTaskBean: TSViewTaskBean?) -> Boolean),
                    taskBean: TaskBean? = null,
                    tSViewTaskBean: TSViewTaskBean? = null,
                    onClose: (taskBean: TaskBean?, tSViewTaskBean: TSViewTaskBean?) -> Unit) : Dialog(context, themeResId), View.OnClickListener {


    private val mTaskBean = taskBean
    private val mTSViewTaskBean = tSViewTaskBean
    private val mIsRepeat = isRepeat
    private val mOnClose = onClose
    private lateinit var mImgHead: ImageView
    private lateinit var mTvTime: TextView
    private lateinit var mEtName: EditText
    private lateinit var mEtDescribe: EditText
    private lateinit var mTvStartTime: TextView
    private lateinit var mTvEndTime: TextView
    private lateinit var mEtGroupName: EditText
    private lateinit var mBtnBack: Button
    private lateinit var mBtnFinish: Button
    private lateinit var mDate: String

    private var mDefaultTextColor = 0
    private var mErrorTextColor = 0xFFFF0000.toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dlg_add_task)
        //设置窗口
        val dialogWindow = window
        dialogWindow!!.setWindowAnimations(R.style.dialogAnim)
        val lp = dialogWindow.attributes
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogWindow.attributes = lp
        setCanceledOnTouchOutside(false)
        setCancelable(true)
        initView()
        initEvent()
    }

    private fun initView() {
        mImgHead = findViewById(R.id.dlg_add_img_head)
        mTvTime = findViewById(R.id.dlg_add_tv_time)
        mEtName = findViewById(R.id.dlg_add_et_name)
        mEtDescribe = findViewById(R.id.dlg_add_et_describe)
        mTvStartTime = findViewById(R.id.dlg_add_tv_start_time_show)
        mTvEndTime = findViewById(R.id.dlg_add_tv_end_time_show)
        mEtGroupName = findViewById(R.id.dlg_add_group_name)
        mBtnBack = findViewById(R.id.dlg_add_btn_back)
        mBtnFinish = findViewById(R.id.dlg_add_btn_finish)
        mDefaultTextColor = mTvStartTime.currentTextColor
        if (mTaskBean == null) {
            mTvTime.text = getDate()

            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            mTvStartTime.text = timeToString(hour, minute)
            mTvEndTime.text = timeToString(hour + 1, minute)
        }else {
            mTvTime.text = getDate(mTaskBean.date)
            mTvStartTime.text = mTaskBean.startTime
            mTvEndTime.text = mTaskBean.endTime
            mEtGroupName.setText(mTaskBean.groupName)
            val name = mTaskBean.name
            if (name != "点击设置") {
                mEtName.setText(name)
            }
            val describe = mTaskBean.describe
            if (describe.isNotEmpty()) {
                mEtDescribe.setText(describe)
            }
        }
    }

    private fun initEvent() {
        mTvStartTime.setOnClickListener(this)
        mTvEndTime.setOnClickListener(this)
        mBtnFinish.setOnClickListener(this)
        mBtnBack.setOnClickListener(this)
    }

    private fun getDate(): String {
        val sdf = SimpleDateFormat("yyyy-M-d")
        mDate = sdf.format(Date())
        return getDate(mDate)
    }

    private fun getDate(date: String): String {
        val time = date.split("-")
        return "${time[0]}年${time[1]}月${time[2]}日"
    }

    private fun close() {
        val name = mEtName.text.toString()
        val describe = mEtDescribe.text.toString()
        val groupName = mEtGroupName.text.toString()
        val startTime = mTvStartTime.text.toString()
        val endTime = mTvEndTime.text.toString()
        if (mTaskBean == null) {
            if (name.isEmpty()) {
                mOnClose.invoke(null, null)
            }else {
                val tSViewTaskBean = TSViewTaskBean(mDate,
                        name,
                        startTime,
                        endTime,
                        getDiffTime(startTime, endTime),
                        ContextCompat.getColor(context, R.color.border_color),
                        ContextCompat.getColor(context, R.color.inside_color))
                val taskBean = TaskBean()
                tSViewTaskBean.any1 = taskBean
                tSViewTaskBean.any2 = describe
                taskBean.update(tSViewTaskBean)
                taskBean.groupName = groupName
                taskBean.save()
                mOnClose.invoke(taskBean, tSViewTaskBean)
            }
        }else {
            val diffTime = getDiffTime(startTime, endTime)
            mTSViewTaskBean!!.name = name
            mTSViewTaskBean.startTime = startTime
            mTSViewTaskBean.endTime = endTime
            mTSViewTaskBean.diffTime = diffTime
            mTSViewTaskBean.any2 = describe
            mTaskBean.name = name
            mTaskBean.startTime = startTime
            mTaskBean.endTime = endTime
            mTaskBean.diffTime = diffTime
            mTaskBean.describe = describe
            mTaskBean.groupName = groupName
            mTaskBean.save()
            mOnClose.invoke(mTaskBean, mTSViewTaskBean)
        }
        dismiss()
    }

    private fun getDiffTime(startTime: String, endTime: String): String {
        val time1 = startTime.split(":")
        val time2 = endTime.split(":")
        val sH = time1[0].toInt()
        val sM = time1[1].toInt()
        val eH = time2[0].toInt()
        val eM = time2[1].toInt()
        var dH = eH - sH
        var dM = eM - sM
        if (dM < 0) {
            dM += 60
            dH--
        }
        return timeToString(dH, dM)
    }

    private fun timeToString(hour: Int, minute: Int): String {
        val stH =if (hour < 0) {
            return timeToString(hour + 24, minute)
        }else if (hour < 10) {
            "0$hour"
        }else if (hour < 24) {
            hour.toString()
        }else {
            "0${hour%24}"
        }
        val stM: String = if (minute < 10) {
            "0$minute"
        }else {
            minute.toString()
        }
        return "$stH:$stM"
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.dlg_add_btn_back -> {
                dismiss()
            }
            R.id.dlg_add_btn_finish ->
                if (mEtName.text.isEmpty()) {
                    Toast.makeText(context, "请输入任务名称！", Toast.LENGTH_SHORT).show()
                }else {
                    if (mTvStartTime.currentTextColor == mErrorTextColor || mTvEndTime.currentTextColor == mErrorTextColor) {
                        Toast.makeText(context, "不能设置重复的时间段！", Toast.LENGTH_SHORT).show()
                    }else {
                        var canClose = true
                        val sTime = mTvStartTime.text.toString()
                        val eTime = mTvEndTime.text.toString()
                        val sTimeList = sTime.split(":")
                        val eTimeList = eTime.split(":")
                        if (mIsRepeat.invoke(null, sTimeList[0].toInt(), sTimeList[1].toInt(), mTSViewTaskBean)) {
                            mTvStartTime.setTextColor(mErrorTextColor)
                            canClose = false
                        }
                        if (mIsRepeat.invoke(sTimeList, eTimeList[0].toInt(), eTimeList[1].toInt(), mTSViewTaskBean)) {
                            mTvEndTime.setTextColor(mErrorTextColor)
                            canClose = false
                        }
                        if (canClose) {
                            val sT = sTimeList[0].toInt() * 60 + sTimeList[1].toInt()
                            val eT = eTimeList[0].toInt() * 60 + eTimeList[1].toInt()
                            if (sT <= eT - 15) {
                                close()
                            }else {
                                Toast.makeText(context, "时间差最小为15分钟!", Toast.LENGTH_SHORT).show()
                            }
                        }else {
                            Toast.makeText(context, "时间出现重复！", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            R.id.dlg_add_tv_start_time_show -> {
                val time = mTvStartTime.text.toString().split(":")
                val initialHour = time[0].toInt()
                val initialMinute= time[1].toInt()
                TimePickerDialog(context, R.style.dialog, initialHour, initialMinute) { hour, minute ->
                    mTvStartTime.text = timeToString(hour, minute)
                    if (mIsRepeat.invoke(null, hour, minute, mTSViewTaskBean)) {
                        mTvStartTime.setTextColor(mErrorTextColor)
                    }else {
                        mTvStartTime.setTextColor(mDefaultTextColor)
                    }
                }.show()
            }
            R.id.dlg_add_tv_end_time_show -> {
                val time = mTvEndTime.text.toString().split(":")
                val initialHour = time[0].toInt()
                val initialMinute= time[1].toInt()
                val startTime = mTvStartTime.text.toString()
                val startTimeList = startTime.split(":")
                TimePickerDialog(context, R.style.dialog, initialHour, initialMinute) { hour, minute ->
                    mTvEndTime.text = timeToString(hour, minute)
                    if (mIsRepeat.invoke(startTimeList, hour, minute, mTSViewTaskBean)) {
                        mTvEndTime.setTextColor(mErrorTextColor)
                    }else {
                        mTvEndTime.setTextColor(mDefaultTextColor)
                    }
                }.show()
            }
        }
    }
}