package com.example.timeplantest.weight.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.example.timeplantest.R
import com.example.timeplantest.bean.TaskBean
import com.example.timeplantest.weight.RoundCornerView
import com.ndhzs.timeselectview.bean.TSViewTaskBean

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/3
 *@description
 */
class NameDialog(context: Context,
                 themeResId: Int,
                 taskBean: TaskBean,
                 tSViewBean: TSViewTaskBean,
                 onClose: (taskBean: TaskBean, tSViewTaskBean: TSViewTaskBean) -> Unit) : Dialog(context, themeResId), View.OnClickListener {

    private val mTaskBean = taskBean
    private val mTSViewTaskBean = tSViewBean
    private lateinit var mImgHead: ImageView
    private lateinit var mTvTime: TextView
    private lateinit var mEtName: EditText
    private lateinit var mEtDescribe: EditText
    private lateinit var mTvBorderColor: TextView
    private lateinit var mTvInsideColor: TextView
    private lateinit var mEdSetGroup: EditText
    private lateinit var mBtnBack: Button
    private lateinit var mBtnFinish: Button
    private lateinit var mColorBorder: RoundCornerView
    private lateinit var mColorInside: RoundCornerView
    private val mOnClose = onClose

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dlg_set_name)
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
        mImgHead = findViewById(R.id.img_head)
        mTvTime = findViewById(R.id.tv_time)
        mEtName = findViewById(R.id.et_name)
        mEtDescribe = findViewById(R.id.et_describe)
        mTvBorderColor = findViewById(R.id.tv_color_border)
        mTvInsideColor = findViewById(R.id.tv_color_inside)
        mEdSetGroup = findViewById(R.id.tv_set_group)
        mBtnBack = findViewById(R.id.btn_back)
        mBtnFinish = findViewById(R.id.btn_finish)
        mColorBorder = findViewById(R.id.view_color_border)
        mColorInside = findViewById(R.id.view_color_inside)
        val time = getDate(mTaskBean.date) + "，${mTaskBean.startTime}-${mTaskBean.endTime}"
        mTvTime.text = time
        val name = mTaskBean.name
        if (name != "点击设置") {
            mEtName.setText(name)
        }
        val describe = mTaskBean.describe
        if (describe.isNotEmpty()) {
            mEtDescribe.setText(describe)
        }
        mColorBorder.setColor(mTaskBean.borderColor)
        mColorInside.setColor(mTaskBean.insideColor)

        if (mTaskBean.groupName.isNotEmpty()) {
            mEdSetGroup.setText(mTaskBean.groupName)
        }
    }

    private fun initEvent() {
        mTvBorderColor.setOnClickListener(this)
        mTvInsideColor.setOnClickListener(this)
        mEdSetGroup.setOnClickListener(this)
        mBtnFinish.setOnClickListener(this)
        mBtnBack.setOnClickListener(this)

        mColorBorder.setOnClick {
            ColorDialog(context, R.style.dialog) { color ->
                mColorBorder.setColor(color)
            }.show()
        }
        mColorInside.setOnClick {
            ColorDialog(context, R.style.dialog) { color ->
                mColorInside.setColor(color)
            }.show()
        }
    }

    private fun getDate(date: String): String {
        val time = date.split("-")
        return "${time[0]}年${time[1]}月${time[2]}日"
    }

    private fun close() {
        val name = mEtName.text.toString()
        val describe = mEtDescribe.text.toString()
        val groupName = mEdSetGroup.text.toString()
        if (name.isNotEmpty()) {
            mTaskBean.name = name
            mTaskBean.describe = describe
            mTaskBean.groupName = groupName
            mTaskBean.borderColor = mColorBorder.getColor()
            mTaskBean.insideColor = mColorInside.getColor()
            mTaskBean.save()
            mTSViewTaskBean.name = name
            mTSViewTaskBean.borderColor = mColorBorder.getColor()
            mTSViewTaskBean.insideColor = mColorInside.getColor()
            mTSViewTaskBean.any2 = describe
            mOnClose.invoke(mTaskBean, mTSViewTaskBean)
            dismiss()
        }else {
            Toast.makeText(context, "请输入任务名称！", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_back -> {
                dismiss()
            }
            R.id.btn_finish -> {
                close()
            }
            R.id.tv_color_border -> {
                ColorDialog(context, R.style.dialog) { color ->
                    mColorBorder.setColor(color)
                }.show()
            }
            R.id.tv_color_inside -> {
                ColorDialog(context, R.style.dialog) { color ->
                    mColorInside.setColor(color)
                }.show()
            }
        }
    }
}