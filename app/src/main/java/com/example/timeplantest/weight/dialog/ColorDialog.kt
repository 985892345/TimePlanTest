package com.example.timeplantest.weight.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.timeplantest.R
import com.example.timeplantest.weight.RoundCornerView

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/4
 *@description
 */
class ColorDialog(context: Context, themeResId: Int, onClose: (color: Int) -> Unit) : Dialog(context, themeResId) {

    private lateinit var mDefaultBorderColorView: RoundCornerView
    private lateinit var mDefaultInsideColorView: RoundCornerView
    private lateinit var mColorView1: RoundCornerView
    private lateinit var mColorView2: RoundCornerView
    private lateinit var mColorView3: RoundCornerView
    private lateinit var mColorView4: RoundCornerView
    private lateinit var mColorView5: RoundCornerView
    private lateinit var mColorView6: RoundCornerView
    private lateinit var mColorView7: RoundCornerView
    private lateinit var mColorView8: RoundCornerView
    private lateinit var mColorView9: RoundCornerView
    private lateinit var mColorView10: RoundCornerView
    private lateinit var mColorView11: RoundCornerView
    private lateinit var mColorView12: RoundCornerView
    private lateinit var mColorView13: RoundCornerView
    private lateinit var mColorView14: RoundCornerView
    private lateinit var mColorView15: RoundCornerView

    private val mOnClose = onClose

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dlg_color)
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
        mDefaultBorderColorView = findViewById(R.id.dlg_default_border_color)
        mDefaultInsideColorView = findViewById(R.id.dlg_default_inside_color)
        mColorView1 = findViewById(R.id.dlg_color1)
        mColorView2 = findViewById(R.id.dlg_color2)
        mColorView3 = findViewById(R.id.dlg_color3)
        mColorView4 = findViewById(R.id.dlg_color4)
        mColorView5 = findViewById(R.id.dlg_color5)
        mColorView6 = findViewById(R.id.dlg_color6)
        mColorView7 = findViewById(R.id.dlg_color7)
        mColorView8 = findViewById(R.id.dlg_color8)
        mColorView9 = findViewById(R.id.dlg_color9)
        mColorView10 = findViewById(R.id.dlg_color10)
        mColorView11 = findViewById(R.id.dlg_color11)
        mColorView12 = findViewById(R.id.dlg_color12)
        mColorView13 = findViewById(R.id.dlg_color13)
        mColorView14 = findViewById(R.id.dlg_color14)
        mColorView15 = findViewById(R.id.dlg_color15)

        mDefaultBorderColorView.setColor(ContextCompat.getColor(context, R.color.border_color))
        mDefaultInsideColorView.setColor(ContextCompat.getColor(context, R.color.inside_color))
        mColorView1.setColor(ContextCompat.getColor(context, R.color.color1))
        mColorView2.setColor(ContextCompat.getColor(context, R.color.color2))
        mColorView3.setColor(ContextCompat.getColor(context, R.color.color3))
        mColorView4.setColor(ContextCompat.getColor(context, R.color.color4))
        mColorView5.setColor(ContextCompat.getColor(context, R.color.color5))
        mColorView6.setColor(ContextCompat.getColor(context, R.color.color6))
        mColorView7.setColor(ContextCompat.getColor(context, R.color.color7))
        mColorView8.setColor(ContextCompat.getColor(context, R.color.color8))
        mColorView9.setColor(ContextCompat.getColor(context, R.color.color9))
        mColorView10.setColor(ContextCompat.getColor(context, R.color.color10))
        mColorView11.setColor(ContextCompat.getColor(context, R.color.color11))
        mColorView12.setColor(ContextCompat.getColor(context, R.color.color12))
        mColorView13.setColor(ContextCompat.getColor(context, R.color.color13))
        mColorView14.setColor(ContextCompat.getColor(context, R.color.color14))
        mColorView15.setColor(ContextCompat.getColor(context, R.color.color15))
    }

    private fun initEvent() {
        val onClick = { color: Int ->
            mOnClose.invoke(color)
            dismiss()
        }

        mDefaultBorderColorView.setOnClick(onClick)
        mDefaultInsideColorView.setOnClick(onClick)
        mColorView1.setOnClick(onClick)
        mColorView2.setOnClick(onClick)
        mColorView3.setOnClick(onClick)
        mColorView4.setOnClick(onClick)
        mColorView5.setOnClick(onClick)
        mColorView6.setOnClick(onClick)
        mColorView7.setOnClick(onClick)
        mColorView8.setOnClick(onClick)
        mColorView9.setOnClick(onClick)
        mColorView10.setOnClick(onClick)
        mColorView11.setOnClick(onClick)
        mColorView12.setOnClick(onClick)
        mColorView13.setOnClick(onClick)
        mColorView14.setOnClick(onClick)
        mColorView15.setOnClick(onClick)
    }
}