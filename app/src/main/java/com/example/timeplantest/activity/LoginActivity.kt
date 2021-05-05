package com.example.timeplantest.activity

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import com.example.timeplantest.R
import com.example.timeplantest.logintextwacther.BaseTextWatcher
import com.example.timeplantest.weight.dialog.RegisterDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.lang.Thread.sleep
import java.util.*

class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mBtnForgetPassword: Button
    private lateinit var mBtnRegister: Button
    private lateinit var mBtnLogin: Button
    private lateinit var mBtnQQ: Button
    private lateinit var mBtnWechat: Button
    private lateinit var mCbRemember: CheckBox
    private lateinit var mTilUsername: TextInputLayout
    private lateinit var mTilPassword: TextInputLayout
    private lateinit var mEtUsername: TextInputEditText
    private lateinit var mEtPassword: TextInputEditText

    private lateinit var mShared: SharedPreferences
    private lateinit var mRegisterDialog: RegisterDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        initEvent()
    }

    private fun initView() {
        mBtnForgetPassword = findViewById(R.id.login_btn_forgetPassword)
        mBtnRegister = findViewById(R.id.login_btn_register)
        mBtnLogin = findViewById(R.id.login_btn_login)
        mBtnQQ = findViewById(R.id.login_btn_qq)
        mBtnWechat = findViewById(R.id.login_btn_wechat)
        mCbRemember = findViewById(R.id.login_cb_remember)
        mTilUsername = findViewById(R.id.login_til_username)
        mTilPassword = findViewById(R.id.login_til_password)
        mEtUsername = findViewById(R.id.login_et_username)
        mEtPassword = findViewById(R.id.login_et_password)
    }

    private fun initEvent() {
        mShared = getSharedPreferences("login", MODE_PRIVATE)
        mBtnForgetPassword.setOnClickListener(this)
        mBtnRegister.setOnClickListener(this)
        mBtnLogin.setOnClickListener(this)
        mBtnQQ.setOnClickListener(this)
        mBtnWechat.setOnClickListener(this)
        mCbRemember.setOnCheckedChangeListener { _, isChecked ->
            val editor = mShared.edit()
            editor.putBoolean("remember_password", isChecked)
            editor.apply()
        }
        mEtUsername.addTextChangedListener(BaseTextWatcher(mTilUsername))
        mEtPassword.addTextChangedListener(BaseTextWatcher(mTilPassword))

        //记录是否记住密码
        mCbRemember.isChecked = mShared.getBoolean("remember_password", false)
        if (mCbRemember.isChecked) {
            val username = mShared.getString("username", null)
            if (username != null) {
                mEtUsername.setText(username)
                mEtPassword.setText(mShared.getString("password", null))
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.login_btn_forgetPassword -> Toast.makeText(this, "暂时不能改密码！", Toast.LENGTH_SHORT)
                .show()
            R.id.login_btn_register -> {
                mRegisterDialog = RegisterDialog(this@LoginActivity, R.style.dialog) { username, password ->
                    sharedEditor(username, password)
                    mEtUsername.setText(username)
                    mEtPassword.setText(password)
                    mRegisterDialog.dismiss()
                }
                mRegisterDialog.show()
            }
            R.id.login_btn_login -> {
                val username = mEtUsername.text.toString()
                val password = mEtPassword.text.toString()
                if (username == "" || password == "") {
                    Toast.makeText(this, "请输入完整！", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this, "请求登陆中，请耐心等待", Toast.LENGTH_SHORT).show()
                    sleep(1000)
                    val correctPassword = mShared.getString(username, null)
                    if (correctPassword == null) {
                        Toast.makeText(this, "该账号并未注册！", Toast.LENGTH_LONG).show()
                    }else if (correctPassword != password) {
                        Toast.makeText(this, "账号或者密码错误！", Toast.LENGTH_LONG).show()
                    }else {
                        Toast.makeText(this, "欢迎回来！", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
            R.id.login_btn_qq, R.id.login_btn_wechat -> Toast.makeText(
                this,
                "好看用的，暂未实现",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //写进SharedPreferences
    private fun sharedEditor(username: String?, password: String?) {
        val editor = mShared.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.apply()
    }
}