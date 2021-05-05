package com.example.timeplantest.weight.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.timeplantest.R

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/4
 *@description
 */
class TimePickerDialog(context: Context, themeResId: Int, initialHour: Int, initialMinute: Int, onClose: ((hour: Int, minute: Int) -> Unit)) : Dialog(context, themeResId) {

    private val mOnClose = onClose
    private val mInitialHour = initialHour
    private val mInitialMinute = initialMinute

    private lateinit var mTimePicker: TimePicker
    private lateinit var mBtnFinish: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dlg_time_picker)
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
    }

    private fun initView() {
        mTimePicker = findViewById(R.id.dlg_time_picker)
        mTimePicker.setIs24HourView(true)
        mTimePicker.currentHour = mInitialHour
        mTimePicker.currentMinute = mInitialMinute

        mBtnFinish = findViewById(R.id.dlg_time_picker_finish)
        mBtnFinish.setOnClickListener {
            mOnClose.invoke(mTimePicker.currentHour, mTimePicker.currentMinute)
            dismiss()
        }
    }

    override fun onBackPressed() {
        mOnClose.invoke(mTimePicker.currentHour, mTimePicker.currentMinute)
        super.onBackPressed()
    }
}