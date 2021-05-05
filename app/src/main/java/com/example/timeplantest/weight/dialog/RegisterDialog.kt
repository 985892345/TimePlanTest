package com.example.timeplantest.weight.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timeplantest.R
import com.example.timeplantest.logintextwacther.BaseTextWatcher
import com.example.timeplantest.logintextwacther.Password1Watcher
import com.example.timeplantest.logintextwacther.Password2Watcher
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/3
 *@description
 */
class RegisterDialog(
    context: Context,
    themeResId: Int,
    onClose: (username: String, password: String) -> Unit
) : Dialog(context, themeResId), View.OnClickListener {

    private var mContext = context
    private lateinit var mTilUsername: TextInputLayout
    private lateinit var mTilPassword1: TextInputLayout
    private lateinit var mTilPassword2: TextInputLayout
    private lateinit var mTilPhone: TextInputLayout
    private lateinit var mEtUsername: TextInputEditText
    private lateinit var mEtPassword1: TextInputEditText
    private lateinit var mEtPassword2: TextInputEditText
    private lateinit var mEtPhone: TextInputEditText
    private lateinit var mCbAgreement: CheckBox
    private lateinit var mBtnRegister: Button
    private val mListener = onClose

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dlg_register)
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
        mTilUsername = findViewById(R.id.register_til_username)
        mTilPassword1 = findViewById(R.id.register_til_password1)
        mTilPassword2 = findViewById(R.id.register_til_password2)
        mTilPhone = findViewById(R.id.register_til_phone)
        mEtUsername = findViewById(R.id.register_et_username)
        mEtPassword1 = findViewById(R.id.register_et_password1)
        mEtPassword2 = findViewById(R.id.register_et_password2)
        mEtPhone = findViewById(R.id.register_et_phone)
        mCbAgreement = findViewById(R.id.register_cb_agreement)
        mBtnRegister = findViewById(R.id.register_btn_register)
    }

    private fun initEvent() {
        mEtUsername.addTextChangedListener(BaseTextWatcher(mTilUsername))
        mEtPassword1.addTextChangedListener(Password1Watcher(mTilPassword1))
        mEtPassword2.addTextChangedListener(
            Password2Watcher(
                mEtPassword1.text.toString(),
                mTilPassword1,
                mTilPassword2
            )
        )
        mEtPhone.addTextChangedListener(BaseTextWatcher(mTilPhone))
        mBtnRegister.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.register_btn_register -> {
                val username = mEtUsername.text.toString()
                val password = mEtPassword1.text.toString()
                val phoneNum = mEtPhone.text.toString()
                if (username == "") {
                    mTilUsername.error = "用户名不能为空！"
                }else if (password == "") {
                    mTilPassword1.error = "密码不能为空！"
                }else if (phoneNum.length != 11) {
                    mTilPhone.error = "号码尾数不为11位！"
                }else {
                    val share = mContext.getSharedPreferences(
                        "login",
                        AppCompatActivity.MODE_PRIVATE
                    )
                    val hasPassword = share.getString("username", null)
                    if (hasPassword == null) {
                        Toast.makeText(mContext, "注册成功！", Toast.LENGTH_LONG).show()
                        mListener.invoke(username, password)
                    } else {
                        Toast.makeText(mContext, "该账号已经被注册！", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}